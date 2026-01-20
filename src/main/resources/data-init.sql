/* password  = root */
INSERT INTO utilisateur (email, mot_de_passe, prenom, nom, telephone, role, actif, cree_le) VALUES
    ('simon@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Simon', 'Picot', '+33000000000', 'ADMIN', true, '2025-12-19 10:00:00'),
    ('terry@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Terry', 'Lagarde', '+33000000000', 'EMPLOYE', true, '2025-12-19 11:30:00'),
    ('cecilia@caveo.com', '$2a$10$plsl0aqMSsPoxBNFXS4gNuu2CbqN06PCGXFUkftYFTLbKLKfhEKDq', 'Cecilia', 'Tomassi', '+33000000000', 'CLIENT', true, '2025-12-19 14:15:00');
