# Documentation API Backoffice - Gestion des Vins

Base URL: `http://localhost:8080`

> **Note:** Toutes les routes de cette documentation nécessitent un token JWT avec le rôle `EMPLOYE` ou `ADMIN`.  
> Header requis: `Authorization: Bearer {token}`

---

## Table des matieres

1. [Categories](#categories)
2. [Domaines](#domaines)
3. [Unites de Conditionnement](#unites-de-conditionnement)
4. [Fournisseurs](#fournisseurs)
5. [Produits](#produits)
6. [Stock](#stock)
7. [Inventaires](#inventaires)
8. [Commandes Fournisseur](#commandes-fournisseur)
9. [Guide Frontend - Alertes Stock](#guide-frontend---alertes-stock)
10. [Guide Frontend - Scanner](#guide-frontend---scanner-inventaire)
11. [Codes d'erreur](#codes-derreur)
12. [Notes importantes](#notes-importantes)

---

## Catégories

### Liste des catégories
```http
GET /api/categories
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Vin Rouge",
    "description": "Vins rouges de toutes régions",
    "ordreTri": 1,
    "actif": true,
    "creeLe": "2026-01-15T10:00:00"
  },
  {
    "id": 2,
    "nom": "Vin Blanc",
    "description": "Vins blancs secs et moelleux",
    "ordreTri": 2,
    "actif": true,
    "creeLe": "2026-01-15T10:00:00"
  }
]
```

---

### Récupérer une catégorie
```http
GET /api/categories/{id}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "nom": "Vin Rouge",
  "description": "Vins rouges de toutes régions",
  "ordreTri": 1,
  "actif": true,
  "creeLe": "2026-01-15T10:00:00"
}
```

---

### Créer une catégorie
```http
POST /api/categories
```

**Body:**
```json
{
  "nom": "Champagne",
  "description": "Vins effervescents de Champagne",
  "ordreTri": 3
}
```

**Response:** `201 Created`
```json
{
  "id": 3,
  "nom": "Champagne",
  "description": "Vins effervescents de Champagne",
  "ordreTri": 3,
  "actif": true,
  "creeLe": "2026-01-21T14:30:00"
}
```

---

### Modifier une catégorie
```http
PUT /api/categories/{id}
```

**Body:**
```json
{
  "nom": "Champagne & Crémants",
  "description": "Vins effervescents",
  "ordreTri": 3,
  "actif": true
}
```

**Response:** `200 OK`

---

### Supprimer une catégorie (soft delete)
```http
DELETE /api/categories/{id}
```

**Response:** `204 No Content`

> **Note:** La catégorie est désactivée (`actif: false`), pas supprimée physiquement.

---

## Domaines

### Liste des domaines
```http
GET /api/domaines
GET /api/domaines?region=Bourgogne
GET /api/domaines?appellation=Saint-Émilion
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `region` | string | Filtrer par région |
| `appellation` | string | Filtrer par appellation |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Château Margaux",
    "region": "Bordeaux",
    "appellation": "Margaux",
    "surfaceVignobleHa": 87.00,
    "typeSol": "Graves, argilo-calcaire",
    "cepages": "Cabernet Sauvignon, Merlot, Petit Verdot",
    "vigneron": "Paul Pontallier",
    "description": "Premier Grand Cru Classé",
    "siteWeb": "https://www.chateau-margaux.com",
    "latitude": 45.04120000,
    "longitude": -0.67340000,
    "actif": true,
    "creeLe": "2026-01-15T10:00:00"
  }
]
```

---

### Récupérer un domaine
```http
GET /api/domaines/{id}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "nom": "Château Margaux",
  "region": "Bordeaux",
  "appellation": "Margaux",
  "surfaceVignobleHa": 87.00,
  "typeSol": "Graves, argilo-calcaire",
  "cepages": "Cabernet Sauvignon, Merlot, Petit Verdot",
  "vigneron": "Paul Pontallier",
  "description": "Premier Grand Cru Classé",
  "siteWeb": "https://www.chateau-margaux.com",
  "latitude": 45.04120000,
  "longitude": -0.67340000,
  "actif": true,
  "creeLe": "2026-01-15T10:00:00"
}
```

---

### Créer un domaine
```http
POST /api/domaines
```

**Body:**
```json
{
  "nom": "Domaine de la Romanée-Conti",
  "region": "Bourgogne",
  "appellation": "Vosne-Romanée",
  "surfaceVignobleHa": 25.50,
  "typeSol": "Argilo-calcaire",
  "cepages": "Pinot Noir",
  "vigneron": "Aubert de Villaine",
  "description": "Domaine mythique de Bourgogne",
  "siteWeb": "https://www.romanee-conti.fr"
}
```

**Response:** `201 Created`

---

### Modifier un domaine
```http
PUT /api/domaines/{id}
```

**Body:** (mêmes champs que la création)

**Response:** `200 OK`

---

### Supprimer un domaine (soft delete)
```http
DELETE /api/domaines/{id}
```

**Response:** `204 No Content`

---

## Unités de Conditionnement

### Liste des unités
```http
GET /api/unites-conditionnement
GET /api/unites-conditionnement?vendableOnly=true
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `vendableOnly` | boolean | Filtrer uniquement les unités vendables |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Bouteille 75cl",
    "nomCourt": "BT75",
    "quantiteUniteBase": 1,
    "description": "Bouteille standard 75cl",
    "dimensionsCm": "30x8",
    "poidsKg": 1.20,
    "volumeMl": 750,
    "estVendable": true,
    "estUniteBase": true,
    "ordreTri": 1,
    "actif": true,
    "creeLe": "2026-01-15T10:00:00"
  },
  {
    "id": 2,
    "nom": "Caisse de 6",
    "nomCourt": "CX6",
    "quantiteUniteBase": 6,
    "description": "Caisse carton de 6 bouteilles",
    "dimensionsCm": "35x25x30",
    "poidsKg": 8.50,
    "volumeMl": 4500,
    "estVendable": true,
    "estUniteBase": false,
    "ordreTri": 2,
    "actif": true,
    "creeLe": "2026-01-15T10:00:00"
  }
]
```

---

### Récupérer l'unité de base
```http
GET /api/unites-conditionnement/unite-base
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "nom": "Bouteille 75cl",
  "nomCourt": "BT75",
  "quantiteUniteBase": 1,
  "estUniteBase": true,
  ...
}
```

---

### Créer une unité
```http
POST /api/unites-conditionnement
```

**Body:**
```json
{
  "nom": "Magnum 1.5L",
  "nomCourt": "MAG",
  "quantiteUniteBase": 2,
  "description": "Bouteille Magnum 1.5 litres",
  "volumeMl": 1500,
  "poidsKg": 2.40,
  "estVendable": true,
  "estUniteBase": false,
  "ordreTri": 3
}
```

**Response:** `201 Created`

---

### Modifier une unité
```http
PUT /api/unites-conditionnement/{id}
```

**Response:** `200 OK`

---

### Supprimer une unité (soft delete)
```http
DELETE /api/unites-conditionnement/{id}
```

**Response:** `204 No Content`

---

## Fournisseurs

### Liste des fournisseurs
```http
GET /api/fournisseurs
GET /api/fournisseurs?bio=true
GET /api/fournisseurs?aoc=true
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `bio` | boolean | Filtrer fournisseurs certifiés BIO |
| `aoc` | boolean | Filtrer fournisseurs certifiés AOC |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Caves de Bordeaux",
    "personneContact": "Jean Dupont",
    "email": "contact@caves-bordeaux.fr",
    "telephone": "0556123456",
    "adresse": "123 Quai des Chartrons, 33000 Bordeaux",
    "conditionsPaiement": "30 jours fin de mois",
    "certificationBio": false,
    "certificationAoc": true,
    "certificationsAutres": "HVE3"
  }
]
```

---

### Récupérer un fournisseur
```http
GET /api/fournisseurs/{id}
```

**Response:** `200 OK`

---

### Produits d'un fournisseur
```http
GET /api/fournisseurs/{id}/produits
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "prixFournisseur": 8.50,
    "delaiApproJours": 5,
    "creeLe": "2026-01-15T10:00:00",
    "produit": {
      "id": 1,
      "sku": "VIN-RG-001",
      "nom": "Château Margaux 2018"
    },
    "fournisseur": {
      "id": 1,
      "nom": "Caves de Bordeaux"
    }
  }
]
```

---

### Créer un fournisseur
```http
POST /api/fournisseurs
```

**Body:**
```json
{
  "nom": "Vignobles du Rhône",
  "personneContact": "Marie Martin",
  "email": "contact@vignobles-rhone.fr",
  "telephone": "0478123456",
  "adresse": "45 Route des Vins, 26000 Valence",
  "conditionsPaiement": "60 jours",
  "certificationBio": true,
  "certificationAoc": true,
  "certificationsAutres": "Demeter, Biodynamie"
}
```

**Response:** `201 Created`

---

### Modifier un fournisseur
```http
PUT /api/fournisseurs/{id}
```

**Response:** `200 OK`

---

### Supprimer un fournisseur
```http
DELETE /api/fournisseurs/{id}
```

**Response:** `204 No Content`

---

## Produits

### Liste des produits
```http
GET /api/produits
GET /api/produits?categorieId=1
GET /api/produits?domaineId=2
GET /api/produits?millesime=2020
GET /api/produits?search=margaux
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `categorieId` | integer | Filtrer par catégorie |
| `domaineId` | integer | Filtrer par domaine |
| `millesime` | integer | Filtrer par millésime |
| `search` | string | Recherche par nom |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "sku": "VIN-RG-001",
    "nom": "Château Margaux 2018",
    "description": "Premier Grand Cru Classé",
    "millesime": 2018,
    "degreAlcool": 13.5,
    "codeBarre": "3760001234567",
    "imageUrl": "https://exemple.com/margaux2018.jpg",
    "notesDegustation": "Robe rubis profond, arômes de fruits noirs",
    "temperatureService": "16-18°C",
    "conditionsConservation": "Cave à 12-14°C",
    "seuilStockMinimal": 5,
    "reapproAuto": true,
    "actif": true,
    "creeLe": "2026-01-15T10:00:00",
    "modifieLe": null,
    "categorie": {
      "id": 1,
      "nom": "Vin Rouge"
    },
    "domaine": {
      "id": 1,
      "nom": "Château Margaux"
    },
    "conditionnements": [...],
    "fournisseurs": [...]
  }
]
```

---

### Récupérer un produit (vue agrégée)
```http
GET /api/produits/{id}
```

**Response:** `200 OK` (inclut conditionnements, fournisseurs, etc.)

---

### Récupérer un produit par SKU
```http
GET /api/produits/sku/{sku}
```

**Response:** `200 OK`

---

### Récupérer un produit par code-barre (scanner)
```http
GET /api/produits/code-barre/{codeBarre}
```

**Exemple:** `GET /api/produits/code-barre/3760001234567`

**Response:** `200 OK`
```json
{
  "id": 1,
  "sku": "VIN-RG-001",
  "nom": "Château Margaux 2018",
  "codeBarre": "3760001234567",
  ...
}
```

> **Usage:** Endpoint pour scanner de code-barres (EAN-13, UPC, etc.)

---

### Créer un produit
```http
POST /api/produits
```

**Body:**
```json
{
  "sku": "VIN-BL-002",
  "nom": "Chablis Grand Cru 2020",
  "description": "Vin blanc sec de Bourgogne",
  "millesime": 2020,
  "degreAlcool": 12.5,
  "codeBarre": "3760009876543",
  "temperatureService": "10-12°C",
  "seuilStockMinimal": 10,
  "reapproAuto": true,
  "categorie": { "id": 2 },
  "domaine": { "id": 3 }
}
```

**Response:** `201 Created`

---

### Modifier un produit
```http
PUT /api/produits/{id}
```

**Body:** (mêmes champs que la création)

**Response:** `200 OK`

---

### Supprimer un produit (soft delete)
```http
DELETE /api/produits/{id}
```

**Response:** `204 No Content`

---

### Conditionnements du produit

#### Liste
```http
GET /api/produits/{id}/conditionnements
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "prixUnitaire": 450.00,
    "disponible": true,
    "creeLe": "2026-01-15T10:00:00",
    "uniteConditionnement": {
      "id": 1,
      "nom": "Bouteille 75cl",
      "nomCourt": "BT75"
    }
  },
  {
    "id": 2,
    "prixUnitaire": 2500.00,
    "disponible": true,
    "creeLe": "2026-01-15T10:00:00",
    "uniteConditionnement": {
      "id": 2,
      "nom": "Caisse de 6",
      "nomCourt": "CX6"
    }
  }
]
```

#### Ajouter
```http
POST /api/produits/{id}/conditionnements
```

**Body:**
```json
{
  "uniteConditionnement": { "id": 2 },
  "prixUnitaire": 2500.00,
  "disponible": true
}
```

**Response:** `201 Created`

#### Modifier
```http
PUT /api/produits/{id}/conditionnements/{condId}
```

**Body:**
```json
{
  "prixUnitaire": 2600.00,
  "disponible": true
}
```

**Response:** `200 OK`

#### Supprimer
```http
DELETE /api/produits/{id}/conditionnements/{condId}
```

**Response:** `204 No Content`

---

### Fournisseurs du produit

#### Liste
```http
GET /api/produits/{id}/fournisseurs
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "prixFournisseur": 280.00,
    "delaiApproJours": 5,
    "creeLe": "2026-01-15T10:00:00",
    "fournisseur": {
      "id": 1,
      "nom": "Caves de Bordeaux"
    }
  }
]
```

#### Ajouter
```http
POST /api/produits/{id}/fournisseurs
```

**Body:**
```json
{
  "fournisseur": { "id": 2 },
  "prixFournisseur": 290.00,
  "delaiApproJours": 7
}
```

**Response:** `201 Created`

#### Modifier
```http
PUT /api/produits/{id}/fournisseurs/{fournId}
```

**Body:**
```json
{
  "prixFournisseur": 295.00,
  "delaiApproJours": 6
}
```

**Response:** `200 OK`

#### Supprimer
```http
DELETE /api/produits/{id}/fournisseurs/{fournId}
```

**Response:** `204 No Content`

---

### Stock du produit
```http
GET /api/produits/{id}/stock
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "quantite": 50,
    "quantiteReservee": 5,
    "quantiteDisponible": 45,
    "quantiteUniteBase": 50,
    "dernierInventaire": "2026-01-10T14:00:00",
    "modifieLe": null,
    "uniteConditionnement": {
      "id": 1,
      "nom": "Bouteille 75cl"
    }
  }
]
```

---

### Mouvements du produit
```http
GET /api/produits/{id}/mouvements
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "typeMouvement": "ENTREE",
    "quantite": 24,
    "quantiteUniteBase": 24,
    "typeReference": "COMMANDE_FOURNISSEUR",
    "referenceId": 5,
    "prixUnitaire": 280.00,
    "raison": "Réception commande CF-202601-0005",
    "numeroLot": "LOT2026-001",
    "datePeremption": null,
    "creeLe": "2026-01-20T10:00:00",
    "utilisateur": {
      "id": 3,
      "email": "employe@caveo.fr",
      "prenom": "Pierre",
      "nom": "Martin"
    }
  }
]
```

---

## Stock

### Stock complet
```http
GET /api/stock
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "quantite": 50,
    "quantiteReservee": 5,
    "quantiteDisponible": 45,
    "quantiteUniteBase": 50,
    "dernierInventaire": "2026-01-10T14:00:00",
    "modifieLe": null,
    "produit": {
      "id": 1,
      "sku": "VIN-RG-001",
      "nom": "Château Margaux 2018"
    },
    "uniteConditionnement": {
      "id": 1,
      "nom": "Bouteille 75cl"
    }
  }
]
```

---

### Alertes stock bas
```http
GET /api/stock/alertes
GET /api/stock/alertes?reapproAutoOnly=true
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `reapproAutoOnly` | boolean | Uniquement produits avec réappro auto activé |

**Response:** `200 OK`
```json
[
  {
    "id": 2,
    "quantite": 3,
    "quantiteDisponible": 3,
    "produit": {
      "id": 5,
      "sku": "VIN-BL-003",
      "nom": "Pouilly-Fumé 2021",
      "seuilStockMinimal": 5,
      "reapproAuto": true
    },
    "uniteConditionnement": {
      "id": 1,
      "nom": "Bouteille 75cl"
    }
  }
]
```

---

### Compteur alertes (badge frontend)
```http
GET /api/stock/alertes/count
```

**Response:** `200 OK`
```json
5
```

> **Usage frontend:** Appeler cet endpoint en polling (toutes les 30-60 secondes) pour afficher un badge de notification dans l'interface. Voir la section [Guide Frontend - Alertes Stock](#guide-frontend---alertes-stock).

---

### Enregistrer un mouvement
```http
POST /api/stock/mouvements
```

**Body:**
```json
{
  "produit": { "id": 1 },
  "uniteConditionnement": { "id": 1 },
  "typeMouvement": "ENTREE",
  "quantite": 12,
  "typeReference": "MANUEL",
  "raison": "Réception livraison directe",
  "prixUnitaire": 280.00,
  "numeroLot": "LOT2026-002"
}
```

**Types de mouvement:** `ENTREE`, `SORTIE`, `AJUSTEMENT`, `INVENTAIRE`

**Types de référence:** `COMMANDE_FOURNISSEUR`, `COMMANDE_CLIENT`, `INVENTAIRE`, `MANUEL`

**Response:** `201 Created`
```json
{
  "id": 15,
  "typeMouvement": "ENTREE",
  "quantite": 12,
  "quantiteUniteBase": 12,
  "typeReference": "MANUEL",
  "raison": "Réception livraison directe",
  "prixUnitaire": 280.00,
  "numeroLot": "LOT2026-002",
  "creeLe": "2026-01-21T15:30:00",
  "produit": {...},
  "uniteConditionnement": {...},
  "utilisateur": {...}
}
```

---

### Historique des mouvements d'un produit
```http
GET /api/stock/mouvements/produit/{produitId}
```

**Response:** `200 OK`

---

### Derniers mouvements (dashboard)
```http
GET /api/stock/mouvements/recents
GET /api/stock/mouvements/recents?limit=50
```

| Paramètre | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | integer | 20 | Nombre max de résultats |

**Response:** `200 OK`

---

### Réserver du stock
```http
POST /api/stock/reserver?produitId=1&uniteConditionnementId=1&quantite=5
```

**Response:** `200 OK`

---

### Libérer du stock réservé
```http
POST /api/stock/liberer?produitId=1&uniteConditionnementId=1&quantite=5
```

**Response:** `200 OK`

---

## Inventaires

### Workflow des inventaires

```
BROUILLON → EN_COURS → TERMINE
     ↓          ↓
   ANNULE    ANNULE
```

| Statut | Description |
|--------|-------------|
| `BROUILLON` | Inventaire créé, modifiable |
| `EN_COURS` | Lignes générées, comptage possible |
| `TERMINE` | Écarts appliqués au stock |
| `ANNULE` | Inventaire annulé |

---

### Liste des inventaires
```http
GET /api/inventaires
GET /api/inventaires?statut=EN_COURS
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `statut` | enum | Filtrer par statut |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Inventaire Janvier 2026",
    "statut": "TERMINE",
    "dateDebut": "2026-01-10T09:00:00",
    "dateFin": "2026-01-10T17:00:00",
    "notes": "Inventaire complet cave principale",
    "creeLe": "2026-01-09T14:00:00",
    "modifieLe": null,
    "utilisateur": {
      "id": 3,
      "email": "employe@caveo.fr",
      "prenom": "Pierre",
      "nom": "Martin"
    },
    "lignes": [...]
  }
]
```

---

### Récupérer un inventaire
```http
GET /api/inventaires/{id}
```

**Response:** `200 OK` (inclut les lignes)

---

### Créer un inventaire
```http
POST /api/inventaires
```

**Body:**
```json
{
  "nom": "Inventaire Février 2026",
  "notes": "Inventaire mensuel"
}
```

**Response:** `201 Created`
```json
{
  "id": 2,
  "nom": "Inventaire Février 2026",
  "statut": "BROUILLON",
  "dateDebut": null,
  "dateFin": null,
  "notes": "Inventaire mensuel",
  "creeLe": "2026-01-21T10:00:00",
  "utilisateur": {...},
  "lignes": []
}
```

---

### Modifier un inventaire
```http
PUT /api/inventaires/{id}
```

> ⚠️ Uniquement si statut = `BROUILLON`

**Body:**
```json
{
  "nom": "Inventaire Février 2026 - Cave A",
  "notes": "Focus sur la cave principale"
}
```

**Response:** `200 OK`

---

### Supprimer un inventaire
```http
DELETE /api/inventaires/{id}
```

> ⚠️ Uniquement si statut = `BROUILLON`

**Response:** `204 No Content`

---

### Démarrer l'inventaire
```http
POST /api/inventaires/{id}/demarrer
```

> **Action:** `BROUILLON` → `EN_COURS`  
> **Effet:** Génère automatiquement les lignes depuis le stock actuel

**Response:** `200 OK`
```json
{
  "id": 2,
  "nom": "Inventaire Février 2026",
  "statut": "EN_COURS",
  "dateDebut": "2026-01-21T10:15:00",
  "lignes": [
    {
      "id": 1,
      "quantiteAttendue": 50,
      "quantiteComptee": null,
      "difference": null,
      "statut": "EN_ATTENTE",
      "produit": { "id": 1, "nom": "Château Margaux 2018" },
      "uniteConditionnement": { "id": 1, "nom": "Bouteille 75cl" }
    }
  ]
}
```

---

### Terminer l'inventaire
```http
POST /api/inventaires/{id}/terminer
```

> **Action:** `EN_COURS` → `TERMINE`  
> **Effet:** Applique les écarts au stock via des mouvements d'inventaire

**Prérequis:** Toutes les lignes doivent être comptées

**Response:** `200 OK`

---

### Annuler l'inventaire
```http
POST /api/inventaires/{id}/annuler
```

> **Action:** `*` → `ANNULE` (sauf si `TERMINE`)

**Response:** `200 OK`

---

### Lignes de l'inventaire

#### Liste des lignes
```http
GET /api/inventaires/{id}/lignes
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "quantiteAttendue": 50,
    "quantiteComptee": 48,
    "difference": -2,
    "statut": "COMPTEE",
    "notes": "2 bouteilles cassées",
    "compteLe": "2026-01-21T11:30:00",
    "produit": {...},
    "uniteConditionnement": {...},
    "comptePar": {...}
  }
]
```

---

#### Lignes avec écart
```http
GET /api/inventaires/{id}/lignes/ecarts
```

**Response:** `200 OK` (uniquement les lignes où `difference != 0`)

---

#### Mettre à jour une ligne (comptage)
```http
PUT /api/inventaires/{id}/lignes/{ligneId}
```

**Body:**
```json
{
  "quantiteComptee": 48,
  "notes": "2 bouteilles cassées trouvées"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "quantiteAttendue": 50,
  "quantiteComptee": 48,
  "difference": -2,
  "statut": "COMPTEE",
  "notes": "2 bouteilles cassées trouvées",
  "compteLe": "2026-01-21T11:30:00",
  "comptePar": {...}
}
```

---

## Commandes Fournisseur

### Workflow des commandes

```
BROUILLON → ENVOYEE → CONFIRMEE → PARTIELLEMENT_RECUE → RECUE
     ↓         ↓          ↓
   ANNULEE  ANNULEE   ANNULEE
```

| Statut | Description |
|--------|-------------|
| `BROUILLON` | Commande en préparation, modifiable |
| `ENVOYEE` | Envoyée au fournisseur |
| `CONFIRMEE` | Fournisseur a confirmé |
| `PARTIELLEMENT_RECUE` | Réception partielle |
| `RECUE` | Totalement reçue |
| `ANNULEE` | Annulée |

---

### Liste des commandes
```http
GET /api/commandes-fournisseur
GET /api/commandes-fournisseur?statut=CONFIRMEE
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `statut` | enum | Filtrer par statut |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "numero": "CF-202601-0001",
    "statut": "CONFIRMEE",
    "dateCommande": "2026-01-15T10:00:00",
    "dateLivraisonPrevue": "2026-01-22T00:00:00",
    "montantTotal": 3360.00,
    "notes": "Commande mensuelle",
    "creeLe": "2026-01-14T16:00:00",
    "modifieLe": null,
    "fournisseur": {
      "id": 1,
      "nom": "Caves de Bordeaux"
    },
    "creePar": {
      "id": 3,
      "email": "employe@caveo.fr",
      "prenom": "Pierre",
      "nom": "Martin"
    },
    "lignes": [...]
  }
]
```

---

### Commandes en attente de réception
```http
GET /api/commandes-fournisseur/en-attente
```

**Response:** `200 OK` (commandes `ENVOYEE`, `CONFIRMEE`, `PARTIELLEMENT_RECUE`)

---

### Récupérer une commande
```http
GET /api/commandes-fournisseur/{id}
```

**Response:** `200 OK` (inclut les lignes)

---

### Créer une commande
```http
POST /api/commandes-fournisseur?fournisseurId=1&notes=Commande urgente
```

| Paramètre | Type | Required | Description |
|-----------|------|----------|-------------|
| `fournisseurId` | integer | ✅ | ID du fournisseur |
| `notes` | string | ❌ | Notes |

**Response:** `201 Created`
```json
{
  "id": 5,
  "numero": "CF-202601-0005",
  "statut": "BROUILLON",
  "dateCommande": null,
  "dateLivraisonPrevue": null,
  "montantTotal": 0.00,
  "notes": "Commande urgente",
  "creeLe": "2026-01-21T14:00:00",
  "fournisseur": {...},
  "creePar": {...},
  "lignes": []
}
```

---

### Modifier une commande
```http
PUT /api/commandes-fournisseur/{id}?dateLivraisonPrevue=2026-01-25T00:00:00&notes=MAJ notes
```

> ⚠️ Uniquement si statut = `BROUILLON`

**Response:** `200 OK`

---

### Supprimer une commande
```http
DELETE /api/commandes-fournisseur/{id}
```

> ⚠️ Uniquement si statut = `BROUILLON`

**Response:** `204 No Content`

---

### Ajouter une ligne
```http
POST /api/commandes-fournisseur/{id}/lignes
```

> ⚠️ Uniquement si statut = `BROUILLON`

**Body:**
```json
{
  "produitId": 1,
  "uniteConditionnementId": 1,
  "quantite": 12,
  "prixUnitaire": 280.00
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "quantite": 12,
  "prixUnitaire": 280.00,
  "prixTotal": 3360.00,
  "quantiteRecue": 0,
  "produit": {...},
  "uniteConditionnement": {...}
}
```

---

### Supprimer une ligne
```http
DELETE /api/commandes-fournisseur/{id}/lignes/{ligneId}
```

> ⚠️ Uniquement si statut = `BROUILLON`

**Response:** `204 No Content`

---

### Envoyer la commande
```http
POST /api/commandes-fournisseur/{id}/envoyer
```

> **Action:** `BROUILLON` → `ENVOYEE`

**Prérequis:** La commande doit avoir au moins une ligne

**Response:** `200 OK`
```json
{
  "id": 5,
  "numero": "CF-202601-0005",
  "statut": "ENVOYEE",
  "dateCommande": "2026-01-21T14:30:00",
  ...
}
```

---

### Confirmer la commande
```http
POST /api/commandes-fournisseur/{id}/confirmer?dateLivraisonPrevue=2026-01-28T00:00:00
```

> **Action:** `ENVOYEE` → `CONFIRMEE`

| Paramètre | Type | Required | Description |
|-----------|------|----------|-------------|
| `dateLivraisonPrevue` | datetime | ❌ | Date de livraison prévue |

**Response:** `200 OK`

---

### Réceptionner des produits
```http
POST /api/commandes-fournisseur/{id}/reception
```

> **Action:** `ENVOYEE/CONFIRMEE/PARTIELLEMENT_RECUE` → `PARTIELLEMENT_RECUE/RECUE`  
> **Effet:** Crée automatiquement des mouvements d'entrée de stock

**Body:**
```json
{
  "lignes": [
    {
      "ligneId": 1,
      "quantiteRecue": 10,
      "numeroLot": "LOT2026-015",
      "datePeremption": "2030-12-31"
    },
    {
      "ligneId": 2,
      "quantiteRecue": 6
    }
  ]
}
```

**Response:** `200 OK`
```json
{
  "id": 5,
  "statut": "PARTIELLEMENT_RECUE",
  "lignes": [
    {
      "id": 1,
      "quantite": 12,
      "quantiteRecue": 10,
      "numeroLot": "LOT2026-015",
      ...
    }
  ]
}
```

---

### Annuler la commande
```http
POST /api/commandes-fournisseur/{id}/annuler
```

> **Action:** `*` → `ANNULEE`  
> ⚠️ Impossible si `RECUE` ou `PARTIELLEMENT_RECUE`

**Response:** `200 OK`

---

### Générer des commandes automatiques
```http
POST /api/commandes-fournisseur/auto/generer
```

> **Effet:** Crée des commandes pour tous les produits sous seuil avec `reapproAuto=true`

**Response:** `200 OK`
```json
[
  {
    "id": 10,
    "numero": "CF-202601-0010",
    "statut": "BROUILLON",
    "notes": "Commande automatique - Stock bas pour: Pouilly-Fumé 2021",
    "fournisseur": {...},
    "lignes": [...]
  }
]
```

---

## Codes d'erreur

### 400 Bad Request
```json
{
  "message": "La quantité doit être supérieure à 0",
  "status": 400
}
```

### 401 Unauthorized
```json
{
  "message": "Token JWT invalide ou expiré",
  "status": 401
}
```

### 403 Forbidden
```json
{
  "message": "Accès refusé - Rôle EMPLOYE ou ADMIN requis",
  "status": 403
}
```

### 404 Not Found
```json
{
  "message": "Produit avec l'id 999 n'existe pas",
  "status": 404
}
```

### 409 Conflict
```json
{
  "message": "Un produit avec le SKU 'VIN-RG-001' existe déjà",
  "status": 409
}
```

---

## Guide Frontend - Alertes Stock

Cette section explique comment implémenter le système d'alertes stock dans le frontend.

### Workflow complet

```
1. Polling badge       GET /api/stock/alertes/count (toutes les 30-60s)
         |
         v
2. Si count > 0  -->   Afficher badge rouge avec le nombre
         |
         v
3. Clic sur badge      GET /api/stock/alertes (liste complète)
         |
         v
4. Afficher tableau    Produit | Stock | Seuil | Manque | Réappro
         |
         v
5. Bouton "Générer"    POST /api/commandes-fournisseur/auto/generer
```

### Implémentation du polling

```javascript
// Exemple React/Vue - polling toutes les 30 secondes
const [alertCount, setAlertCount] = useState(0);

useEffect(() => {
  const fetchAlertCount = async () => {
    const response = await api.get('/api/stock/alertes/count');
    setAlertCount(response.data);
  };
  
  fetchAlertCount();
  const interval = setInterval(fetchAlertCount, 30000);
  return () => clearInterval(interval);
}, []);
```

### Affichage du tableau des alertes

| Colonne | Source | Description |
|---------|--------|-------------|
| Produit | `stock.produit.nom` | Nom du produit |
| SKU | `stock.produit.sku` | Référence unique |
| Stock actuel | `stock.quantite` | Quantité en stock |
| Seuil minimal | `stock.produit.seuilStockMinimal` | Seuil d'alerte |
| Manque | `seuil - stock` | Quantité à commander |
| Réappro auto | `stock.produit.reapproAuto` | Si éligible commande auto |

### Génération automatique des commandes

Le bouton "Générer commandes automatiques" doit:

1. Appeler `POST /api/commandes-fournisseur/auto/generer`
2. Le backend crée des commandes **groupées par fournisseur**
3. Seuls les produits avec `reapproAuto: true` sont inclus
4. Rediriger vers la liste des commandes ou afficher un message de confirmation

**Response exemple:**
```json
[
  {
    "id": 15,
    "numeroCommande": "CMD-2026-0015",
    "statut": "BROUILLON",
    "fournisseur": { "nom": "Grands Vins de Bordeaux" },
    "lignes": [
      { "produit": { "nom": "Château Margaux 2018" }, "quantiteCommandee": 8 },
      { "produit": { "nom": "Château Latour 2019" }, "quantiteCommandee": 5 }
    ]
  },
  {
    "id": 16,
    "numeroCommande": "CMD-2026-0016",
    "fournisseur": { "nom": "Domaines de Bourgogne" },
    "lignes": [...]
  }
]
```

### Produits sans réappro auto

Les produits avec `reapproAuto: false` apparaissent dans les alertes mais ne sont **pas inclus** dans la génération automatique. L'opérateur doit créer manuellement une commande pour ces produits via `POST /api/commandes-fournisseur`.

> **Note:** Le champ `reapproAuto` peut être modifié à tout moment via `PUT /api/produits/{id}` avec `{ "reapproAuto": true }`. Le produit sera alors inclus dans les prochaines générations automatiques.

---

## Guide Frontend - Scanner (Inventaire)

Cette section explique comment implémenter le workflow scanner pour les inventaires.

### Workflow inventaire avec scanner

```
1. Créer inventaire    POST /api/inventaires
         |
         v
2. Démarrer            POST /api/inventaires/{id}/demarrer
         |
         v
3. Scanner produit     GET /api/produits/code-barre/{codeBarre}
         |
         v
4. Afficher produit    Nom, stock théorique, conditionnement
         |
         v
5. Saisir quantité     PUT /api/inventaires/{id}/lignes/{ligneId}
         |
         v
6. Répéter 3-5         Pour chaque produit
         |
         v
7. Terminer            POST /api/inventaires/{id}/terminer
```

### Recherche par code-barre

```http
GET /api/produits/code-barre/{codeBarre}
```

**Response:** `200 OK` - Le produit correspondant au code-barre

**Response:** `404 Not Found` - Code-barre non reconnu

### Interface recommandée

1. **Champ de saisie** autofocus pour scanner
2. **Affichage produit** trouvé avec photo si disponible
3. **Input numérique** pour la quantité comptée
4. **Écart calculé** automatiquement (compté - théorique)
5. **Bouton validation** puis retour au scan

---

## Notes importantes

1. **Authentification:** Toutes les routes nécessitent `@IsEmploye` (rôle `EMPLOYE` ou `ADMIN`).

2. **Soft Delete:** Les entités principales (Catégorie, Domaine, Produit, etc.) ne sont pas supprimées mais désactivées (`actif: false`).

3. **Stock automatique:** 
   - Les mouvements mettent à jour automatiquement `stock_actuel`
   - Les réceptions de commandes créent des mouvements d'entrée
   - Les inventaires terminés créent des mouvements de type `INVENTAIRE`

4. **Alertes stock:** Les produits passant sous `seuilStockMinimal` sont signalés dans `/api/stock/alertes`. Utiliser `/api/stock/alertes/count` pour le badge frontend.

5. **Commandes automatiques:** `/api/commandes-fournisseur/auto/generer` crée des commandes **groupées par fournisseur** pour les produits avec `reapproAuto: true` et stock bas.
