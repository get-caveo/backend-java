# Documentation API Complète - Caveo (Gestion des Vins)

Base URL: `http://localhost:8080`

---

## Table des matières

### Authentification & Sécurité
1. [Authentification](#1-authentification)
2. [Rôles et autorisations](#2-rôles-et-autorisations)

### Front Office (Public & Client)
3. [Catalogue public](#3-catalogue-public-sans-authentification)
4. [Panier](#4-panier-client)
5. [Commandes client](#5-commandes-client)
6. [Paiements](#6-paiements-client)
7. [Adresses](#7-adresses-client)
8. [Profil utilisateur](#8-profil-utilisateur-client)

### Back Office (Employé & Admin)
9. [Catégories](#9-catégories-backoffice)
10. [Domaines](#10-domaines-backoffice)
11. [Unités de conditionnement](#11-unités-de-conditionnement-backoffice)
12. [Fournisseurs](#12-fournisseurs-backoffice)
13. [Produits](#13-produits-backoffice)
14. [Stock](#14-stock-backoffice)
15. [Inventaires](#15-inventaires-backoffice)
16. [Commandes fournisseur](#16-commandes-fournisseur-backoffice)
17. [Commandes client (gestion backoffice)](#17-commandes-client---gestion-backoffice)
18. [Notifications & WebSocket](#18-notifications--websocket-backoffice)
19. [Gestion utilisateurs (admin)](#19-gestion-utilisateurs-admin)

### Annexes
20. [Codes d'erreur](#20-codes-derreur)
21. [Enums de référence](#21-enums-de-référence)
22. [Guide Frontend - Alertes Stock](#22-guide-frontend---alertes-stock)
23. [Guide Frontend - Scanner Inventaire](#23-guide-frontend---scanner-inventaire)
24. [Guide Frontend - Notifications WebSocket](#24-guide-frontend---notifications-websocket)

---

## 1. Authentification

Base path: `/api/auth`

> **Accès:** Public (aucun token requis)

---

### Login
```http
POST /api/auth/login
```

**Body:**
```json
{
  "email": "client@example.fr",
  "password": "motdepasse123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": 1,
  "email": "client@example.fr",
  "prenom": "Marie",
  "nom": "Dupont",
  "role": "CLIENT"
}
```

**Erreurs:**
- `401` — Email ou mot de passe incorrect
- `403` — Compte désactivé

---

### Inscription (création de compte client)
```http
POST /api/auth/register
```

**Body:**
```json
{
  "email": "nouveau@example.fr",
  "password": "motdepasse123",
  "prenom": "Jean",
  "nom": "Martin",
  "telephone": "0612345678"
}
```

**Response:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": 5,
  "email": "nouveau@example.fr",
  "prenom": "Jean",
  "nom": "Martin",
  "role": "CLIENT"
}
```

> **Note:** Le rôle est automatiquement `CLIENT`. Le token JWT est retourné directement, le client est connecté immédiatement après l'inscription.

**Erreurs:**
- `409` — Un compte avec cet email existe déjà

---

### Utilisateur courant
```http
GET /api/auth/me
```

**Header:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "client@example.fr",
  "prenom": "Marie",
  "nom": "Dupont",
  "role": "CLIENT",
  "actif": true
}
```

---

## 2. Rôles et autorisations

| Rôle | Description | Accès |
|------|-------------|-------|
| `CLIENT` | Client e-commerce | Catalogue public, panier, commandes, paiements, profil |
| `EMPLOYE` | Employé backoffice | Tout ce que CLIENT peut + backoffice complet |
| `ADMIN` | Administrateur | Tout ce que EMPLOYE peut + gestion utilisateurs/rôles |

### Annotations de sécurité

| Annotation | Rôles autorisés | Utilisée pour |
|-----------|----------------|---------------|
| `@IsClient` | CLIENT, EMPLOYE, ADMIN | Panier, commandes client, profil |
| `@IsEmploye` | EMPLOYE, ADMIN | Backoffice (produits, stock, etc.) |
| `@IsAdmin` | ADMIN | Gestion des utilisateurs |

### Header d'authentification

```
Authorization: Bearer {token_jwt}
```

Le token JWT expire après **24 heures** et contient : `email`, `role`, `id`.

---

## 3. Catalogue public (sans authentification)

Base path: `/api/public`

> **Accès:** Public — aucun token requis

---

### Liste des produits
```http
GET /api/public/produits
GET /api/public/produits?categorieId=1
GET /api/public/produits?domaineId=2
GET /api/public/produits?millesime=2020
GET /api/public/produits?search=margaux
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
    "actif": true,
    "categorie": { "id": 1, "nom": "Vin Rouge" },
    "domaine": { "id": 1, "nom": "Château Margaux" },
    "conditionnements": [
      {
        "id": 1,
        "prixUnitaire": 450.00,
        "disponible": true,
        "uniteConditionnement": { "id": 1, "nom": "Bouteille 75cl", "nomCourt": "BT75" }
      }
    ]
  }
]
```

---

### Détail d'un produit
```http
GET /api/public/produits/{id}
```

**Response:** `200 OK` — Produit complet avec conditionnements, domaine, catégorie, fournisseurs.

---

### Liste des catégories
```http
GET /api/public/categories
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Vin Rouge",
    "description": "Vins rouges de toutes régions",
    "ordreTri": 1,
    "actif": true
  }
]
```

---

### Détail d'une catégorie
```http
GET /api/public/categories/{id}
```

---

### Liste des domaines
```http
GET /api/public/domaines
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Château Margaux",
    "region": "Bordeaux",
    "appellation": "Margaux",
    "actif": true
  }
]
```

---

### Détail d'un domaine
```http
GET /api/public/domaines/{id}
```

---

## 4. Panier (Client)

Base path: `/api/panier`

> **Accès:** `@IsClient` — Token JWT requis (CLIENT, EMPLOYE ou ADMIN)

---

### Voir mon panier
```http
GET /api/panier
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "client": {
    "id": 5,
    "email": "client@example.fr",
    "prenom": "Marie",
    "nom": "Dupont"
  },
  "creeLe": "2026-01-20T14:00:00",
  "modifieLe": "2026-01-21T10:30:00",
  "lignes": [
    {
      "id": 1,
      "produit": {
        "id": 1,
        "sku": "VIN-RG-001",
        "nom": "Château Margaux 2018"
      },
      "uniteConditionnement": {
        "id": 1,
        "nom": "Bouteille 75cl",
        "nomCourt": "BT75"
      },
      "quantite": 2,
      "ajouteLe": "2026-01-20T14:05:00"
    }
  ]
}
```

> **Note:** Si le panier n'existe pas encore, il est créé automatiquement (vide).

---

### Ajouter un article au panier
```http
POST /api/panier/articles
```

**Body:**
```json
{
  "produitId": 1,
  "uniteConditionnementId": 1,
  "quantite": 2
}
```

**Response:** `200 OK` — Le panier complet mis à jour.

> **Note:** Si le même produit+conditionnement existe déjà dans le panier, les quantités sont fusionnées.

**Erreurs:**
- `404` — Produit ou unité de conditionnement introuvable
- `400` — Ce conditionnement n'est pas disponible pour ce produit

---

### Modifier la quantité d'un article
```http
PUT /api/panier/articles/{ligneId}?quantite=5
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `quantite` | integer | Nouvelle quantité (si ≤ 0, l'article est supprimé) |

**Response:** `200 OK` — Le panier complet mis à jour.

---

### Supprimer un article du panier
```http
DELETE /api/panier/articles/{ligneId}
```

**Response:** `200 OK` — Le panier complet mis à jour.

---

### Vider le panier
```http
DELETE /api/panier
```

**Response:** `204 No Content`

---

## 5. Commandes client

Base path: `/api/commandes-client`

### Workflow des commandes client

```
EN_ATTENTE → CONFIRMEE → EN_PREPARATION → EXPEDIEE → LIVREE
     ↓            ↓              ↓
              ANNULEE
```

| Statut | Description | Qui peut agir |
|--------|-------------|---------------|
| `EN_ATTENTE` | Commande créée, en attente de paiement | Client |
| `CONFIRMEE` | Paiement reçu, stock réservé | (auto après paiement) |
| `EN_PREPARATION` | En cours de préparation | Employé |
| `EXPEDIEE` | Colis expédié, stock déduit | Employé |
| `LIVREE` | Livré au client | Employé |
| `ANNULEE` | Annulée (stock libéré si réservé) | Client ou Employé |

---

### Créer une commande (depuis le panier)
```http
POST /api/commandes-client?adresseLivraisonId=1&adresseFacturationId=2&notes=Merci
```

> **Accès:** `@IsClient`

| Paramètre | Type | Required | Description |
|-----------|------|----------|-------------|
| `adresseLivraisonId` | integer | ❌ | ID de l'adresse de livraison |
| `adresseFacturationId` | integer | ❌ | ID de l'adresse de facturation |
| `notes` | string | ❌ | Notes du client |

**Response:** `201 Created`
```json
{
  "id": 1,
  "numero": "CC-202601-0001",
  "statutCommande": "EN_ATTENTE",
  "dateCommande": "2026-01-21T15:30:00",
  "sousTotal": 900.00,
  "fraisLivraison": 0.00,
  "montantTaxes": 0.00,
  "montantTotal": 900.00,
  "notes": "Merci",
  "client": { "id": 5, "email": "client@example.fr", "prenom": "Marie", "nom": "Dupont" },
  "adresseLivraison": { "id": 1, "rue": "12 Rue de la Vigne", "ville": "Paris", "codePostal": "75001", "pays": "France" },
  "adresseFacturation": { "id": 2, "rue": "12 Rue de la Vigne", "ville": "Paris", "codePostal": "75001", "pays": "France" },
  "lignes": [
    {
      "id": 1,
      "produit": { "id": 1, "nom": "Château Margaux 2018" },
      "uniteConditionnement": { "id": 1, "nom": "Bouteille 75cl" },
      "quantite": 2,
      "prixUnitaire": 450.00,
      "prixTotal": 900.00
    }
  ],
  "creeLe": "2026-01-21T15:30:00"
}
```

> **Effets:**
> - Les prix sont récupérés depuis `ConditionnementProduit` (pas depuis le panier)
> - Le panier est vidé automatiquement
> - Une notification `COMMANDE_RECUE` est envoyée au backoffice via WebSocket

**Erreurs:**
- `400` — Panier vide ou introuvable

---

### Mes commandes (historique client)
```http
GET /api/commandes-client/mes-commandes
```

> **Accès:** `@IsClient`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "numero": "CC-202601-0001",
    "statutCommande": "CONFIRMEE",
    "dateCommande": "2026-01-21T15:30:00",
    "montantTotal": 900.00,
    "client": { "id": 5, "prenom": "Marie", "nom": "Dupont" },
    "lignes": [...]
  }
]
```

> Triées par date de création décroissante (plus récentes en premier).

---

### Détail d'une commande
```http
GET /api/commandes-client/{id}
```

> **Accès:** `@IsClient`

**Response:** `200 OK` — Commande complète avec lignes, produits, adresses et dates.

```json
{
  "id": 1,
  "numero": "CC-202601-0001",
  "statutCommande": "EXPEDIEE",
  "dateCommande": "2026-01-21T15:30:00",
  "dateExpedition": "2026-01-23T09:00:00",
  "dateLivraison": null,
  "sousTotal": 900.00,
  "fraisLivraison": 0.00,
  "montantTaxes": 0.00,
  "montantTotal": 900.00,
  "notes": "Merci",
  "creeLe": "2026-01-21T15:30:00",
  "modifieLe": "2026-01-23T09:00:00",
  "client": { "id": 5, "email": "client@example.fr", "prenom": "Marie", "nom": "Dupont" },
  "adresseLivraison": {
    "id": 1,
    "type": "LIVRAISON",
    "rue": "12 Rue de la Vigne",
    "ville": "Paris",
    "codePostal": "75001",
    "pays": "France"
  },
  "adresseFacturation": {
    "id": 2,
    "type": "FACTURATION",
    "rue": "12 Rue de la Vigne",
    "ville": "Paris",
    "codePostal": "75001",
    "pays": "France"
  },
  "lignes": [
    {
      "id": 1,
      "produit": {
        "id": 1,
        "sku": "VIN-RG-001",
        "nom": "Château Margaux 2018"
      },
      "uniteConditionnement": {
        "id": 1,
        "nom": "Bouteille 75cl",
        "nomCourt": "BT75"
      },
      "quantite": 2,
      "prixUnitaire": 450.00,
      "prixTotal": 900.00
    }
  ]
}
```

---

### Annuler une commande
```http
POST /api/commandes-client/{id}/annuler
```

> **Accès:** `@IsClient`

**Response:** `200 OK` — La commande avec `statutCommande: "ANNULEE"`.

> **Effets:**
> - Si la commande était CONFIRMEE ou EN_PREPARATION, le stock réservé est libéré
> - Impossible d'annuler une commande EXPEDIEE ou LIVREE

---

## 6. Paiements (Client)

Base path: `/api/paiements`

> **Accès:** `@IsClient` — Token JWT requis

---

### Effectuer un paiement (mock)
```http
POST /api/paiements/commande/{commandeId}
```

**Body:**
```json
{
  "methodePaiement": "CARTE",
  "referenceTransaction": null,
  "detailsPaiement": "Visa se terminant par 4242"
}
```

| Champ | Type | Required | Description |
|-------|------|----------|-------------|
| `methodePaiement` | enum | ✅ | `CARTE`, `PAYPAL` ou `VIREMENT` |
| `referenceTransaction` | string | ❌ | Référence externe (auto-générée si absente) |
| `detailsPaiement` | string | ❌ | Détails supplémentaires |

**Response:** `201 Created`
```json
{
  "id": 1,
  "commandeClient": { "id": 1, "numero": "CC-202601-0001" },
  "montant": 900.00,
  "methodePaiement": "CARTE",
  "statutPaiement": "COMPLET",
  "referenceTransaction": "FICTIF-A3B7C9D2",
  "detailsPaiement": "Visa se terminant par 4242",
  "payeLe": "2026-01-21T15:35:00"
}
```

> **Effets (mock):**
> - Le paiement passe directement en statut `COMPLET` (simulation)
> - La commande est automatiquement confirmée (`EN_ATTENTE` → `CONFIRMEE`)
> - Le stock est réservé pour chaque ligne de la commande
> - Si `referenceTransaction` est null, une référence fictive `FICTIF-XXXXXXXX` est générée

**Erreurs:**
- `400` — La commande n'est pas en statut EN_ATTENTE
- `409` — Un paiement existe déjà pour cette commande

---

### Consulter le paiement d'une commande
```http
GET /api/paiements/commande/{commandeId}
```

**Response:** `200 OK` — Le paiement associé.

**Erreurs:**
- `404` — Aucun paiement trouvé pour cette commande

---

## 7. Adresses (Client)

Base path: `/adresse`

> **Accès:** `@IsClient` — Token JWT requis
> **Note:** Les adresses sont liées automatiquement à l'utilisateur connecté.

---

### Liste de mes adresses
```http
GET /adresse/liste
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "type": "LIVRAISON",
    "rue": "12 Rue de la Vigne",
    "ville": "Paris",
    "codePostal": "75001",
    "pays": "France",
    "parDefaut": true
  },
  {
    "id": 2,
    "type": "FACTURATION",
    "rue": "12 Rue de la Vigne",
    "ville": "Paris",
    "codePostal": "75001",
    "pays": "France",
    "parDefaut": false
  }
]
```

---

### Récupérer une adresse
```http
GET /adresse/{id}
```

**Response:** `200 OK`

**Erreurs:**
- `403` — Cette adresse ne vous appartient pas

---

### Créer une adresse
```http
POST /adresse
```

**Body:**
```json
{
  "type": "LIVRAISON",
  "rue": "45 Avenue des Vignerons",
  "ville": "Lyon",
  "codePostal": "69001",
  "pays": "France",
  "parDefaut": true
}
```

| Champ | Type | Required | Valeurs |
|-------|------|----------|---------|
| `type` | enum | ✅ | `FACTURATION`, `LIVRAISON` |
| `rue` | string | ✅ | |
| `ville` | string | ✅ | |
| `codePostal` | string | ✅ | |
| `pays` | string | ✅ | |
| `parDefaut` | boolean | ❌ | Si `true`, les autres adresses perdent le flag par défaut |

**Response:** `201 Created`

---

### Modifier une adresse
```http
PUT /adresse/{id}
```

**Body:** Mêmes champs que la création.

**Response:** `200 OK`

**Erreurs:**
- `403` — Cette adresse ne vous appartient pas

---

### Supprimer une adresse
```http
DELETE /adresse/{id}
```

**Response:** `204 No Content`

**Erreurs:**
- `403` — Cette adresse ne vous appartient pas

---

## 8. Profil utilisateur (Client)

Base path: `/utilisateur`

> **Accès:** `@IsClient` — Token JWT requis

---

### Voir mon profil
```http
GET /utilisateur/{id}
```

> Chaque utilisateur ne peut consulter que **son propre profil**.

**Response:** `200 OK`
```json
{
  "id": 5,
  "email": "client@example.fr",
  "prenom": "Marie",
  "nom": "Dupont",
  "telephone": "0612345678",
  "role": "CLIENT",
  "actif": true,
  "creeLe": "2026-01-15T10:00:00",
  "modifieLe": null,
  "adresses": [
    { "id": 1, "type": "LIVRAISON", "rue": "12 Rue de la Vigne", "ville": "Paris", "codePostal": "75001", "pays": "France", "parDefaut": true }
  ]
}
```

---

### Modifier mon profil
```http
PUT /utilisateur/{id}
```

**Body (champs modifiables par le propriétaire):**
```json
{
  "email": "nouveau@example.fr",
  "prenom": "Marie-Claire",
  "nom": "Dupont-Martin",
  "telephone": "0698765432"
}
```

**Response:** `200 OK`

> **Règles:**
> - Chaque utilisateur ne peut modifier que **son propre profil**
> - Un ADMIN peut en plus modifier le `role` et `actif` de n'importe quel utilisateur
> - L'email doit être unique

---

### Supprimer mon compte
```http
DELETE /utilisateur/{id}
```

**Response:** `204 No Content`

> Le propriétaire peut supprimer son compte. Un ADMIN peut supprimer n'importe quel compte.

---

## 9. Catégories (Backoffice)

Base path: `/api/categories`

> **Accès:** `@IsEmploye` — Rôle EMPLOYE ou ADMIN requis

---

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
  }
]
```

---

### Récupérer une catégorie
```http
GET /api/categories/{id}
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

**Erreurs:**
- `409` — Une catégorie avec ce nom existe déjà

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

> La catégorie est désactivée (`actif: false`), pas supprimée physiquement.

---

## 10. Domaines (Backoffice)

Base path: `/api/domaines`

> **Accès:** `@IsEmploye`

---

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

**Body:** Mêmes champs que la création.

**Response:** `200 OK`

---

### Supprimer un domaine (soft delete)
```http
DELETE /api/domaines/{id}
```

**Response:** `204 No Content`

---

## 11. Unités de conditionnement (Backoffice)

Base path: `/api/unites-conditionnement`

> **Accès:** `@IsEmploye`

---

### Liste des unités
```http
GET /api/unites-conditionnement
GET /api/unites-conditionnement?vendableOnly=true
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `vendableOnly` | boolean | Uniquement les unités vendables |

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

**Response:** `200 OK` — L'unité de conditionnement marquée `estUniteBase: true`.

---

### Récupérer une unité
```http
GET /api/unites-conditionnement/{id}
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

**Erreurs:**
- `409` — Une unité avec ce nom ou nom court existe déjà
- `409` — Une unité de base existe déjà (si `estUniteBase: true`)

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

## 12. Fournisseurs (Backoffice)

Base path: `/api/fournisseurs`

> **Accès:** `@IsEmploye`

---

### Liste des fournisseurs
```http
GET /api/fournisseurs
GET /api/fournisseurs?bio=true
GET /api/fournisseurs?aoc=true
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| `bio` | boolean | Fournisseurs certifiés BIO |
| `aoc` | boolean | Fournisseurs certifiés AOC |

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
    "produit": { "id": 1, "sku": "VIN-RG-001", "nom": "Château Margaux 2018" },
    "fournisseur": { "id": 1, "nom": "Caves de Bordeaux" }
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

**Erreurs:**
- `409` — Un fournisseur avec ce nom ou email existe déjà

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

> **Attention:** Suppression physique (hard delete). Échoue si des produits sont liés (foreign key).

---

## 13. Produits (Backoffice)

Base path: `/api/produits`

> **Accès:** `@IsEmploye`

---

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

**Response:** `200 OK` — Liste complète avec catégorie, domaine, conditionnements, fournisseurs.

---

### Récupérer un produit (vue agrégée)
```http
GET /api/produits/{id}
```

**Response:** `200 OK` — Inclut toutes les relations (conditionnements, fournisseurs, catégorie, domaine).

---

### Récupérer par SKU
```http
GET /api/produits/sku/{sku}
```

---

### Récupérer par code-barre (scanner)
```http
GET /api/produits/code-barre/{codeBarre}
```

> **Usage:** Scanner de codes-barres (EAN-13, UPC, etc.)

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
  "categorieId": 2,
  "domaineId": 3,
  "millesime": 2020,
  "degreAlcool": 12.5,
  "codeBarre": "3760009876543",
  "imageUrl": "https://exemple.com/chablis.jpg",
  "notesDegustation": "Fleurs blanches, minéralité",
  "temperatureService": "10-12°C",
  "conditionsConservation": "Cave à 12°C",
  "seuilStockMinimal": 10,
  "reapproAuto": true,
  "fournisseurId": 1,
  "prixFournisseur": 15.00,
  "delaiApproJours": 5
}
```

| Champ | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `sku` | string | ✅ | | Référence unique |
| `nom` | string | ✅ | | Nom du produit |
| `categorieId` | integer | ✅ | | ID catégorie |
| `fournisseurId` | integer | ✅ | | ID fournisseur principal |
| `domaineId` | integer | ❌ | | ID domaine |
| `millesime` | integer | ❌ | | Année |
| `degreAlcool` | decimal | ❌ | | Degré d'alcool |
| `codeBarre` | string | ❌ | | Code EAN-13 |
| `seuilStockMinimal` | integer | ❌ | 5 | Seuil alerte stock |
| `reapproAuto` | boolean | ❌ | true | Réappro automatique |
| `prixFournisseur` | decimal | ❌ | | Prix d'achat fournisseur |
| `delaiApproJours` | integer | ❌ | 0 | Délai de livraison fournisseur |

**Response:** `201 Created`

**Erreurs:**
- `409` — SKU ou code-barre déjà existant

---

### Modifier un produit
```http
PUT /api/produits/{id}
```

**Body:** Mêmes champs que le model Produit.

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
    "uniteConditionnement": { "id": 1, "nom": "Bouteille 75cl", "nomCourt": "BT75" }
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
    "fournisseur": { "id": 1, "nom": "Caves de Bordeaux" }
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
    "uniteConditionnement": { "id": 1, "nom": "Bouteille 75cl" }
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
    "creeLe": "2026-01-20T10:00:00",
    "utilisateur": { "id": 3, "prenom": "Pierre", "nom": "Martin" }
  }
]
```

---

## 14. Stock (Backoffice)

Base path: `/api/stock`

> **Accès:** `@IsEmploye`

---

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
    "produit": { "id": 1, "sku": "VIN-RG-001", "nom": "Château Margaux 2018" },
    "uniteConditionnement": { "id": 1, "nom": "Bouteille 75cl" }
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

**Response:** `200 OK` — Produits dont le stock est ≤ `seuilStockMinimal`.

---

### Compteur alertes (badge frontend)
```http
GET /api/stock/alertes/count
```

**Response:** `200 OK`
```json
5
```

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

> **Effets:**
> - Met à jour `stock_actuel` automatiquement
> - Si le stock tombe sous `seuilStockMinimal`, publie une notification `STOCK_FAIBLE` via WebSocket
> - Si `reapproAuto` est activé, publie aussi une notification `REAPPRO_BESOIN`

---

### Historique des mouvements d'un produit
```http
GET /api/stock/mouvements/produit/{produitId}
```

---

### Derniers mouvements (dashboard)
```http
GET /api/stock/mouvements/recents
GET /api/stock/mouvements/recents?limit=50
```

| Paramètre | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | integer | 20 | Nombre max de résultats |

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

### Recherche par code-barre (stock)
```http
GET /api/stock/code-barre/{codeBarre}
```

**Response:** `200 OK` — ConditionnementProduit avec produit.

---

## 15. Inventaires (Backoffice)

Base path: `/api/inventaires`

> **Accès:** `@IsEmploye`

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
    "utilisateur": { "id": 3, "prenom": "Pierre", "nom": "Martin" },
    "lignes": [...]
  }
]
```

---

### Récupérer un inventaire
```http
GET /api/inventaires/{id}
```

**Response:** `200 OK` — Inclut les lignes.

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

**Response:** `201 Created` — Statut `BROUILLON`, sans lignes.

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
> **Effet:** Génère automatiquement les lignes depuis le stock actuel.

**Response:** `200 OK`
```json
{
  "id": 2,
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
> **Effet:** Applique les écarts au stock via des mouvements d'inventaire.
> **Prérequis:** Toutes les lignes doivent être comptées.

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
    "produit": { ... },
    "uniteConditionnement": { ... },
    "comptePar": { ... }
  }
]
```

#### Lignes avec écart
```http
GET /api/inventaires/{id}/lignes/ecarts
```

> Uniquement les lignes où `difference != 0`.

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

**Response:** `200 OK` — Ligne avec `statut: "COMPTEE"`, `difference: -2`.

---

## 16. Commandes fournisseur (Backoffice)

Base path: `/api/commandes-fournisseur`

> **Accès:** `@IsEmploye`

### Workflow des commandes fournisseur

```
BROUILLON → ENVOYEE → CONFIRMEE → PARTIELLEMENT_RECUE → RECUE
     ↓         ↓          ↓
   ANNULEE  ANNULEE   ANNULEE
```

| Statut | Description |
|--------|-------------|
| `BROUILLON` | En préparation, modifiable |
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
    "fournisseur": { "id": 1, "nom": "Caves de Bordeaux" },
    "creePar": { "id": 3, "prenom": "Pierre", "nom": "Martin" },
    "lignes": [...]
  }
]
```

---

### Commandes en attente de réception
```http
GET /api/commandes-fournisseur/en-attente
```

> Retourne les commandes `ENVOYEE`, `CONFIRMEE`, `PARTIELLEMENT_RECUE`.

---

### Récupérer une commande
```http
GET /api/commandes-fournisseur/{id}
```

---

### Créer une commande
```http
POST /api/commandes-fournisseur?fournisseurId=1&notes=Commande urgente
```

| Paramètre | Type | Required | Description |
|-----------|------|----------|-------------|
| `fournisseurId` | integer | ✅ | ID du fournisseur |
| `notes` | string | ❌ | Notes |

**Response:** `201 Created` — Commande `BROUILLON` avec 0 lignes.

---

### Modifier une commande
```http
PUT /api/commandes-fournisseur/{id}?dateLivraisonPrevue=2026-01-25T00:00:00&notes=MAJ notes
```

> ⚠️ Uniquement si statut = `BROUILLON`

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
  "produit": { ... },
  "uniteConditionnement": { ... }
}
```

---

### Modifier une ligne
```http
PUT /api/commandes-fournisseur/{id}/lignes/{ligneId}
```

**Body:**
```json
{
  "quantite": 24,
  "prixUnitaire": 275.00
}
```

**Response:** `200 OK`

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
> **Prérequis:** Au moins une ligne.

---

### Confirmer la commande
```http
POST /api/commandes-fournisseur/{id}/confirmer?dateLivraisonPrevue=2026-01-28T00:00:00
```

> **Action:** `ENVOYEE` → `CONFIRMEE`

| Paramètre | Type | Required |
|-----------|------|----------|
| `dateLivraisonPrevue` | datetime | ❌ |

---

### Réceptionner des produits
```http
POST /api/commandes-fournisseur/{id}/reception
```

> **Action:** `ENVOYEE/CONFIRMEE/PARTIELLEMENT_RECUE` → `PARTIELLEMENT_RECUE/RECUE`
> **Effet:** Crée automatiquement des mouvements d'entrée de stock.

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

**Response:** `200 OK` — Si toutes les lignes sont complètement reçues → `RECUE`, sinon `PARTIELLEMENT_RECUE`.

---

### Annuler la commande
```http
POST /api/commandes-fournisseur/{id}/annuler
```

> ⚠️ Impossible si `RECUE` ou `PARTIELLEMENT_RECUE`.

---

### Générer des commandes automatiques
```http
POST /api/commandes-fournisseur/auto/generer
```

> **Effet:** Crée des commandes groupées par fournisseur pour tous les produits sous seuil avec `reapproAuto: true`.

**Response:** `200 OK`
```json
[
  {
    "id": 10,
    "numero": "CF-202601-0010",
    "statut": "BROUILLON",
    "notes": "Commande automatique - Stock bas pour: Pouilly-Fumé 2021",
    "fournisseur": { "nom": "Domaines de Bourgogne" },
    "lignes": [...]
  }
]
```

---

## 17. Commandes client - Gestion Backoffice

Base path: `/api/commandes-client`

> **Accès gestion:** `@IsEmploye` (les endpoints de lecture par client sont `@IsClient`)

---

### Liste de toutes les commandes
```http
GET /api/commandes-client
GET /api/commandes-client?statut=EN_ATTENTE
```

> **Accès:** `@IsEmploye`

| Paramètre | Type | Description |
|-----------|------|-------------|
| `statut` | enum | Filtrer par statut (`EN_ATTENTE`, `CONFIRMEE`, `EN_PREPARATION`, `EXPEDIEE`, `LIVREE`, `ANNULEE`) |

**Response:** `200 OK` — Toutes les commandes, ordonnées par date décroissante.

---

### Confirmer une commande
```http
POST /api/commandes-client/{id}/confirmer
```

> **Accès:** `@IsEmploye`
> **Action:** `EN_ATTENTE` → `CONFIRMEE`
> **Effet:** Réserve le stock pour chaque ligne de la commande.

**Response:** `200 OK`

---

### Préparer une commande
```http
POST /api/commandes-client/{id}/preparer
```

> **Accès:** `@IsEmploye`
> **Action:** `CONFIRMEE` → `EN_PREPARATION`

**Response:** `200 OK`

---

### Expédier une commande
```http
POST /api/commandes-client/{id}/expedier
```

> **Accès:** `@IsEmploye`
> **Action:** `EN_PREPARATION` → `EXPEDIEE`
> **Effets:**
> - Libère le stock réservé
> - Crée des mouvements de sortie de stock pour chaque ligne
> - Publie une notification `COMMANDE_EXPEDIEE` via WebSocket
> - Enregistre `dateExpedition`

**Response:** `200 OK`

---

### Marquer comme livrée
```http
POST /api/commandes-client/{id}/livrer
```

> **Accès:** `@IsEmploye`
> **Action:** `EXPEDIEE` → `LIVREE`
> **Effet:** Enregistre `dateLivraison`.

**Response:** `200 OK`

---

## 18. Notifications & WebSocket (Backoffice)

### API REST

Base path: `/api/notifications`

> **Accès:** `@IsEmploye`

---

#### Liste des notifications
```http
GET /api/notifications
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "type": "STOCK_FAIBLE",
    "titre": "Stock bas : Château Margaux 2018",
    "message": "Le produit Château Margaux 2018 n'a plus que 3 unités (seuil : 5)",
    "typeReference": "PRODUIT",
    "referenceId": 1,
    "lu": false,
    "creeLe": "2026-01-21T15:30:00"
  },
  {
    "id": 2,
    "type": "COMMANDE_RECUE",
    "titre": "Nouvelle commande CC-202601-0001",
    "message": "Commande de 900.00 € reçue",
    "typeReference": "COMMANDE_CLIENT",
    "referenceId": 1,
    "lu": true,
    "creeLe": "2026-01-21T14:00:00"
  }
]
```

---

#### Notifications non lues
```http
GET /api/notifications/non-lues
```

---

#### Compteur non lues (badge)
```http
GET /api/notifications/count
```

**Response:** `200 OK`
```json
{ "count": 3 }
```

---

#### Marquer comme lue
```http
PUT /api/notifications/{id}/lire
```

**Response:** `200 OK`

---

#### Marquer toutes comme lues
```http
PUT /api/notifications/lire-tout
```

**Response:** `204 No Content`

---

### WebSocket (push temps réel)

#### Configuration
| Paramètre | Valeur |
|-----------|--------|
| Endpoint | `/ws` (SockJS) |
| Protocole | STOMP |
| Auth | Header `Authorization: Bearer {token}` lors du `CONNECT` |
| Topic | `/topic/notifications` |

#### Types de notification

| Type | Déclenché par | Quand |
|------|---------------|-------|
| `STOCK_FAIBLE` | `StockService` | Stock ≤ seuil minimal |
| `REAPPRO_BESOIN` | `StockService` | Stock bas + `reapproAuto: true` |
| `INVENTAIRE_REQUIS` | (prévu) | Inventaire nécessaire |
| `COMMANDE_RECUE` | `CommandeClientService` | Client crée une commande |
| `COMMANDE_EXPEDIEE` | `CommandeClientService` | Commande expédiée |
| `PAIEMENT_ECHOUE` | (prévu) | Paiement en échec |

#### Format du message WebSocket
```json
{
  "id": 5,
  "type": "STOCK_FAIBLE",
  "titre": "Stock bas : Château Margaux 2018",
  "message": "Le produit Château Margaux 2018 n'a plus que 3 unités (seuil : 5)",
  "typeReference": "PRODUIT",
  "referenceId": 1,
  "utilisateur": null,
  "lu": false,
  "creeLe": "2026-01-21T15:30:00"
}
```

---

## 19. Gestion utilisateurs (Admin)

Base path: `/utilisateur`

> **Accès:** `@IsAdmin` pour la gestion, `@IsClient` pour le profil personnel

---

### Liste des clients
```http
GET /utilisateur/liste-client
```

> **Accès:** `@IsAdmin`

**Response:** `200 OK`
```json
[
  {
    "id": 5,
    "email": "client@example.fr",
    "prenom": "Marie",
    "nom": "Dupont",
    "telephone": "0612345678",
    "role": "CLIENT",
    "actif": true,
    "creeLe": "2026-01-15T10:00:00",
    "adresses": [...]
  }
]
```

---

### Liste des employés
```http
GET /utilisateur/liste-employe
```

> **Accès:** `@IsAdmin`

---

### Créer un employé
```http
POST /utilisateur/employe
```

> **Accès:** `@IsAdmin`

**Body:**
```json
{
  "email": "nouveau.employe@caveo.fr",
  "password": "motdepasse123",
  "prenom": "Paul",
  "nom": "Bernard",
  "telephone": "0678901234"
}
```

**Response:** `201 Created` — Utilisateur avec rôle `EMPLOYE`.

---

### Modifier rôle / activer-désactiver un utilisateur
```http
PUT /utilisateur/{id}
```

> **Accès:** `@IsAdmin` pour modifier `role` et `actif`

**Body (admin):**
```json
{
  "role": "ADMIN",
  "actif": false
}
```

> Un ADMIN peut modifier le rôle (`EMPLOYE` ↔ `ADMIN`) et activer/désactiver un compte.

---

### Supprimer un utilisateur
```http
DELETE /utilisateur/{id}
```

> **Accès:** Propriétaire du compte ou `@IsAdmin`

**Response:** `204 No Content`

---

## 20. Codes d'erreur

Toutes les erreurs suivent le même format :

```json
{
  "message": "Description de l'erreur",
  "status": 400
}
```

| Code | Description | Exemple |
|------|-------------|---------|
| `400` | Bad Request | Données invalides, transition d'état impossible |
| `401` | Unauthorized | Token JWT manquant, invalide ou expiré |
| `403` | Forbidden | Rôle insuffisant ou accès à une ressource non autorisée |
| `404` | Not Found | Entité introuvable |
| `409` | Conflict | Doublon (SKU, email, nom unique, etc.) |

---

## 21. Enums de référence

### Rôles
| Valeur | Description |
|--------|-------------|
| `CLIENT` | Client e-commerce |
| `EMPLOYE` | Employé backoffice |
| `ADMIN` | Administrateur |

### TypeNotification
| Valeur | Description |
|--------|-------------|
| `STOCK_FAIBLE` | Stock sous le seuil minimal |
| `REAPPRO_BESOIN` | Réapprovisionnement nécessaire |
| `INVENTAIRE_REQUIS` | Inventaire à planifier |
| `COMMANDE_RECUE` | Nouvelle commande client reçue |
| `COMMANDE_EXPEDIEE` | Commande expédiée |
| `PAIEMENT_ECHOUE` | Paiement en échec |

### StatutCommandeClient
| Valeur | Transitions possibles |
|--------|-----------------------|
| `EN_ATTENTE` | CONFIRMEE, ANNULEE |
| `CONFIRMEE` | EN_PREPARATION, ANNULEE |
| `EN_PREPARATION` | EXPEDIEE, ANNULEE |
| `EXPEDIEE` | LIVREE |
| `LIVREE` | (terminal) |
| `ANNULEE` | (terminal) |

### StatutCommandeFournisseur
| Valeur | Description |
|--------|-------------|
| `BROUILLON` | En préparation |
| `ENVOYEE` | Envoyée au fournisseur |
| `CONFIRMEE` | Confirmée par le fournisseur |
| `PARTIELLEMENT_RECUE` | Réception partielle |
| `RECUE` | Réception complète |
| `ANNULEE` | Annulée |

### StatutInventaire
| Valeur | Description |
|--------|-------------|
| `BROUILLON` | Créé, modifiable |
| `EN_COURS` | Comptage en cours |
| `TERMINE` | Terminé, écarts appliqués |
| `ANNULE` | Annulé |

### StatutPaiement
| Valeur | Description |
|--------|-------------|
| `EN_ATTENTE` | En attente de traitement |
| `COMPLET` | Paiement réussi |
| `ECHEC` | Paiement échoué |

### MethodePaiement
| Valeur |
|--------|
| `CARTE` |
| `PAYPAL` |
| `VIREMENT` |

### TypeAdresse
| Valeur |
|--------|
| `FACTURATION` |
| `LIVRAISON` |

### TypeMouvement
| Valeur |
|--------|
| `ENTREE` |
| `SORTIE` |
| `AJUSTEMENT` |
| `INVENTAIRE` |

### TypeReference
| Valeur |
|--------|
| `COMMANDE_FOURNISSEUR` |
| `COMMANDE_CLIENT` |
| `INVENTAIRE` |
| `MANUEL` |

---

## 22. Guide Frontend - Alertes Stock

### Workflow complet

```
1. Polling badge       GET /api/stock/alertes/count (toutes les 30-60s)
         |
         v
2. Si count > 0  →    Afficher badge rouge avec le nombre
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

### Tableau des alertes

| Colonne | Source | Description |
|---------|--------|-------------|
| Produit | `stock.produit.nom` | Nom du produit |
| SKU | `stock.produit.sku` | Référence unique |
| Stock actuel | `stock.quantite` | Quantité en stock |
| Seuil | `stock.produit.seuilStockMinimal` | Seuil d'alerte |
| Manque | `seuil - stock` | Quantité à commander |
| Réappro auto | `stock.produit.reapproAuto` | Éligible commande auto |

> Les produits avec `reapproAuto: false` apparaissent dans les alertes mais ne sont **pas** inclus dans la génération automatique.

---

## 23. Guide Frontend - Scanner Inventaire

### Workflow

```
1. Créer inventaire    POST /api/inventaires
         |
2. Démarrer            POST /api/inventaires/{id}/demarrer
         |
3. Scanner produit     GET /api/produits/code-barre/{codeBarre}
         |
4. Afficher produit    Nom, stock théorique, conditionnement
         |
5. Saisir quantité     PUT /api/inventaires/{id}/lignes/{ligneId}
         |
6. Répéter 3-5         Pour chaque produit
         |
7. Terminer            POST /api/inventaires/{id}/terminer
```

### Interface recommandée

1. **Champ de saisie** autofocus pour scanner
2. **Affichage produit** trouvé avec photo si disponible
3. **Input numérique** pour la quantité comptée
4. **Écart calculé** automatiquement (compté - théorique)
5. **Bouton validation** puis retour au scan

---

## 24. Guide Frontend - Notifications WebSocket

### Connexion

```javascript
// Avec SockJS + STOMP.js
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { Authorization: 'Bearer ' + token },
  () => {
    // Connecté — charger les notifications existantes
    fetch('/api/notifications/count', { headers: { Authorization: 'Bearer ' + token } })
      .then(res => res.json())
      .then(data => setBadgeCount(data.count));

    // Écouter les nouvelles notifications en temps réel
    stompClient.subscribe('/topic/notifications', (message) => {
      const notification = JSON.parse(message.body);
      // Ajouter à la liste + incrémenter le badge
      addNotification(notification);
      setBadgeCount(prev => prev + 1);
    });
  }
);
```

### Gestion des notifications

1. **Badge** : Afficher le compteur via `GET /api/notifications/count`
2. **Panel** : Liste via `GET /api/notifications` (ou `/non-lues`)
3. **Marquer lue** : `PUT /api/notifications/{id}/lire` au clic
4. **Tout marquer** : `PUT /api/notifications/lire-tout`
5. **Temps réel** : Subscription WebSocket `/topic/notifications`

---

## Notes importantes

1. **Authentification:** Token JWT dans le header `Authorization: Bearer {token}`. Expire après 24h.

2. **Rôles hiérarchiques:**
   - `@IsClient` = CLIENT + EMPLOYE + ADMIN
   - `@IsEmploye` = EMPLOYE + ADMIN
   - `@IsAdmin` = ADMIN uniquement

3. **Soft Delete:** Les entités principales (Catégorie, Domaine, Produit, Unité de conditionnement) sont désactivées (`actif: false`) et non supprimées physiquement.

4. **Stock automatique:**
   - Les mouvements mettent à jour automatiquement `stock_actuel`
   - Les réceptions de commandes fournisseur créent des mouvements d'entrée
   - Les expéditions de commandes client créent des mouvements de sortie
   - Les inventaires terminés créent des mouvements de type `INVENTAIRE`

5. **Notifications temps réel:** Le système utilise l'Observer Pattern (Spring Events) + WebSocket STOMP pour pousser les notifications au backoffice en temps réel.

6. **Paiement mock:** Le paiement est une simulation — il passe directement en statut `COMPLET` et confirme automatiquement la commande.

7. **Commandes automatiques:** `POST /api/commandes-fournisseur/auto/generer` crée des commandes **groupées par fournisseur** pour les produits avec `reapproAuto: true` et stock sous le seuil.