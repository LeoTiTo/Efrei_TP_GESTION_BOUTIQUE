# Gestion d’une boutique

**Participants :** Lucas Singhavara, Loc Tran, Alexandre Piana, Noé Ballu  
**Formation :** Ing1-Apprenti  
**Groupe :** BDML 1  
**Enseignante :** Madame Georgina ABI SEJAAN  
**Environnement :** Java et NetBeans

## Fonctionnalités

- Gérer les livres, les manuels scolaires et les magazines.
- Afficher les articles par stock croissant.
- Enregistrer les dépôts et retirer les exemplaires vendus.
- Rechercher un article par son ISBN ou son ISSN.
- Consulter les dépôts d’un client ou d’un article sur une période.
- Sauvegarder et charger les articles et les dépôts dans des fichiers texte.

## Organisation des classes

| Classe | Responsabilité |
|---|---|
| `Article` | Classe abstraite regroupant les informations communes : description, prix initial et stock. |
| `Livre` | Ajoute l’ISBN et le nombre de pages. |
| `Manuel` | Spécialise un livre avec une matière et un niveau scolaire. |
| `Magazine` | Ajoute l’ISSN, la périodicité et la date de publication. |
| `Etablissement` | Gère les articles, les stocks, les dépôts et les fichiers. |
| `BonDepot` | Regroupe les lignes d’un dépôt, sa date et le téléphone du déposant. |
| `LigneDepot` | Conserve la référence d’un article et la quantité déposée. |
| `TP2_Exo1` | Gère le menu et les saisies de l’utilisateur. |


## Diagramme de classes

![Diagramme UML de la boutique](Boutique_UML.png)

