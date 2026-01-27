# API Complete - Caveo Backend

Documentation complète de toutes les API REST du backend Java.

**Base URL:** `http://localhost:8080`

---

## 📋 Table des matières

1. [Authentification](#-authentification)
2. [Produits](#-produits)
3. [Catégories](#-catégories)
4. [Domaines](#-domaines)
5. [Fournisseurs](#-fournisseurs)
6. [Stock](#-stock)
7. [Commandes Fournisseur](#-commandes-fournisseur)
8. [Inventaire](#-inventaire)
9. [Point de Vente (POS)](#-point-de-vente-pos)
10. [Unités de Conditionnement](#-unités-de-conditionnement)
11. [Utilisateurs](#-utilisateurs)
12. [Adresses](#-adresses)
13. [API Publiques](#-api-publiques)

---

## 🔐 Authentification

**Base path:** `/api/auth`

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| `POST` | `/api/auth/login` | Connexion utilisateur | ❌ |
| `POST` | `/api/auth/register` | Inscription client | ❌ |
| `GET` | `/api/auth/me` | Récupérer l'utilisateur connecté | ✅ Bearer |

### Détails

#### POST /api/auth/login
```json
// Request
{
  "email": "string",
  "password": "string"
}

// Response 200
{
  "token": "string",
  "id": "number",
  "email": "string",
  "prenom": "string",
  "nom": "string",
  "role": "CLIENT | EMPLOYE | ADMIN"
}
```

#### POST /api/auth/register
```json
// Request
{
  "email": "string",
  "password": "string",
  "prenom": "string",
  "nom": "string",
  "telephone": "string (optional)"
}
```

---

## 🍷 Produits

**Base path:** `/api/produits`  
**Auth:** `@IsEmploye` (EMPLOYE ou ADMIN)

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/produits` | Liste des produits | `categorieId`, `domaineId`, `millesime`, `search` |
| `GET` | `/api/produits/{id}` | Détail d'un produit (avec relations) | - |
| `GET` | `/api/produits/sku/{sku}` | Produit par SKU | - |
| `GET` | `/api/produits/code-barre/{codeBarre}` | Produit par code-barre | - |
| `POST` | `/api/produits` | Créer un produit | - |
| `PUT` | `/api/produits/{id}` | Modifier un produit | - |
| `DELETE` | `/api/produits/{id}` | Supprimer (soft delete) | - |

### Conditionnements du produit

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/produits/{id}/conditionnements` | Liste des conditionnements |
| `POST` | `/api/produits/{id}/conditionnements` | Ajouter un conditionnement |
| `PUT` | `/api/produits/{id}/conditionnements/{condId}` | Modifier un conditionnement |
| `DELETE` | `/api/produits/{id}/conditionnements/{condId}` | Supprimer un conditionnement |

### Fournisseurs du produit

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/produits/{id}/fournisseurs` | Liste des fournisseurs |
| `POST` | `/api/produits/{id}/fournisseurs` | Associer un fournisseur |
| `PUT` | `/api/produits/{id}/fournisseurs/{fournId}` | Modifier l'association |
| `DELETE` | `/api/produits/{id}/fournisseurs/{fournId}` | Supprimer l'association |

### Stock du produit

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/produits/{id}/stock` | Stock actuel du produit |
| `GET` | `/api/produits/{id}/mouvements` | Historique des mouvements |

### Création produit (DTO)
```json
// POST /api/produits
{
  "sku": "string (required)",
  "nom": "string (required)",
  "description": "string",
  "categorieId": "number (required)",
  "domaineId": "number",
  "fournisseurId": "number (required)",
  "prixFournisseur": "number (required)",
  "delaiApproJours": "number",
  "millesime": "number",
  "degreAlcool": "number",
  "codeBarre": "string",
  "imageUrl": "string",
  "notesDegustation": "string",
  "temperatureService": "string",
  "conditionsConservation": "string",
  "seuilStockMinimal": "number (default: 5)",
  "reapproAuto": "boolean (default: true)"
}
```

---

## 📂 Catégories

**Base path:** `/api/categories`  
**Auth:** `@IsEmploye`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/categories` | Liste des catégories actives |
| `GET` | `/api/categories/{id}` | Détail d'une catégorie |
| `POST` | `/api/categories` | Créer une catégorie |
| `PUT` | `/api/categories/{id}` | Modifier une catégorie |
| `DELETE` | `/api/categories/{id}` | Supprimer (soft delete) |

---

## 🏔️ Domaines

**Base path:** `/api/domaines`  
**Auth:** `@IsEmploye`

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/domaines` | Liste des domaines | `region`, `appellation` |
| `GET` | `/api/domaines/{id}` | Détail d'un domaine | - |
| `POST` | `/api/domaines` | Créer un domaine | - |
| `PUT` | `/api/domaines/{id}` | Modifier un domaine | - |
| `DELETE` | `/api/domaines/{id}` | Supprimer (soft delete) | - |

---

## 🚚 Fournisseurs

**Base path:** `/api/fournisseurs`  
**Auth:** `@IsEmploye`

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/fournisseurs` | Liste des fournisseurs | `bio`, `aoc` |
| `GET` | `/api/fournisseurs/{id}` | Détail d'un fournisseur | - |
| `POST` | `/api/fournisseurs` | Créer un fournisseur | - |
| `PUT` | `/api/fournisseurs/{id}` | Modifier un fournisseur | - |
| `DELETE` | `/api/fournisseurs/{id}` | Supprimer (hard delete) | - |
| `GET` | `/api/fournisseurs/{id}/produits` | Produits du fournisseur | - |

---

## 📦 Stock

**Base path:** `/api/stock`  
**Auth:** `@IsEmploye`

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/stock` | Stock complet | - |
| `GET` | `/api/stock/alertes` | Produits sous seuil | `reapproAutoOnly` |
| `GET` | `/api/stock/alertes/count` | Nombre d'alertes | - |
| `POST` | `/api/stock/mouvements` | Enregistrer un mouvement | - |
| `GET` | `/api/stock/mouvements/produit/{produitId}` | Historique d'un produit | - |
| `GET` | `/api/stock/mouvements/recents` | Derniers mouvements | `limit` (default: 20) |
| `POST` | `/api/stock/reserver` | Réserver du stock | `produitId`, `uniteConditionnementId`, `quantite` |
| `POST` | `/api/stock/liberer` | Libérer du stock réservé | `produitId`, `uniteConditionnementId`, `quantite` |
| `GET` | `/api/stock/code-barre/{codeBarre}` | Recherche par code-barre | - |

### Mouvement stock (Request Body)
```json
{
  "produit": { "id": "number" },
  "uniteConditionnement": { "id": "number" },
  "typeMouvement": "ENTREE | SORTIE | AJUSTEMENT",
  "quantite": "number (>0)",
  "typeReference": "COMMANDE_FOURNISSEUR | COMMANDE_CLIENT | INVENTAIRE | MANUEL",
  "referenceId": "number (optional)",
  "notes": "string"
}
```

---

## 📋 Commandes Fournisseur

**Base path:** `/api/commandes-fournisseur`  
**Auth:** `@IsEmploye`

### CRUD

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/commandes-fournisseur` | Liste des commandes | `statut` |
| `GET` | `/api/commandes-fournisseur/en-attente` | Commandes en attente | - |
| `GET` | `/api/commandes-fournisseur/{id}` | Détail avec lignes | - |
| `POST` | `/api/commandes-fournisseur` | Créer une commande | `fournisseurId`, `notes` |
| `PUT` | `/api/commandes-fournisseur/{id}` | Modifier (BROUILLON only) | `dateLivraisonPrevue`, `notes` |
| `DELETE` | `/api/commandes-fournisseur/{id}` | Supprimer (BROUILLON only) | - |

### Lignes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/commandes-fournisseur/{id}/lignes` | Ajouter une ligne |
| `DELETE` | `/api/commandes-fournisseur/{id}/lignes/{ligneId}` | Supprimer une ligne |

### Workflow

| Méthode | Endpoint | Description | Transition |
|---------|----------|-------------|------------|
| `POST` | `/api/commandes-fournisseur/{id}/envoyer` | Envoyer au fournisseur | BROUILLON → ENVOYEE |
| `POST` | `/api/commandes-fournisseur/{id}/confirmer` | Confirmer la commande | ENVOYEE → CONFIRMEE |
| `POST` | `/api/commandes-fournisseur/{id}/reception` | Réceptionner les produits | * → PARTIELLEMENT_RECUE/RECUE |
| `POST` | `/api/commandes-fournisseur/{id}/annuler` | Annuler | * → ANNULEE |

### Commandes automatiques

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/commandes-fournisseur/auto/generer` | Générer commandes auto (produits sous seuil) |

### Statuts
- `BROUILLON` - En cours de création
- `ENVOYEE` - Envoyée au fournisseur
- `CONFIRMEE` - Confirmée par le fournisseur
- `PARTIELLEMENT_RECUE` - Réception partielle
- `RECUE` - Entièrement reçue
- `ANNULEE` - Annulée

### Ligne commande (Request Body)
```json
{
  "produitId": "number",
  "uniteConditionnementId": "number",
  "quantite": "number",
  "prixUnitaire": "number"
}
```

### Réception (Request Body)
```json
{
  "lignes": [
    {
      "ligneId": "number",
      "quantiteRecue": "number",
      "numeroLot": "string (optional)",
      "datePeremption": "string ISO date (optional)"
    }
  ]
}
```

---

## 📊 Inventaire

**Base path:** `/api/inventaires`  
**Auth:** `@IsEmploye`

### CRUD

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/inventaires` | Liste des inventaires | `statut` |
| `GET` | `/api/inventaires/{id}` | Détail avec lignes | - |
| `POST` | `/api/inventaires` | Créer un inventaire | - |
| `PUT` | `/api/inventaires/{id}` | Modifier | - |
| `DELETE` | `/api/inventaires/{id}` | Supprimer | - |

### Workflow

| Méthode | Endpoint | Description | Transition |
|---------|----------|-------------|------------|
| `POST` | `/api/inventaires/{id}/demarrer` | Démarrer (génère lignes) | BROUILLON → EN_COURS |
| `POST` | `/api/inventaires/{id}/terminer` | Terminer (applique écarts) | EN_COURS → TERMINE |
| `POST` | `/api/inventaires/{id}/annuler` | Annuler | * → ANNULE |

### Lignes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/inventaires/{id}/lignes` | Toutes les lignes |
| `GET` | `/api/inventaires/{id}/lignes/ecarts` | Lignes avec écart |
| `PUT` | `/api/inventaires/{id}/lignes/{ligneId}` | Mettre à jour le comptage |

### Statuts
- `BROUILLON` - En préparation
- `EN_COURS` - Comptage en cours
- `TERMINE` - Terminé et appliqué
- `ANNULE` - Annulé

---

## 💳 Point de Vente (POS)

**Base path:** `/api/pos`  
**Auth:** `@IsEmploye`

### Ventes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/pos/ventes` | Créer une vente |
| `GET` | `/api/pos/ventes/{id}` | Détail d'une vente |
| `GET` | `/api/pos/ventes/jour` | Ventes du jour |
| `GET` | `/api/pos/ventes/brouillon` | Ventes brouillon de l'utilisateur |
| `DELETE` | `/api/pos/ventes/{id}` | Annuler une vente |

### Lignes

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `POST` | `/api/pos/ventes/{id}/lignes` | Ajouter une ligne | - |
| `PUT` | `/api/pos/ventes/{venteId}/lignes/{ligneId}` | Modifier quantité | `quantite` |
| `DELETE` | `/api/pos/ventes/{venteId}/lignes/{ligneId}` | Supprimer une ligne | - |

### Remises

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/pos/ventes/{id}/remise` | Appliquer une remise |
| `DELETE` | `/api/pos/ventes/{id}/remise` | Supprimer la remise |

### Paiements & Finalisation

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/pos/ventes/{id}/paiements` | Enregistrer un paiement |
| `POST` | `/api/pos/ventes/{id}/finaliser` | Finaliser la vente |

### Ticket & Stats

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/pos/ventes/{id}/ticket` | Générer le ticket |
| `GET` | `/api/pos/stats/jour` | Statistiques du jour |

### Recherche

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/pos/recherche/code-barre/{codeBarre}` | Rechercher par code-barre |

### Ligne vente (Request Body)
```json
{
  "conditionnementProduitId": "number",
  "quantite": "number"
}
```

### Paiement (Request Body)
```json
{
  "methodePaiement": "ESPECES | CARTE | CHEQUE",
  "montant": "number"
}
```

### Remise (Request Body)
```json
{
  "typeRemise": "POURCENTAGE | MONTANT",
  "valeurRemise": "number"
}
```

---

## 📐 Unités de Conditionnement

**Base path:** `/api/unites-conditionnement`  
**Auth:** `@IsEmploye`

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/unites-conditionnement` | Liste des unités | `vendableOnly` |
| `GET` | `/api/unites-conditionnement/{id}` | Détail d'une unité | - |
| `GET` | `/api/unites-conditionnement/unite-base` | Unité de base | - |
| `POST` | `/api/unites-conditionnement` | Créer une unité | - |
| `PUT` | `/api/unites-conditionnement/{id}` | Modifier une unité | - |
| `DELETE` | `/api/unites-conditionnement/{id}` | Supprimer (soft delete) | - |

---

## 👤 Utilisateurs

**Base path:** `/utilisateur`  
**Auth:** `@IsClient` (minimum)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| `GET` | `/utilisateur/liste-client` | Liste des clients | `@IsAdmin` |
| `GET` | `/utilisateur/liste-employe` | Liste des employés | `@IsAdmin` |
| `GET` | `/utilisateur/{id}` | Profil utilisateur (son propre) | `@IsClient` |
| `POST` | `/utilisateur/employe` | Créer un employé | `@IsAdmin` |
| `PUT` | `/utilisateur/{id}` | Modifier profil | `@IsClient` |
| `DELETE` | `/utilisateur/{id}` | Supprimer compte | `@IsClient` |

### Notes
- Un utilisateur ne peut consulter/modifier que son propre profil
- Un ADMIN peut modifier le rôle et activer/désactiver les comptes
- Un ADMIN peut supprimer n'importe quel compte

---

## 🏠 Adresses

**Base path:** `/adresse`  
**Auth:** `@IsClient`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/adresse/liste` | Adresses de l'utilisateur connecté |
| `GET` | `/adresse/{id}` | Détail d'une adresse |
| `POST` | `/adresse` | Créer une adresse |
| `PUT` | `/adresse/{id}` | Modifier une adresse |
| `DELETE` | `/adresse/{id}` | Supprimer une adresse |

### Notes
- Un utilisateur ne peut gérer que ses propres adresses
- Une seule adresse peut être "par défaut"

---

## 🌐 API Publiques

**Base path:** `/api/public`  
**Auth:** ❌ Aucune

| Méthode | Endpoint | Description | Query Params |
|---------|----------|-------------|--------------|
| `GET` | `/api/public/produits` | Liste des produits | `categorieId`, `domaineId`, `millesime`, `search` |
| `GET` | `/api/public/produits/{id}` | Détail d'un produit | - |
| `GET` | `/api/public/categories` | Liste des catégories | - |
| `GET` | `/api/public/categories/{id}` | Détail d'une catégorie | - |
| `GET` | `/api/public/domaines` | Liste des domaines | - |
| `GET` | `/api/public/domaines/{id}` | Détail d'un domaine | - |

---

## 🔑 Rôles et Permissions

| Rôle | Description | Accès |
|------|-------------|-------|
| `CLIENT` | Client final | Profil, adresses, API publiques |
| `EMPLOYE` | Employé cave | Tout sauf gestion utilisateurs |
| `ADMIN` | Administrateur | Accès complet |

### Annotations de sécurité
- `@IsClient` - CLIENT, EMPLOYE, ADMIN
- `@IsEmploye` - EMPLOYE, ADMIN
- `@IsAdmin` - ADMIN uniquement

---

## 📊 Résumé par état Frontend

### ✅ Implémenté dans backoffice-react
- [x] Authentification (login, register, me)
- [x] Produits (CRUD complet + conditionnements + fournisseurs)
- [x] Catégories (lecture seule)
- [x] Domaines (lecture seule)
- [x] Fournisseurs (CRUD complet)
- [x] Stock (alertes, mouvements)
- [x] Commandes Fournisseur (CRUD + workflow)
- [x] POS (ventes, lignes, paiements)

### ⏳ À implémenter
- [ ] Catégories (CRUD complet)
- [ ] Domaines (CRUD complet)
- [ ] Inventaire (CRUD + workflow)
- [ ] Unités de conditionnement (CRUD)
- [ ] Utilisateurs (gestion admin)
- [ ] Statistiques / Dashboard avancé

### ❓ Non applicable au backoffice
- API Publiques (pour front-office client)
- Adresses (pour front-office client)
