package com.caveo.backend.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class StockInsuffisantException extends GestionException {

    private final String type = "STOCK_INSUFFISANT";
    private final List<ProduitInsuffisant> produitsInsuffisants;
    private final List<String> numerosCommandesFournisseur;

    public StockInsuffisantException(
            String message,
            List<ProduitInsuffisant> produitsInsuffisants,
            List<String> numerosCommandesFournisseur) {
        super(message, HttpStatus.CONFLICT);
        this.produitsInsuffisants = produitsInsuffisants;
        this.numerosCommandesFournisseur = numerosCommandesFournisseur;
    }

    @Getter
    public static class ProduitInsuffisant {
        private final Integer produitId;
        private final String produitNom;
        private final Integer quantiteDemandee;
        private final Integer quantiteDisponible;
        private final Integer deficit;

        public ProduitInsuffisant(Integer produitId, String produitNom,
                                  Integer quantiteDemandee, Integer quantiteDisponible) {
            this.produitId = produitId;
            this.produitNom = produitNom;
            this.quantiteDemandee = quantiteDemandee;
            this.quantiteDisponible = quantiteDisponible;
            this.deficit = quantiteDemandee - quantiteDisponible;
        }
    }
}
