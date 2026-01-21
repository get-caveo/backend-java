package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.dto.*;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentePOSService {

    private final VentePOSDao ventePOSDao;
    private final LigneVentePOSDao ligneVentePOSDao;
    private final PaiementVentePOSDao paiementVentePOSDao;
    private final ProduitDao produitDao;
    private final UniteConditionnementDao uniteConditionnementDao;
    private final ConditionnementProduitDao conditionnementProduitDao;
    private final StockService stockService;
    private final StockActuelDao stockActuelDao;

    // ==================== GESTION DES VENTES ====================

    @Transactional
    public VentePOS creerVente(Utilisateur utilisateur) {
        VentePOS vente = new VentePOS();
        vente.setNumero(genererNumeroVente());
        vente.setStatut(StatutVentePOS.BROUILLON);
        vente.setUtilisateur(utilisateur);
        vente.setMontantSousTotal(BigDecimal.ZERO);
        vente.setMontantRemise(BigDecimal.ZERO);
        vente.setMontantTotal(BigDecimal.ZERO);
        vente.setMontantPaye(BigDecimal.ZERO);

        log.info("Création d'une nouvelle vente POS: {} par {}", vente.getNumero(), utilisateur.getEmail());
        return ventePOSDao.save(vente);
    }

    public VentePOS getVente(Integer id) {
        return ventePOSDao.findByIdWithDetails(id)
                .orElseThrow(() -> GestionException.notFound("Vente POS", id));
    }

    public List<VentePOS> getVentesJour() {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        return ventePOSDao.findVentesPayeesAujourdHui(debutJour);
    }

    public List<VentePOS> getVentesBrouillon(Integer utilisateurId) {
        return ventePOSDao.findVentesBrouillonByUtilisateur(utilisateurId);
    }

    @Transactional
    public void annulerVente(Integer venteId) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() == StatutVentePOS.PAYEE) {
            throw GestionException.badRequest("Impossible d'annuler une vente déjà payée");
        }

        vente.setStatut(StatutVentePOS.ANNULEE);
        ventePOSDao.save(vente);
        log.info("Vente POS {} annulée", vente.getNumero());
    }

    // ==================== GESTION DES LIGNES ====================

    @Transactional
    public LigneVentePOS ajouterLigne(Integer venteId, LigneVentePOSDto dto) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() != StatutVentePOS.BROUILLON) {
            throw GestionException.badRequest("Impossible de modifier une vente qui n'est pas en brouillon");
        }

        Produit produit = produitDao.findById(dto.getProduitId())
                .orElseThrow(() -> GestionException.notFound("Produit", dto.getProduitId()));

        UniteConditionnement unite = uniteConditionnementDao.findById(dto.getUniteConditionnementId())
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", dto.getUniteConditionnementId()));

        // Vérifier stock disponible
        StockActuel stock = stockActuelDao.findByProduitIdAndUniteConditionnementId(
                dto.getProduitId(), dto.getUniteConditionnementId()).orElse(null);

        if (stock == null || stock.getQuantiteDisponible() < dto.getQuantite()) {
            int disponible = stock != null ? stock.getQuantiteDisponible() : 0;
            throw GestionException.badRequest("Stock insuffisant. Disponible: " + disponible);
        }

        // Vérifier si la ligne existe déjà (même produit, même conditionnement)
        LigneVentePOS ligneExistante = ligneVentePOSDao.findByVenteAndProduitAndUnite(
                venteId, dto.getProduitId(), dto.getUniteConditionnementId()).orElse(null);

        if (ligneExistante != null) {
            // Mise à jour de la quantité
            ligneExistante.setQuantite(ligneExistante.getQuantite() + dto.getQuantite());
            ligneExistante.setPrixTotal(ligneExistante.getPrixUnitaire()
                    .multiply(BigDecimal.valueOf(ligneExistante.getQuantite())));
            ligneVentePOSDao.save(ligneExistante);
            recalculerTotaux(vente);
            return ligneExistante;
        }

        // Obtenir le prix unitaire depuis le conditionnement produit
        BigDecimal prixUnitaire = dto.getPrixUnitaire();
        if (prixUnitaire == null) {
            ConditionnementProduit cp = conditionnementProduitDao
                    .findByProduitIdAndUniteConditionnementId(dto.getProduitId(), dto.getUniteConditionnementId())
                    .orElseThrow(() -> GestionException.badRequest(
                            "Ce conditionnement n'est pas disponible pour ce produit"));
            prixUnitaire = cp.getPrixUnitaire();
        }

        LigneVentePOS ligne = new LigneVentePOS();
        ligne.setVentePOS(vente);
        ligne.setProduit(produit);
        ligne.setUniteConditionnement(unite);
        ligne.setQuantite(dto.getQuantite());
        ligne.setPrixUnitaire(prixUnitaire);
        ligne.setPrixTotal(prixUnitaire.multiply(BigDecimal.valueOf(dto.getQuantite())));
        ligne.setRemiseLigne(BigDecimal.ZERO);

        vente.getLignes().add(ligne);
        ligneVentePOSDao.save(ligne);
        recalculerTotaux(vente);

        log.info("Ligne ajoutée à la vente {}: {} x {} {}",
                vente.getNumero(), dto.getQuantite(), produit.getNom(), unite.getNom());

        return ligne;
    }

    @Transactional
    public LigneVentePOS modifierQuantiteLigne(Integer venteId, Integer ligneId, Integer quantite) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() != StatutVentePOS.BROUILLON) {
            throw GestionException.badRequest("Impossible de modifier une vente qui n'est pas en brouillon");
        }

        LigneVentePOS ligne = ligneVentePOSDao.findById(ligneId)
                .orElseThrow(() -> GestionException.notFound("Ligne de vente", ligneId));

        if (!ligne.getVentePOS().getId().equals(venteId)) {
            throw GestionException.badRequest("Cette ligne n'appartient pas à cette vente");
        }

        if (quantite <= 0) {
            throw GestionException.badRequest("La quantité doit être supérieure à 0");
        }

        // Vérifier stock disponible
        StockActuel stock = stockActuelDao.findByProduitIdAndUniteConditionnementId(
                ligne.getProduit().getId(), ligne.getUniteConditionnement().getId()).orElse(null);

        if (stock == null || stock.getQuantiteDisponible() < quantite) {
            int disponible = stock != null ? stock.getQuantiteDisponible() : 0;
            throw GestionException.badRequest("Stock insuffisant. Disponible: " + disponible);
        }

        ligne.setQuantite(quantite);
        ligne.setPrixTotal(ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(quantite)));
        ligneVentePOSDao.save(ligne);
        recalculerTotaux(vente);

        return ligne;
    }

    @Transactional
    public void supprimerLigne(Integer venteId, Integer ligneId) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() != StatutVentePOS.BROUILLON) {
            throw GestionException.badRequest("Impossible de modifier une vente qui n'est pas en brouillon");
        }

        LigneVentePOS ligne = ligneVentePOSDao.findById(ligneId)
                .orElseThrow(() -> GestionException.notFound("Ligne de vente", ligneId));

        if (!ligne.getVentePOS().getId().equals(venteId)) {
            throw GestionException.badRequest("Cette ligne n'appartient pas à cette vente");
        }

        vente.getLignes().remove(ligne);
        ligneVentePOSDao.delete(ligne);
        recalculerTotaux(vente);

        log.info("Ligne {} supprimée de la vente {}", ligneId, vente.getNumero());
    }

    // ==================== GESTION DES REMISES ====================

    @Transactional
    public VentePOS appliquerRemise(Integer venteId, RemiseDto dto) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() != StatutVentePOS.BROUILLON) {
            throw GestionException.badRequest("Impossible de modifier une vente qui n'est pas en brouillon");
        }

        vente.setTypeRemise(dto.getTypeRemise());
        vente.setValeurRemise(dto.getValeur());
        recalculerTotaux(vente);

        log.info("Remise appliquée à la vente {}: {} {}",
                vente.getNumero(), dto.getValeur(), dto.getTypeRemise());

        return ventePOSDao.save(vente);
    }

    @Transactional
    public VentePOS supprimerRemise(Integer venteId) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() != StatutVentePOS.BROUILLON) {
            throw GestionException.badRequest("Impossible de modifier une vente qui n'est pas en brouillon");
        }

        vente.setTypeRemise(null);
        vente.setValeurRemise(null);
        vente.setMontantRemise(BigDecimal.ZERO);
        recalculerTotaux(vente);

        return ventePOSDao.save(vente);
    }

    // ==================== GESTION DES PAIEMENTS ====================

    @Transactional
    public PaiementVentePOS enregistrerPaiement(Integer venteId, PaiementVentePOSDto dto) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() == StatutVentePOS.PAYEE) {
            throw GestionException.badRequest("Cette vente est déjà payée");
        }

        if (vente.getStatut() == StatutVentePOS.ANNULEE) {
            throw GestionException.badRequest("Impossible de payer une vente annulée");
        }

        if (vente.getLignes().isEmpty()) {
            throw GestionException.badRequest("Impossible de payer une vente sans articles");
        }

        BigDecimal resteAPayer = vente.getMontantTotal().subtract(vente.getMontantPaye());

        if (dto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw GestionException.badRequest("Le montant du paiement doit être positif");
        }

        PaiementVentePOS paiement = new PaiementVentePOS();
        paiement.setVentePOS(vente);
        paiement.setModePaiement(dto.getModePaiement());
        paiement.setMontant(dto.getMontant().min(resteAPayer));
        paiement.setReference(dto.getReference());

        // Calculer le rendu de monnaie pour les espèces
        if (dto.getModePaiement() == ModePaiement.ESPECES && dto.getMontantRecu() != null) {
            BigDecimal montantRendu = dto.getMontantRecu().subtract(resteAPayer);
            if (montantRendu.compareTo(BigDecimal.ZERO) > 0) {
                paiement.setMontantRendu(montantRendu);
            }
        }

        vente.getPaiements().add(paiement);
        paiementVentePOSDao.save(paiement);

        // Mettre à jour le montant payé
        BigDecimal nouveauMontantPaye = vente.getMontantPaye().add(paiement.getMontant());
        vente.setMontantPaye(nouveauMontantPaye);
        ventePOSDao.save(vente);

        log.info("Paiement enregistré pour la vente {}: {} € en {}",
                vente.getNumero(), paiement.getMontant(), dto.getModePaiement());

        return paiement;
    }

    @Transactional
    public VentePOS finaliserVente(Integer venteId) {
        VentePOS vente = getVente(venteId);

        if (vente.getStatut() == StatutVentePOS.PAYEE) {
            throw GestionException.badRequest("Cette vente est déjà finalisée");
        }

        if (vente.getStatut() == StatutVentePOS.ANNULEE) {
            throw GestionException.badRequest("Impossible de finaliser une vente annulée");
        }

        if (vente.getLignes().isEmpty()) {
            throw GestionException.badRequest("Impossible de finaliser une vente sans articles");
        }

        // Vérifier que le paiement est complet
        if (vente.getMontantPaye().compareTo(vente.getMontantTotal()) < 0) {
            throw GestionException.badRequest("Le paiement n'est pas complet. Reste à payer: " +
                    vente.getMontantTotal().subtract(vente.getMontantPaye()) + " €");
        }

        // Enregistrer les mouvements de stock (SORTIE)
        for (LigneVentePOS ligne : vente.getLignes()) {
            MouvementStock mouvement = new MouvementStock();
            mouvement.setProduit(ligne.getProduit());
            mouvement.setUniteConditionnement(ligne.getUniteConditionnement());
            mouvement.setTypeMouvement(TypeMouvement.SORTIE);
            mouvement.setQuantite(ligne.getQuantite());
            mouvement.setTypeReference(TypeReference.VENTE_POS);
            mouvement.setReferenceId(vente.getId());
            mouvement.setPrixUnitaire(ligne.getPrixUnitaire());
            mouvement.setRaison("Vente POS " + vente.getNumero());
            mouvement.setUtilisateur(vente.getUtilisateur());

            stockService.enregistrerMouvement(mouvement);
        }

        vente.setStatut(StatutVentePOS.PAYEE);
        vente.setDateVente(LocalDateTime.now());
        ventePOSDao.save(vente);

        log.info("Vente POS {} finalisée. Total: {} €", vente.getNumero(), vente.getMontantTotal());

        return vente;
    }

    // ==================== TICKET DE CAISSE ====================

    public TicketVentePOSDto genererTicket(Integer venteId) {
        VentePOS vente = getVente(venteId);

        List<TicketVentePOSDto.LigneTicketDto> lignes = vente.getLignes().stream()
                .map(l -> TicketVentePOSDto.LigneTicketDto.builder()
                        .produitNom(l.getProduit().getNom())
                        .conditionnement(l.getUniteConditionnement().getNom())
                        .quantite(l.getQuantite())
                        .prixUnitaire(l.getPrixUnitaire())
                        .prixTotal(l.getPrixTotal())
                        .build())
                .collect(Collectors.toList());

        List<TicketVentePOSDto.PaiementTicketDto> paiements = vente.getPaiements().stream()
                .map(p -> TicketVentePOSDto.PaiementTicketDto.builder()
                        .modePaiement(p.getModePaiement())
                        .montant(p.getMontant())
                        .reference(p.getReference())
                        .build())
                .collect(Collectors.toList());

        BigDecimal montantRendu = vente.getPaiements().stream()
                .filter(p -> p.getMontantRendu() != null)
                .map(PaiementVentePOS::getMontantRendu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TicketVentePOSDto.builder()
                .numero(vente.getNumero())
                .dateVente(vente.getDateVente() != null ? vente.getDateVente() : vente.getCreeLe())
                .vendeur(vente.getUtilisateur().getPrenom() + " " + vente.getUtilisateur().getNom())
                .lignes(lignes)
                .sousTotal(vente.getMontantSousTotal())
                .typeRemise(vente.getTypeRemise())
                .valeurRemise(vente.getValeurRemise())
                .montantRemise(vente.getMontantRemise())
                .total(vente.getMontantTotal())
                .paiements(paiements)
                .montantPaye(vente.getMontantPaye())
                .montantRendu(montantRendu)
                .build();
    }

    // ==================== STATISTIQUES ====================

    public StatsJourPOSDto getStatsJour() {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();

        Long nombreVentes = ventePOSDao.getNombreVentesJour(debutJour);
        BigDecimal totalVentes = ventePOSDao.getTotalVentesJour(debutJour);

        BigDecimal moyenneParVente = BigDecimal.ZERO;
        if (nombreVentes > 0) {
            moyenneParVente = totalVentes.divide(BigDecimal.valueOf(nombreVentes), 2, RoundingMode.HALF_UP);
        }

        // Totaux par mode de paiement
        Map<String, BigDecimal> totauxParMode = new HashMap<>();
        List<Object[]> resultats = paiementVentePOSDao.getTotauxParModePaiementJour(debutJour);
        for (Object[] row : resultats) {
            ModePaiement mode = (ModePaiement) row[0];
            BigDecimal montant = (BigDecimal) row[1];
            totauxParMode.put(mode.name(), montant);
        }

        return StatsJourPOSDto.builder()
                .nombreVentes(nombreVentes)
                .totalVentes(totalVentes)
                .moyenneParVente(moyenneParVente)
                .totauxParModePaiement(totauxParMode)
                .build();
    }

    // ==================== RECHERCHE PAR CODE-BARRES ====================

    public ConditionnementProduit rechercherParCodeBarre(String codeBarre) {
        return conditionnementProduitDao.findByCodeBarreWithDetails(codeBarre)
                .orElseThrow(() -> GestionException.notFound("Produit", "code-barres " + codeBarre));
    }

    // ==================== MÉTHODES UTILITAIRES PRIVÉES ====================

    private String genererNumeroVente() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer maxId = ventePOSDao.getMaxId();
        int nextId = (maxId != null ? maxId : 0) + 1;
        return String.format("POS-%s-%04d", dateStr, nextId);
    }

    private void recalculerTotaux(VentePOS vente) {
        // Calcul du sous-total
        BigDecimal sousTotal = vente.getLignes().stream()
                .map(LigneVentePOS::getPrixTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vente.setMontantSousTotal(sousTotal);

        // Calcul de la remise
        BigDecimal montantRemise = BigDecimal.ZERO;
        if (vente.getTypeRemise() != null && vente.getValeurRemise() != null) {
            if (vente.getTypeRemise() == TypeRemise.POURCENTAGE) {
                montantRemise = sousTotal.multiply(vente.getValeurRemise())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                montantRemise = vente.getValeurRemise().min(sousTotal);
            }
        }
        vente.setMontantRemise(montantRemise);

        // Calcul du total
        BigDecimal total = sousTotal.subtract(montantRemise);
        vente.setMontantTotal(total.max(BigDecimal.ZERO));

        ventePOSDao.save(vente);
    }
}
