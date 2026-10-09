/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tp2_exo1;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author lochu
 */
public class TP2_Exo1 {
    private static final Scanner CLAVIER = new Scanner(System.in);
    private static final String ARTICLES = "data/articles.txt";
    private static final String DEPOTS = "data/depots.txt";

    public static void main(String[] args) {
        Etablissement boutique = new Etablissement("Boutique culturelle d'occasion");
        try {
            File dossier = new File("data");
            if (!dossier.isDirectory() && !dossier.mkdirs()) throw new IOException("Création du dossier data impossible.");
            if (new File(ARTICLES).exists()) boutique.depuisFichierArticles(ARTICLES);
            if (new File(DEPOTS).exists()) boutique.depuisFichierDepots(DEPOTS);
        } catch (IOException e) {
            System.out.println("Chargement impossible : " + e.getMessage());
            System.out.println("Arrêt du programme pour préserver les fichiers existants.");
            return;
        }

        boolean terminer = false;
        while (!terminer) {
            afficherMenu();
            try {
                int choix = entier("Votre choix : ", 0, 8);
                switch (choix) {
                    case 1:
                        System.out.println(boutique.lister());
                        break;
                    case 2:
                        saisirDepot(boutique);
                        break;
                    case 3:
                        boutique.retirer(texte("ISBN ou ISSN : "), entier("Quantité vendue : ", 1, Integer.MAX_VALUE));
                        System.out.println("Stock mis à jour.");
                        break;
                    case 4:
                        Article article = boutique.rechercher(texte("ISBN ou ISSN : "));
                        if (article == null) System.out.println("Article introuvable.");
                        else {
                            System.out.println(article);
                            System.out.printf("Prix actuel : %.2f EUR%n", article.calculerPrix());
                        }
                        break;
                    case 5:
                        System.out.println(boutique.lister(texte("Téléphone du déposant : ")));
                        break;
                    case 6:
                        String numero = texte("ISBN ou ISSN : ");
                        LocalDate debut = date("Date de début (AAAA-MM-JJ) : ");
                        LocalDate fin = date("Date de fin (AAAA-MM-JJ) : ");
                        System.out.println(boutique.lister(numero, debut, fin));
                        break;
                    case 7:
                        System.out.println(boutique);
                        break;
                    case 8:
                        sauvegarder(boutique);
                        break;
                    case 0:
                        sauvegarder(boutique);
                        terminer = true;
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Opération refusée : " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Sauvegarde impossible : " + e.getMessage());
                System.out.println("Le programme reste ouvert. Corrigez le problème puis réessayez.");
            } catch (NoSuchElementException e) {
                try { sauvegarder(boutique); }
                catch (IOException erreur) { System.out.println("Sauvegarde impossible : " + erreur.getMessage()); }
                terminer = true;
            }
        }
        CLAVIER.close();
    }

    private static void afficherMenu() {
        System.out.println("\n--- Boutique culturelle ---");
        System.out.println("1. Lister les articles par stock croissant");
        System.out.println("2. Enregistrer un dépôt (articles nouveaux ou existants)");
        System.out.println("3. Retirer des exemplaires vendus");
        System.out.println("4. Rechercher un article");
        System.out.println("5. Lister les dépôts d'un client");
        System.out.println("6. Lister les dépôts d'un article sur une période");
        System.out.println("7. Afficher tout l'établissement");
        System.out.println("8. Sauvegarder");
        System.out.println("0. Sauvegarder et quitter");
    }

    private static void saisirDepot(Etablissement boutique) {
        BonDepot bon = new BonDepot(texte("Téléphone du déposant : "));
        int nombre = entier("Nombre d'articles distincts : ", 1, BonDepot.MAX_LIGNES);
        Article[] nouveaux = new Article[nombre];
        int nbNouveaux = 0;
        for (int i = 0; i < nombre; i++) {
            System.out.println("Article " + (i + 1) + "/" + nombre);
            System.out.printf("ISSN : %d chiffres (Magazine) | ISBN : %d chiffres (Livre ou Manuel)%n",8, 13);
            String numero;

            // Redemander le numero tant que son format est incorrect
            while (true) {
                try {
                    numero = Article.normaliserNumero(texte("ISBN ou ISSN : "));
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Numero invalide : saisissez 8 chiffres pour un ISSN "
                            + "ou 13 chiffres pour un ISBN."
                    );
                }
            }
            
            int quantite = entier("Nombre d'exemplaires déposés : ", 1, Integer.MAX_VALUE);
            bon.ajouterLigne(numero, quantite);
            if (boutique.rechercher(numero) == null) {
                System.out.println("Nouvelle référence : création de sa fiche.");
                nouveaux[nbNouveaux++] = saisirArticle(numero);
            }
        }
        System.out.println("Récapitulatif du dépôt :");
        for (LigneDepot ligne : bon.getLignes()) System.out.println(ligne);
        if (!texte("Confirmer le dépôt (o/n) : ").equalsIgnoreCase("o")) {
            System.out.println("Dépôt annulé. Aucun stock modifié.");
            return;
        }
        boutique.ajouter(bon, Arrays.copyOf(nouveaux, nbNouveaux));
        System.out.println("Dépôt enregistré :");
        System.out.println(bon);
    }

    private static Article saisirArticle(String numero) {
    int type;

    if (numero.length() == 8) {
        // Un ISSN correspond uniquement a un magazine
        System.out.println("ISSN reconnu : type Magazine.");
        type = 3;
    } else {
        // Le numero a deja ete valide : il contient donc 13 chiffres
        System.out.println("ISBN reconnu : Livre ou Manuel.");
        type = entier("Type : 1 Livre, 2 Manuel : ", 1, 2);
    }

    String description = texte("Description : ");
    double prix = prix("Prix initial en euros : ");

    if (type == 3) {
        String periodicite = texte(
                "Periodicite (hebdomadaire, mensuel, trimestriel) : "
        );

        return new Magazine(
                description,
                prix,
                0,
                numero,
                periodicite,
                date("Publication (AAAA-MM-JJ) : ")
        );
    }

    int pages = entier(
            "Nombre de pages : ",
            1,
            Integer.MAX_VALUE
    );

    if (type == 2) {
        return new Manuel(
                description,
                prix,
                0,
                numero,
                pages,
                texte("Matiere : "),
                texte("Niveau scolaire : ")
        );
    }

    return new Livre(description, prix, 0, numero, pages);
    }

    private static String texte(String invite) {
        while (true) {
            System.out.print(invite);
            String valeur = CLAVIER.nextLine().trim();
            if (!valeur.isEmpty()) return valeur;
            System.out.println("La saisie ne doit pas être vide.");
        }
    }

    private static int entier(String invite, int minimum, int maximum) {
        while (true) {
            try {
                int valeur = Integer.parseInt(texte(invite));
                if (valeur >= minimum && valeur <= maximum) return valeur;
            } catch (NumberFormatException e) { /* La saisie est redemandée. */ }
            System.out.println("Saisir un entier entre " + minimum + " et " + maximum + ".");
        }
    }

    private static double prix(String invite) {
        while (true) {
            try {
                double valeur = Double.parseDouble(texte(invite).replace(',', '.'));
                if (Double.isFinite(valeur) && valeur >= 0) return valeur;
            } catch (NumberFormatException e) { /* La saisie est redemandée. */ }
            System.out.println("Saisir un prix positif ou nul.");
        }
    }

    private static LocalDate date(String invite) {
        while (true) {
            try { return LocalDate.parse(texte(invite)); }
            catch (DateTimeParseException e) { System.out.println("Date invalide. Format attendu : AAAA-MM-JJ."); }
        }
    }

    private static void sauvegarder(Etablissement boutique) throws IOException {
        boutique.versFichierArticles(ARTICLES);
        boutique.versFichierDepots(DEPOTS);
        System.out.println("Articles et bons de dépôt sauvegardés.");
    }
}
