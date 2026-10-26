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
- Rechercher les articles déposés sur une période, avec les quantités cumulées par référence (option 9).
- Filtrer les articles par type : livre, manuel ou magazine (option 10).
- Rechercher un mot-clé dans les descriptions, sans distinction de majuscules/minuscules (option 11).
- Filtrer les articles par prix actuel, réductions comprises (option 12).
- Consulter les dépôts d’un client ou d’un article sur une période.
- Sauvegarder et charger les articles et les dépôts dans des fichiers texte.

Les bornes des périodes et des prix sont incluses. Le filtre « Livre » exclut les manuels,
qui possèdent leur propre catégorie. Les recherches ne modifient pas les stocks.

## Vérification des recherches

Depuis la racine du projet, avec un JDK installé :

```powershell
javac -encoding UTF-8 -d build/test/classes src/tp2_exo1/*.java test/tp2_exo1/EtablissementRechercheTest.java
java -ea "-Dfile.encoding=UTF-8" -cp build/test/classes tp2_exo1.EtablissementRechercheTest
```

Les tests utilisent leurs propres données et ne modifient pas les fichiers du dossier `data`.

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

