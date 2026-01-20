# Documentation API Backend 

Base URL: `http://localhost:8080`

##  Authentification

### Login
```http
POST /login
```

**Body:**
```json
{
  "username": "email@example.com",
  "password": "motdepasse"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "utilisateur": {
    "id": 1,
    "email": "email@example.com",
    "prenom": "John",
    "nom": "Doe",
    "role": "CLIENT",
    "actif": true
  }
}
```

**Note:** Utiliser le token dans l'header `Authorization: Bearer {token}` pour toutes les requêtes suivantes.

---

##  Utilisateurs

### Récupérer son profil
```http
GET /utilisateur/{id}
```
 **Auth requise:** Utilisateur connecté (uniquement son propre profil)

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "user@example.com",
  "prenom": "John",
  "nom": "Doe",
  "telephone": "0612345678",
  "role": "CLIENT",
  "actif": true,
  "creeLe": "2026-01-15T10:30:00",
  "modifieLe": null,
  "adresses": [...]
}
```

---

### Liste des clients
```http
GET /utilisateur/liste-client
```
 **Auth requise:** ADMIN uniquement

**Response:** `200 OK`
```json
[
  {
    "id": 2,
    "email": "client@example.com",
    "prenom": "Marie",
    "nom": "Dupont",
    "role": "CLIENT",
    "actif": true,
    "adresses": [...]
  }
]
```

---

### Liste des employés
```http
GET /utilisateur/liste-employe
```
 **Auth requise:** ADMIN uniquement

**Response:** `200 OK`
```json
[
  {
    "id": 3,
    "email": "employe@example.com",
    "prenom": "Pierre",
    "nom": "Martin",
    "role": "EMPLOYE",
    "actif": true
  }
]
```

---

### Créer un employé
```http
POST /utilisateur/employe
```
 **Auth requise:** ADMIN uniquement

**Body:**
```json
{
  "email": "nouvel.employe@example.com",
  "password": "motdepasse123",
  "prenom": "Sophie",
  "nom": "Bernard",
  "telephone": "0623456789"
}
```

**Response:** `201 Created`
```json
{
  "id": 5,
  "email": "nouvel.employe@example.com",
  "prenom": "Sophie",
  "nom": "Bernard",
  "role": "EMPLOYE",
  "actif": true
}
```

**Note:** Le rôle est automatiquement défini à `EMPLOYE`.

---

### Modifier un utilisateur
```http
PUT /utilisateur/{id}
```
 **Auth requise:** 
- Utilisateur connecté peut modifier ses propres informations (email, nom, prenom, telephone)
- ADMIN peut modifier le rôle et actif/désactiver le compte

**Body (utilisateur normal):**
```json
{
  "email": "nouveau.email@example.com",
  "nom": "NouveauNom",
  "prenom": "NouveauPrenom",
  "telephone": "0698765432"
}
```

**Body (ADMIN):**
```json
{
  "role": "ADMIN",
  "actif": false
}
```

**Response:** `200 OK`

---

### Supprimer un compte
```http
DELETE /utilisateur/{id}
```
 **Auth requise:** 
- Utilisateur connecté peut supprimer son propre compte
- ADMIN peut supprimer n'importe quel compte

**Response:** `204 No Content`

---

## 📍 Adresses

### Liste de ses adresses
```http
GET /adresse/liste
```
 **Auth requise:** Utilisateur connecté

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "type": "LIVRAISON",
    "rue": "123 Rue de la Paix",
    "ville": "Paris",
    "codePostal": "75001",
    "pays": "France",
    "parDefaut": true
  },
  {
    "id": 2,
    "type": "FACTURATION",
    "rue": "456 Avenue des Champs",
    "ville": "Lyon",
    "codePostal": "69001",
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
 **Auth requise:** Utilisateur connecté (uniquement ses propres adresses)

**Response:** `200 OK`
```json
{
  "id": 1,
  "type": "LIVRAISON",
  "rue": "123 Rue de la Paix",
  "ville": "Paris",
  "codePostal": "75001",
  "pays": "France",
  "parDefaut": true
}
```

---

### Créer une adresse
```http
POST /adresse
```
 **Auth requise:** Utilisateur connecté

**Body:**
```json
{
  "type": "LIVRAISON",
  "rue": "789 Boulevard Victor Hugo",
  "ville": "Marseille",
  "codePostal": "13001",
  "pays": "France",
  "parDefaut": false
}
```

**Types d'adresse possibles:** `LIVRAISON`, `FACTURATION`, `ENTREPRISE`

**Response:** `201 Created`
```json
{
  "id": 3,
  "type": "LIVRAISON",
  "rue": "789 Boulevard Victor Hugo",
  "ville": "Marseille",
  "codePostal": "13001",
  "pays": "France",
  "parDefaut": false
}
```

**Note:** Si `parDefaut: true`, toutes les autres adresses seront automatiquement mises à `false`.

---

### Modifier une adresse
```http
PUT /adresse/{id}
```
 **Auth requise:** Utilisateur connecté (uniquement ses propres adresses)

**Body:**
```json
{
  "type": "FACTURATION",
  "rue": "789 Boulevard Victor Hugo",
  "ville": "Marseille",
  "codePostal": "13002",
  "pays": "France",
  "parDefaut": true
}
```

**Response:** `200 OK`

---

### Supprimer une adresse
```http
DELETE /adresse/{id}
```
 **Auth requise:** Utilisateur connecté (uniquement ses propres adresses)

**Response:** `204 No Content`

---

##  Codes d'erreur

### 400 Bad Request
Erreur de validation des données.

**Exemple:**
```json
{
  "message": "Erreur de validation",
  "errors": {
    "email": "L'email ne peut pas être vide",
    "prenom": "Le prenom ne peut pas être vide"
  },
  "status": 400
}
```

---

### 401 Unauthorized
Token manquant ou invalide.

**Exemple:**
```json
{
  "message": "Token JWT invalide ou expiré",
  "status": 401
}
```

---

### 403 Forbidden
Accès refusé (permissions insuffisantes).

**Exemple:**
```json
{
  "message": "Vous ne pouvez consulter que votre propre profil",
  "status": 403
}
```

---

### 404 Not Found
Ressource non trouvée.

**Exemple:**
```json
{
  "message": "Utilisateur avec l'id 999 n'existe pas",
  "status": 404
}
```

---

### 409 Conflict
Conflit de données (ex: email déjà utilisé).

**Exemple:**
```json
{
  "message": "L'email est déjà utilisé par un autre utilisateur",
  "status": 409
}
```

---

## 📝 Notes importantes

1. **Authentification:** Toutes les routes (sauf `/login`) nécessitent un token JWT dans l'header `Authorization: Bearer {token}`.

2. **Mot de passe:** Le champ `password` n'est jamais retourné dans les réponses JSON.

3. **Rôles disponibles:**
   - `CLIENT` - Client de l'e-commerce
   - `EMPLOYE` - Employé de l'entreprise
   - `ADMIN` - Administrateur (tous les droits)

4. **Adresse par défaut:** Un utilisateur ne peut avoir qu'une seule adresse par défaut à la fois.

5. **Suppression de compte:** La suppression d'un utilisateur supprime également toutes ses adresses (cascade).

---
```
