SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS caveodb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE caveodb;

/* password  = root */
INSERT IGNORE INTO utilisateurs (email, mot_de_passe, prenom, nom, telephone, role, actif, cree_le) VALUES
    ('simon@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Simon', 'Picot', '+33000000000', 'ADMIN', true, '2025-12-19 10:00:00'),
    ('terry@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Terry', 'Lagarde', '+33000000000', 'EMPLOYE', true, '2025-12-19 11:30:00'),
    ('cecilia@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Cecilia', 'Tomassi', '+33000000000', 'CLIENT', true, '2025-12-19 14:15:00');

INSERT IGNORE INTO categories (nom, description, ordre_tri, actif) VALUES
    ('Vins Rouges', 'Sélection de vins rouges prestigieux', 1, true),
    ('Vins Blancs', 'Vins blancs fins et élégants', 2, true),
    ('Champagnes', 'Champagnes et vins effervescents', 3, true),
    ('Rosés', 'Vins rosés frais et fruités', 4, true);

/* Domaines viticoles */
INSERT IGNORE INTO domaines (nom, region, description, actif) VALUES
    ('Château Margaux', 'Bordeaux', 'Premier Grand Cru Classé, domaine emblématique du Médoc', true),
    ('Domaine de la Romanée-Conti', 'Bourgogne', 'Le plus prestigieux domaine de Bourgogne', true),
    ('Dom Pérignon', 'Champagne', 'Maison de champagne légendaire', true),
    ('Château Mouton Rothschild', 'Bordeaux', 'Premier Grand Cru Classé depuis 1973', true);

/* Unités de conditionnement */
INSERT IGNORE INTO unites_conditionnement (nom, nom_court, quantite_unite_base, description, volume_ml) VALUES
    ('Bouteille 75cl', '75cl', 1, 'Bouteille standard de 75cl', 750),
    ('Magnum 1.5L', 'Magnum', 2, 'Magnum équivalent à 2 bouteilles', 1500),
    ('Caisse de 6', 'Cx6', 6, 'Caisse de 6 bouteilles de 75cl', 4500),
    ('Caisse de 12', 'Cx12', 12, 'Caisse de 12 bouteilles de 75cl', 9000);

/* Fournisseurs */
INSERT IGNORE INTO fournisseurs (nom, personne_contact, email, telephone, adresse, conditions_paiement, certification_bio, certification_aoc, certifications_autres) VALUES
    ('Domaine de Tariquet', 'Pierre Grassa', 'contact@tariquet.com', '+33562091287', '32800 Eauze, Gers', 'Net 30 jours', false, true, 'IGP Côtes de Gascogne'),
    ('Domaine de Pellehaut', 'Mathieu Béraut', 'info@pellehaut.com', '+33562283791', '32250 Montréal-du-Gers', 'Net 45 jours', true, true, 'IGP Côtes de Gascogne, Agriculture Biologique'),
    ('Domaine de Joy', 'Olivier Daugé', 'domaine@joy.fr', '+33562090180', '32110 Panjas, Gers', 'Net 30 jours', false, true, 'IGP Côtes de Gascogne'),
    ('Vignoble Fontan', 'Jean-Marc Fontan', 'contact@vignoble-fontan.fr', '+33562695412', '32800 Eauze, Gers', 'Net 60 jours', false, false, NULL),
    ('Domaine Uby', 'François Morel', 'cave@uby.fr', '+33562291055', '32150 Cazaubon, Gers', 'Net 30 jours', true, true, 'IGP Côtes de Gascogne, HVE Niveau 3');

/* Produits - Vins */
INSERT IGNORE INTO produits (sku, nom, description, millesime, degre_alcool, image_url, categorie_id, domaine_id, actif) VALUES
    ('VR-MAR-2018', 'Château Margaux 2018', 'Un millésime exceptionnel, alliance parfaite de puissance et d''élégance. Notes de cassis, violette et épices douces.', 2018, 13.5, 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=400&h=800&fit=crop', 1, 1, true),
    ('VR-ROM-2019', 'Romanée-Conti 2019', 'Le pinot noir dans sa plus pure expression. Arômes de fruits rouges, truffe et sous-bois.', 2019, 13.0, 'https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=400&h=800&fit=crop', 1, 2, true),
    ('VR-MOU-2016', 'Mouton Rothschild 2016', 'Millésime d''anthologie, structure tannique remarquable avec une finale exceptionnelle.', 2016, 13.5, 'https://images.unsplash.com/photo-1586370434639-0fe43b2d5f5e?w=400&h=800&fit=crop', 1, 4, true),
    ('CH-DOM-2012', 'Dom Pérignon Vintage 2012', 'Champagne d''exception aux notes de brioche, agrumes et amandes grillées.', 2012, 12.5, 'https://images.unsplash.com/photo-1547595628-c61a29f496f0?w=400&h=800&fit=crop', 3, 3, true),
    ('VB-MAR-2020', 'Pavillon Blanc du Château Margaux 2020', 'Grand vin blanc de Bordeaux, fraîcheur et complexité aromatique.', 2020, 13.0, 'https://images.unsplash.com/photo-1566995541428-f2246c17cda1?w=400&h=800&fit=crop', 2, 1, true),
    ('CH-DOM-ROSE', 'Dom Pérignon Rosé 2008', 'Champagne rosé rare et prestigieux, notes de fruits rouges et épices.', 2008, 12.5, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=800&fit=crop', 3, 3, true);

/* Conditionnements produits avec prix */
INSERT IGNORE INTO conditionnements_produit (prix_unitaire, disponible, produit_id, unite_conditionnement_id) VALUES
    (890.00, true, 1, 1),   /* Château Margaux - Bouteille 75cl */
    (1700.00, true, 1, 2),  /* Château Margaux - Magnum */
    (5000.00, true, 1, 3),  /* Château Margaux - Caisse 6 */
    (15000.00, true, 2, 1), /* Romanée-Conti - Bouteille 75cl */
    (750.00, true, 3, 1),   /* Mouton Rothschild - Bouteille 75cl */
    (4200.00, true, 3, 3),  /* Mouton Rothschild - Caisse 6 */
    (250.00, true, 4, 1),   /* Dom Pérignon - Bouteille 75cl */
    (480.00, true, 4, 2),   /* Dom Pérignon - Magnum */
    (320.00, true, 5, 1),   /* Pavillon Blanc - Bouteille 75cl */
    (650.00, true, 6, 1);   /* Dom Pérignon Rosé - Bouteille 75cl */

/* Fournisseurs-Produits - Relations entre fournisseurs, produits et conditionnements */
/* Format: (prix_fournisseur, delai_appro_jours, fournisseur_id, produit_id, unite_conditionnement_id) */
INSERT IGNORE INTO fournisseurs_produits (prix_fournisseur, delai_appro_jours, fournisseur_id, produit_id, unite_conditionnement_id) VALUES
    (650.00, 7, 1, 1, 1),    /* Tariquet fournit Château Margaux - 75cl */
    (1250.00, 7, 1, 1, 2),   /* Tariquet fournit Château Margaux - Magnum */
    (3800.00, 10, 1, 1, 3),  /* Tariquet fournit Château Margaux - Caisse 6 */
    (12000.00, 14, 2, 2, 1), /* Pellehaut fournit Romanée-Conti - 75cl */
    (550.00, 10, 3, 3, 1),   /* Joy fournit Mouton Rothschild - 75cl */
    (3200.00, 12, 3, 3, 3),  /* Joy fournit Mouton Rothschild - Caisse 6 */
    (180.00, 5, 4, 4, 1),    /* Fontan fournit Dom Pérignon - 75cl */
    (350.00, 5, 4, 4, 2),    /* Fontan fournit Dom Pérignon - Magnum */
    (220.00, 7, 5, 5, 1),    /* Uby fournit Pavillon Blanc - 75cl */
    (480.00, 10, 1, 6, 1),   /* Tariquet fournit Dom Pérignon Rosé - 75cl */
    (700.00, 12, 2, 1, 1),   /* Pellehaut fournit aussi Château Margaux - 75cl (second fournisseur) */
    (600.00, 8, 5, 3, 1);    /* Uby fournit aussi Mouton Rothschild - 75cl (second fournisseur) */

/* Update produits with seuil_stock_minimal */
UPDATE produits SET seuil_stock_minimal = 10, reappro_auto = true WHERE sku = 'VR-MAR-2018';
UPDATE produits SET seuil_stock_minimal = 5, reappro_auto = true WHERE sku = 'VR-ROM-2019';
UPDATE produits SET seuil_stock_minimal = 8, reappro_auto = true WHERE sku = 'VR-MOU-2016';
UPDATE produits SET seuil_stock_minimal = 12, reappro_auto = true WHERE sku = 'CH-DOM-2012';
UPDATE produits SET seuil_stock_minimal = 6, reappro_auto = false WHERE sku = 'VB-MAR-2020';
UPDATE produits SET seuil_stock_minimal = 4, reappro_auto = true WHERE sku = 'CH-DOM-ROSE';

/* Update conditionnements with code_barre (barcode per product+packaging combination) */
UPDATE conditionnements_produit SET code_barre = '3760001234501' WHERE produit_id = 1 AND unite_conditionnement_id = 1;  /* Margaux 75cl */
UPDATE conditionnements_produit SET code_barre = '3760001234502' WHERE produit_id = 1 AND unite_conditionnement_id = 2;  /* Margaux Magnum */
UPDATE conditionnements_produit SET code_barre = '3760001234503' WHERE produit_id = 1 AND unite_conditionnement_id = 3;  /* Margaux Caisse 6 */
UPDATE conditionnements_produit SET code_barre = '3760001234504' WHERE produit_id = 2 AND unite_conditionnement_id = 1;  /* Romanée-Conti 75cl */
UPDATE conditionnements_produit SET code_barre = '3760001234505' WHERE produit_id = 3 AND unite_conditionnement_id = 1;  /* Mouton 75cl */
UPDATE conditionnements_produit SET code_barre = '3760001234506' WHERE produit_id = 3 AND unite_conditionnement_id = 3;  /* Mouton Caisse 6 */
UPDATE conditionnements_produit SET code_barre = '3760001234507' WHERE produit_id = 4 AND unite_conditionnement_id = 1;  /* Dom Pérignon 75cl */
UPDATE conditionnements_produit SET code_barre = '3760001234508' WHERE produit_id = 4 AND unite_conditionnement_id = 2;  /* Dom Pérignon Magnum */
UPDATE conditionnements_produit SET code_barre = '3760001234509' WHERE produit_id = 5 AND unite_conditionnement_id = 1;  /* Pavillon Blanc 75cl */
UPDATE conditionnements_produit SET code_barre = '3760001234510' WHERE produit_id = 6 AND unite_conditionnement_id = 1;  /* Dom Pérignon Rosé 75cl */

/* Stock actuel - Initial stock levels */
INSERT IGNORE INTO stock_actuel (quantite, quantite_reservee, quantite_disponible, quantite_unite_base, dernier_inventaire, produit_id, unite_conditionnement_id) VALUES
    (24, 2, 22, 24, '2026-01-15 10:00:00', 1, 1),  /* Château Margaux - 75cl: 24 bouteilles, 2 réservées */
    (6, 0, 6, 12, '2026-01-15 10:00:00', 1, 2),    /* Château Margaux - Magnum: 6 magnums */
    (3, 0, 3, 18, '2026-01-15 10:00:00', 1, 3),    /* Château Margaux - Caisse 6: 3 caisses */
    (8, 1, 7, 8, '2026-01-15 10:00:00', 2, 1),     /* Romanée-Conti - 75cl: 8 bouteilles */
    (15, 0, 15, 15, '2026-01-15 10:00:00', 3, 1),  /* Mouton Rothschild - 75cl: 15 bouteilles */
    (2, 0, 2, 12, '2026-01-15 10:00:00', 3, 3),    /* Mouton Rothschild - Caisse 6: 2 caisses */
    (36, 5, 31, 36, '2026-01-15 10:00:00', 4, 1),  /* Dom Pérignon - 75cl: 36 bouteilles */
    (4, 0, 4, 8, '2026-01-15 10:00:00', 4, 2),     /* Dom Pérignon - Magnum: 4 magnums */
    (3, 0, 3, 3, '2026-01-15 10:00:00', 5, 1),     /* Pavillon Blanc - 75cl: 3 bouteilles (below threshold!) */
    (2, 0, 2, 2, '2026-01-15 10:00:00', 6, 1);     /* Dom Pérignon Rosé - 75cl: 2 bouteilles (below threshold!) */

/* Mouvements stock - Recent movements history */
INSERT IGNORE INTO mouvements_stock (type_mouvement, quantite, quantite_unite_base, type_reference, raison, cree_le, produit_id, unite_conditionnement_id, utilisateur_id) VALUES
    ('ENTREE', 24, 24, 'COMMANDE_FOURNISSEUR', 'Réception commande CF-202601-0001', '2026-01-10 09:30:00', 1, 1, 1),
    ('ENTREE', 6, 12, 'COMMANDE_FOURNISSEUR', 'Réception commande CF-202601-0001', '2026-01-10 09:30:00', 1, 2, 1),
    ('ENTREE', 8, 8, 'COMMANDE_FOURNISSEUR', 'Réception commande CF-202601-0002', '2026-01-12 14:00:00', 2, 1, 2),
    ('SORTIE', 2, 2, 'COMMANDE_CLIENT', 'Commande client #1234', '2026-01-18 11:00:00', 1, 1, 2),
    ('SORTIE', 1, 1, 'COMMANDE_CLIENT', 'Commande client #1235', '2026-01-19 15:30:00', 2, 1, 2),
    ('ENTREE', 36, 36, 'COMMANDE_FOURNISSEUR', 'Réception commande CF-202601-0003', '2026-01-14 10:00:00', 4, 1, 1),
    ('SORTIE', 5, 5, 'MANUEL', 'Événement dégustation VIP', '2026-01-20 18:00:00', 4, 1, 2),
    ('AJUSTEMENT', -2, -2, 'INVENTAIRE', 'Correction inventaire - bouteilles cassées', '2026-01-15 16:00:00', 5, 1, 1),
    ('ENTREE', 15, 15, 'COMMANDE_FOURNISSEUR', 'Réception commande CF-202601-0004', '2026-01-13 11:00:00', 3, 1, 1);
