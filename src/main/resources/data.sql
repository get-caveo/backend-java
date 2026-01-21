/* password  = root */
INSERT IGNORE INTO utilisateurs (email, mot_de_passe, prenom, nom, telephone, role, actif, cree_le) VALUES
    ('simon@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Simon', 'Picot', '+33000000000', 'ADMIN', true, '2025-12-19 10:00:00'),
    ('terry@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Terry', 'Lagarde', '+33000000000', 'EMPLOYE', true, '2025-12-19 11:30:00'),
    ('cecilia@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Cecilia', 'Tomassi', '+33000000000', 'CLIENT', true, '2025-12-19 14:15:00');

/* Catégories de vins */
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
