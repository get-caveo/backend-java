/* password  = root */
INSERT INTO utilisateurs (email, mot_de_passe, prenom, nom, telephone, role, actif, cree_le) VALUES
    ('simon@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Simon', 'Picot', '+33000000000', 'ADMIN', true, '2025-12-19 10:00:00'),
    ('terry@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Terry', 'Lagarde', '+33000000000', 'ADMIN', true, '2025-12-19 11:30:00'),
    ('cecilia@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Cecilia', 'Tomassi', '+33000000000', 'EMPLOYE', true, '2025-12-19 14:15:00');

-- =============================================
-- CATÉGORIES
-- =============================================
INSERT INTO categories (nom, description, ordre_tri, actif, cree_le) VALUES
    ('Vins Rouges', 'Vins rouges', 1, true, '2025-12-19 09:00:00'),
    ('Vins Blancs', 'Vins blancs secs et moelleux', 2, true, '2025-12-19 09:00:00'),
    ('Vins Rosés', 'Vins rosés de Provence et autres régions', 3, true, '2025-12-19 09:00:00'),
    ('Champagnes', 'Champagnes et crémants', 4, true, '2025-12-19 09:00:00'),
    ('Spiritueux', 'Cognac, Armagnac, Whisky et autres spiritueux', 5, true, '2025-12-19 09:00:00');

-- =============================================
-- DOMAINES
-- =============================================
INSERT INTO domaines (nom, region, appellation, surface_vignoble_ha, type_sol, cepages, vigneron, description, site_web, latitude, longitude, actif, cree_le) VALUES
    ('Château Margaux', 'Bordeaux', 'Margaux AOC', 87.00, 'Graves profondes', 'Cabernet Sauvignon, Merlot, Petit Verdot, Cabernet Franc', 'Philippe Bascaules', 'Premier Grand Cru Classé 1855, l''un des plus prestigieux domaines du Médoc', 'https://www.chateau-margaux.com', 45.04130000, -0.67690000, true, '2025-12-19 09:00:00'),
    ('Domaine de la Romanée-Conti', 'Bourgogne', 'Vosne-Romanée AOC', 28.00, 'Calcaire et marne', 'Pinot Noir, Chardonnay', 'Aubert de Villaine', 'Le domaine le plus mythique de Bourgogne, produisant les vins les plus recherchés au monde', 'https://www.romanee-conti.fr', 47.16220000, 4.95170000, true, '2025-12-19 09:00:00'),
    ('Maison Louis Latour', 'Bourgogne', 'Corton-Charlemagne AOC', 50.00, 'Argilo-calcaire', 'Chardonnay, Pinot Noir', 'Louis-Fabrice Latour', 'Maison bourguignonne fondée en 1797, spécialiste des grands crus', 'https://www.louislatour.com', 47.07500000, 4.85830000, true, '2025-12-19 09:00:00'),
    ('Château d''Yquem', 'Bordeaux', 'Sauternes AOC', 113.00, 'Argilo-graveleux', 'Sémillon, Sauvignon Blanc', 'Pierre Lurton', 'Premier Cru Supérieur, seul Sauternes à avoir cette classification', 'https://www.yquem.fr', 44.54440000, -0.29170000, true, '2025-12-19 09:00:00'),
    ('Domaine Tempier', 'Provence', 'Bandol AOC', 38.00, 'Calcaire et marnes', 'Mourvèdre, Grenache, Cinsault', 'Daniel Ravier', 'Domaine emblématique de Bandol, pionnier des vins de qualité en Provence', 'https://www.domainetempier.com', 43.17500000, 5.75000000, true, '2025-12-19 09:00:00'),
    ('Champagne Krug', 'Champagne', 'Champagne AOC', 20.00, 'Craie', 'Chardonnay, Pinot Noir, Pinot Meunier', 'Julie Cavil', 'Maison de prestige fondée en 1843, connue pour ses cuvées d''exception', 'https://www.krug.com', 49.04420000, 3.95330000, true, '2025-12-19 09:00:00'),
    ('Hennessy', 'Cognac', 'Cognac AOC', NULL, 'Calcaire crayeux', 'Ugni Blanc', 'Renaud Fillioux de Gironde', 'Plus grande maison de Cognac au monde, fondée en 1765', 'https://www.hennessy.com', 45.69580000, -0.32640000, true, '2025-12-19 09:00:00');

-- =============================================
-- FOURNISSEURS (3)
-- =============================================
INSERT INTO fournisseurs (nom, personne_contact, email, telephone, adresse, conditions_paiement, certification_bio, certification_aoc) VALUES
    ('Bordeaux Grands Crus Distribution', 'Jean-Pierre Duval', 'jp.duval@bgcd.fr', '+33556001234', '15 Quai des Chartrons, 33000 Bordeaux', '30 jours fin de mois', false, true),
    ('La Cave Bourguignonne', 'Marie-Claire Fontaine', 'mc.fontaine@cavebourgogne.fr', '+33380234567', '8 Route des Grands Crus, 21200 Beaune', '45 jours', true, true),
    ('Premium Spirits Import', 'Antoine Marchetti', 'a.marchetti@premiumspirits.com', '+33147890123', '42 Avenue des Champs-Élysées, 75008 Paris', '60 jours', false, false);

-- =============================================
-- UNITÉS DE CONDITIONNEMENT (Packaging)
-- =============================================
INSERT INTO unites_conditionnement (nom, nom_court, quantite_unite_base, description, dimensions_cm, poids_kg, volume_ml, est_vendable, est_unite_base, ordre_tri, actif, cree_le) VALUES
    ('Bouteille 75cl', 'BT75', 1, 'Bouteille standard de 75cl', '8x8x30', 1.20, 750, true, true, 1, true, '2025-12-19 09:00:00'),
    ('Magnum 150cl', 'MAG', 2, 'Magnum équivalent à 2 bouteilles', '10x10x40', 2.50, 1500, true, false, 2, true, '2025-12-19 09:00:00'),
    ('Jéroboam 300cl', 'JER', 4, 'Jéroboam équivalent à 4 bouteilles', '14x14x50', 5.00, 3000, true, false, 3, true, '2025-12-19 09:00:00'),
    ('Demi-bouteille 37.5cl', 'DEM', 1, 'Demi-bouteille pour dégustation', '7x7x25', 0.60, 375, true, false, 4, true, '2025-12-19 09:00:00'),
    ('Caisse 6 bouteilles', 'CX6', 6, 'Caisse carton de 6 bouteilles 75cl', '25x17x32', 8.00, 4500, true, false, 5, true, '2025-12-19 09:00:00'),
    ('Caisse 12 bouteilles', 'CX12', 12, 'Caisse bois de 12 bouteilles 75cl', '35x27x32', 16.00, 9000, true, false, 6, true, '2025-12-19 09:00:00');

-- =============================================
-- PRODUITS (10 vins inventés)
-- =============================================
INSERT INTO produits (sku, nom, description, domaine_id, millesime, degre_alcool, code_barre, image_url, notes_degustation, temperature_service, conditions_conservation, seuil_stock_minimal, reappro_auto, actif, categorie_id, cree_le) VALUES
    ('VR-MARG-2018', 'Château Margaux 2018', 'Grand vin rouge de Margaux, Premier Grand Cru Classé. Expression parfaite du terroir de Margaux avec une élégance incomparable.', 1, 2018, 13.5, '3760001001001', 'https://images.caveo.com/margaux-2018.jpg', 'Robe rubis profond. Nez complexe de cassis, violette et cèdre. Bouche soyeuse avec des tanins veloutés, finale longue et persistante.', '16-18°C', 'Cave à 12-14°C, humidité 70%, à l''abri de la lumière', 10, true, true, 1, '2025-12-19 10:00:00'),
    ('VR-DRC-2019', 'Romanée-Conti 2019', 'Grand Cru de la Romanée-Conti, le pinot noir dans sa plus pure expression. Vin mythique et rare.', 2, 2019, 13.0, '3760001001002', 'https://images.caveo.com/drc-2019.jpg', 'Robe grenat lumineuse. Bouquet envoûtant de rose fanée, cerise noire et épices orientales. Texture aérienne, tanins fins comme de la soie.', '15-17°C', 'Cave température constante, position couchée obligatoire', 3, true, true, 1, '2025-12-19 10:00:00'),
    ('VB-CORT-2020', 'Corton-Charlemagne 2020', 'Grand Cru blanc de la maison Louis Latour. Chardonnay d''exception sur le coteau de Corton.', 3, 2020, 13.5, '3760001001003', 'https://images.caveo.com/corton-2020.jpg', 'Or pâle aux reflets verts. Nez minéral intense, notes de silex, citron confit et amande fraîche. Bouche ample et tendue, finale saline.', '12-14°C', 'Cave fraîche, consommer dans les 15 ans', 8, true, true, 2, '2025-12-19 10:00:00'),
    ('VB-YQM-2017', 'Château d''Yquem 2017', 'Premier Cru Supérieur de Sauternes. Vin liquoreux légendaire, botrytis noble.', 4, 2017, 14.0, '3760001001004', 'https://images.caveo.com/yquem-2017.jpg', 'Robe or intense. Nez opulent d''abricot confit, miel d''acacia, safran et zeste d''orange. Bouche onctueuse équilibrée par une acidité vibrante.', '8-10°C', 'Cave, peut se conserver plus de 100 ans', 5, true, true, 2, '2025-12-19 10:00:00'),
    ('VRS-TEMP-2021', 'Bandol Rosé 2021', 'Rosé de gastronomie du Domaine Tempier. Mourvèdre dominant pour un rosé de caractère.', 5, 2021, 13.0, '3760001001005', 'https://images.caveo.com/tempier-rose-2021.jpg', 'Robe saumon pâle. Nez de pêche blanche, fleur d''oranger et herbes de Provence. Bouche ample et fraîche, finale saline.', '10-12°C', 'À consommer dans les 3 ans, garder au frais', 15, true, true, 3, '2025-12-19 10:00:00'),
    ('VR-TEMP-2019', 'Bandol Rouge La Tourtine 2019', 'Cuvée parcellaire du Domaine Tempier. Mourvèdre centenaire, vin de garde.', 5, 2019, 14.0, '3760001001006', 'https://images.caveo.com/tempier-tourtine-2019.jpg', 'Robe pourpre profond. Nez puissant de mûre, garrigue, olive noire et viande fumée. Tanins fermes mais mûrs, potentiel de garde exceptionnel.', '16-18°C', 'Cave 12-15°C, attendre 5-10 ans minimum', 6, true, true, 1, '2025-12-19 10:00:00'),
    ('CH-KRUG-NV', 'Krug Grande Cuvée', 'Assemblage multi-millésimes, expression ultime du style Krug. Plus de 120 vins de 10 années différentes.', 6, NULL, 12.5, '3760001001007', 'https://images.caveo.com/krug-gc.jpg', 'Bulles fines et persistantes. Nez de brioche toastée, noisette, fleur d''acacia et agrumes confits. Bouche crémeuse et complexe, finale interminable.', '9-12°C', 'Cave fraîche, position couchée ou debout', 10, true, true, 4, '2025-12-19 10:00:00'),
    ('CH-KRUG-2008', 'Krug Vintage 2008', 'Champagne millésimé d''exception. Année légendaire, parfait équilibre.', 6, 2008, 12.5, '3760001001008', 'https://images.caveo.com/krug-2008.jpg', 'Or doré lumineux. Nez complexe de fruits secs, pain grillé, citron confit et craie. Bouche puissante et précise, acidité tranchante.', '10-12°C', 'Cave, potentiel de garde 30+ ans', 4, true, true, 4, '2025-12-19 10:00:00'),
    ('SP-HENN-XO', 'Hennessy XO', 'Cognac Extra Old, assemblage de plus de 100 eaux-de-vie. Minimum 10 ans de vieillissement.', 7, NULL, 40.0, '3760001001009', 'https://images.caveo.com/hennessy-xo.jpg', 'Ambre profond aux reflets cuivrés. Nez intense de fruits confits, épices douces, cuir et chêne toasté. Bouche ronde et puissante, finale épicée.', '18-22°C', 'À température ambiante, à l''abri de la lumière', 8, true, true, 5, '2025-12-19 10:00:00'),
    ('SP-HENN-PAR', 'Hennessy Paradis', 'Cognac d''exception, assemblage rare d''eaux-de-vie centenaires de Grande Champagne.', 7, NULL, 40.0, '3760001001010', 'https://images.caveo.com/hennessy-paradis.jpg', 'Ambre doré étincelant. Bouquet délicat de jasmin, rose, fruits secs et notes de miel. Texture soyeuse, complexité infinie, finale florale persistante.', '18-22°C', 'Conserver debout, température stable', 3, true, true, 5, '2025-12-19 10:00:00');

-- =============================================
-- CONDITIONNEMENTS PRODUIT (prix de vente)
-- =============================================
INSERT INTO conditionnements_produit (prix_unitaire, disponible, produit_id, unite_conditionnement_id, cree_le) VALUES
    -- Château Margaux 2018
    (850.00, true, 1, 1, '2025-12-19 10:00:00'),   -- BT75
    (1800.00, true, 1, 2, '2025-12-19 10:00:00'),  -- MAG
    -- Romanée-Conti 2019
    (22000.00, true, 2, 1, '2025-12-19 10:00:00'), -- BT75
    -- Corton-Charlemagne 2020
    (180.00, true, 3, 1, '2025-12-19 10:00:00'),   -- BT75
    (380.00, true, 3, 2, '2025-12-19 10:00:00'),   -- MAG
    (980.00, true, 3, 5, '2025-12-19 10:00:00'),   -- CX6
    -- Château d'Yquem 2017
    (450.00, true, 4, 1, '2025-12-19 10:00:00'),   -- BT75
    (120.00, true, 4, 4, '2025-12-19 10:00:00'),   -- DEM
    -- Bandol Rosé 2021
    (35.00, true, 5, 1, '2025-12-19 10:00:00'),    -- BT75
    (180.00, true, 5, 5, '2025-12-19 10:00:00'),   -- CX6
    -- Bandol Rouge La Tourtine 2019
    (75.00, true, 6, 1, '2025-12-19 10:00:00'),    -- BT75
    (160.00, true, 6, 2, '2025-12-19 10:00:00'),   -- MAG
    -- Krug Grande Cuvée
    (250.00, true, 7, 1, '2025-12-19 10:00:00'),   -- BT75
    (520.00, true, 7, 2, '2025-12-19 10:00:00'),   -- MAG
    -- Krug 2008
    (380.00, true, 8, 1, '2025-12-19 10:00:00'),   -- BT75
    -- Hennessy XO
    (220.00, true, 9, 1, '2025-12-19 10:00:00'),   -- BT75
    -- Hennessy Paradis
    (1200.00, true, 10, 1, '2025-12-19 10:00:00'); -- BT75

-- =============================================
-- FOURNISSEURS PRODUITS (liaison produit-fournisseur)
-- =============================================
INSERT INTO fournisseurs_produits (prix_fournisseur, delai_appro_jours, fournisseur_id, produit_id, cree_le) VALUES
    -- Bordeaux Grands Crus Distribution (fournisseur 1)
    (650.00, 7, 1, 1, '2025-12-19 10:00:00'),   -- Margaux
    (350.00, 7, 1, 4, '2025-12-19 10:00:00'),   -- Yquem
    -- La Cave Bourguignonne (fournisseur 2)
    (18500.00, 14, 2, 2, '2025-12-19 10:00:00'), -- DRC
    (140.00, 5, 2, 3, '2025-12-19 10:00:00'),    -- Corton-Charlemagne
    (25.00, 3, 2, 5, '2025-12-19 10:00:00'),     -- Bandol Rosé
    (55.00, 5, 2, 6, '2025-12-19 10:00:00'),     -- Bandol Rouge
    -- Premium Spirits Import (fournisseur 3)
    (190.00, 10, 3, 7, '2025-12-19 10:00:00'),   -- Krug GC
    (290.00, 14, 3, 8, '2025-12-19 10:00:00'),   -- Krug 2008
    (165.00, 7, 3, 9, '2025-12-19 10:00:00'),    -- Hennessy XO
    (900.00, 21, 3, 10, '2025-12-19 10:00:00');  -- Hennessy Paradis

-- =============================================
-- STOCK ACTUEL (tous au-dessus du seuil minimal)
-- =============================================
INSERT INTO stock_actuel (quantite, quantite_reservee, quantite_disponible, quantite_unite_base, dernier_inventaire, modifie_le, produit_id, unite_conditionnement_id) VALUES
    -- Château Margaux 2018 (seuil: 10)
    (24, 2, 22, 24, '2025-12-15 14:00:00', NULL, 1, 1),
    (6, 0, 6, 12, '2025-12-15 14:00:00', NULL, 1, 2),
    -- Romanée-Conti 2019 (seuil: 3)
    (6, 1, 5, 6, '2025-12-15 14:00:00', NULL, 2, 1),
    -- Corton-Charlemagne 2020 (seuil: 8)
    (36, 4, 32, 36, '2025-12-15 14:00:00', NULL, 3, 1),
    (8, 0, 8, 16, '2025-12-15 14:00:00', NULL, 3, 2),
    -- Château d'Yquem 2017 (seuil: 5)
    (18, 0, 18, 18, '2025-12-15 14:00:00', NULL, 4, 1),
    (12, 2, 10, 12, '2025-12-15 14:00:00', NULL, 4, 4),
    -- Bandol Rosé 2021 (seuil: 15)
    (48, 6, 42, 48, '2025-12-15 14:00:00', NULL, 5, 1),
    -- Bandol Rouge La Tourtine 2019 (seuil: 6)
    (18, 0, 18, 18, '2025-12-15 14:00:00', NULL, 6, 1),
    (4, 0, 4, 8, '2025-12-15 14:00:00', NULL, 6, 2),
    -- Krug Grande Cuvée (seuil: 10)
    (30, 3, 27, 30, '2025-12-15 14:00:00', NULL, 7, 1),
    (6, 0, 6, 12, '2025-12-15 14:00:00', NULL, 7, 2),
    -- Krug 2008 (seuil: 4)
    (12, 0, 12, 12, '2025-12-15 14:00:00', NULL, 8, 1),
    -- Hennessy XO (seuil: 8)
    (24, 2, 22, 24, '2025-12-15 14:00:00', NULL, 9, 1),
    -- Hennessy Paradis (seuil: 3)
    (8, 0, 8, 8, '2025-12-15 14:00:00', NULL, 10, 1);

-- =============================================
-- INVENTAIRE (1 inventaire terminé)
-- =============================================
INSERT INTO inventaires (nom, statut, date_debut, date_fin, utilisateur_id, notes, cree_le) VALUES
    ('Inventaire Annuel 2025', 'TERMINE', '2025-12-15 09:00:00', '2025-12-15 17:00:00', 1, 'Inventaire complet de fin d''année. Tous les stocks vérifiés et conformes. Aucun écart significatif constaté.', '2025-12-14 16:00:00');

-- =============================================
-- LIGNES INVENTAIRE (pour l'inventaire ci-dessus)
-- =============================================
INSERT INTO lignes_inventaire (inventaire_id, produit_id, unite_conditionnement_id, quantite_theorique, quantite_comptee, ecart, notes, scanne_le) VALUES
    (1, 1, 1, 24, 24, 0, NULL, '2025-12-15 10:15:00'),
    (1, 1, 2, 6, 6, 0, NULL, '2025-12-15 10:18:00'),
    (1, 2, 1, 6, 6, 0, 'Vérification double effectuée', '2025-12-15 10:25:00'),
    (1, 3, 1, 36, 36, 0, NULL, '2025-12-15 11:00:00'),
    (1, 3, 2, 8, 8, 0, NULL, '2025-12-15 11:05:00'),
    (1, 4, 1, 18, 18, 0, NULL, '2025-12-15 11:30:00'),
    (1, 4, 4, 12, 12, 0, NULL, '2025-12-15 11:35:00'),
    (1, 5, 1, 48, 48, 0, NULL, '2025-12-15 12:00:00'),
    (1, 6, 1, 18, 18, 0, NULL, '2025-12-15 14:00:00'),
    (1, 6, 2, 4, 4, 0, NULL, '2025-12-15 14:05:00'),
    (1, 7, 1, 30, 30, 0, NULL, '2025-12-15 14:30:00'),
    (1, 7, 2, 6, 6, 0, NULL, '2025-12-15 14:35:00'),
    (1, 8, 1, 12, 12, 0, NULL, '2025-12-15 15:00:00'),
    (1, 9, 1, 24, 24, 0, NULL, '2025-12-15 15:30:00'),
    (1, 10, 1, 8, 8, 0, NULL, '2025-12-15 16:00:00');
