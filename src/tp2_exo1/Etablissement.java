/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp2_exo1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author lochu
 */
public class Etablissement {
    // Nombre maximal de références d'articles que le tableau peut stocker.
    // Cette limite ne concerne pas le nombre total d'exemplaires en stock
    // et ne limite pas directement le nombre de lignes du fichier articles.txt.    
    public static final int MAX_ARTICLES = 1000;
    public static final int MAX_DEPOTS = 1000;
    private final String nom;
    private Article[] articles = new Article[MAX_ARTICLES];
    private BonDepot[] depots = new BonDepot[MAX_DEPOTS];
    private int nbArticles;
    private int nbDepots;

    public Etablissement(String nom) { this.nom = Article.verifierTexte(nom, "Nom"); }
    public int getNbArticles() { return nbArticles; }
    public int getNbDepots() { return nbDepots; }
    public Article[] getArticles() { return Arrays.copyOf(articles, nbArticles); }
    public BonDepot[] getDepots() { return Arrays.copyOf(depots, nbDepots); }

    // Insertion dans le tableau principal, toujours trié selon ISBN/ISSN.
    private void inserer(Article article) {
        if (nbArticles == MAX_ARTICLES) throw new IllegalStateException("Catalogue plein.");
        if (rechercher(article.getNumero()) != null) throw new IllegalArgumentException("Référence déjà existante.");
        int i = nbArticles;
        while (i > 0 && articles[i - 1].placerApres(article)) {
            articles[i] = articles[i - 1];
            i--;
        }
        articles[i] = article;
        nbArticles++;
    }

    public void ajouter(String description, double prix, int quantite, String isbn, int pages) {
        inserer(new Livre(description, prix, quantite, isbn, pages));
    }

    public void ajouter(String description, double prix, int quantite, String isbn, int pages,
            String matiere, String niveau) {
        inserer(new Manuel(description, prix, quantite, isbn, pages, matiere, niveau));
    }

    public void ajouter(String description, double prix, int quantite, String issn,
            String periodicite, LocalDate publication) {
        inserer(new Magazine(description, prix, quantite, issn, periodicite, publication));
    }

    public Article rechercher(String numero) {
        String n = Article.normaliserNumero(numero);
        int debut = 0;
        int fin = nbArticles - 1;
        while (debut <= fin) {
            int milieu = (debut + fin) / 2;
            int comparaison = articles[milieu].getNumero().compareTo(n);
            if (comparaison == 0) return articles[milieu];
            if (comparaison < 0) debut = milieu + 1;
            else fin = milieu - 1;
        }
        return null;
    }

    private Article exigerArticle(String numero) {
        Article article = rechercher(numero);
        if (article == null) throw new IllegalArgumentException("Article introuvable.");
        return article;
    }

    public void ajouter(String numero, int quantite) { exigerArticle(numero).ajouter(quantite); }
    public void retirer(String numero, int quantite) { exigerArticle(numero).retirer(quantite); }

    // Enregistre un bon dont toutes les références sont déjà au catalogue.
    public void ajouter(BonDepot bon) { ajouter(bon, new Article[0]); }

    // Valide tout le dépôt avant de modifier le catalogue ou les stocks.
    // Les nouvelles fiches sont préparées avec un stock nul : le bon apporte le stock.
    public void ajouter(BonDepot bon, Article[] nouveaux) {
        if (bon == null || bon.getNumero() != 0 || bon.getNbArticles() == 0) {
            throw new IllegalArgumentException("Bon vide ou déjà enregistré.");
        }
        if (nouveaux == null || nbArticles + nouveaux.length > MAX_ARTICLES || nbDepots == MAX_DEPOTS) {
            throw new IllegalStateException("Capacité de stockage dépassée.");
        }
        for (int i = 0; i < nouveaux.length; i++) {
            Article a = nouveaux[i];
            if (a == null || a.getNbExemplaires() != 0 || rechercher(a.getNumero()) != null
                    || !bon.contient(a.getNumero())) {
                throw new IllegalArgumentException("Nouvelle fiche incorrecte ou non présente dans le bon.");
            }
            for (int j = 0; j < i; j++) {
                if (nouveaux[j].getNumero().equals(a.getNumero())) {
                    throw new IllegalArgumentException("Nouvelle référence en double.");
                }
            }
        }
        for (LigneDepot ligne : bon.getLignes()) {
            Article a = rechercher(ligne.getNumero());
            if (a == null) {
                for (Article nouveau : nouveaux) {
                    if (nouveau.getNumero().equals(ligne.getNumero())) a = nouveau;
                }
            }
            if (a == null || ligne.getNbExemplaires() > Integer.MAX_VALUE - a.getNbExemplaires()) {
                throw new IllegalArgumentException("Référence sans fiche ou quantité trop élevée.");
            }
        }
        bon.enregistrer();
        for (Article a : nouveaux) inserer(a);
        for (LigneDepot ligne : bon.getLignes()) ajouter(ligne.getNumero(), ligne.getNbExemplaires());
        insererBon(bon);
    }

    private void insererBon(BonDepot bon) {
        if (nbDepots == MAX_DEPOTS) throw new IllegalStateException("Tableau des dépôts plein.");
        int i = nbDepots;
        while (i > 0 && (depots[i - 1].getDateEmission().isAfter(bon.getDateEmission())
                || (depots[i - 1].getDateEmission().equals(bon.getDateEmission())
                && depots[i - 1].getNumero() > bon.getNumero()))) {
            depots[i] = depots[i - 1];
            i--;
        }
        depots[i] = bon;
        nbDepots++;
    }

    // Les méthodes lister préparent le texte ; seul le programme principal l'affiche.
    public String lister() {
        Article[] copie = getArticles();
        for (int i = 1; i < copie.length; i++) {
            Article courant = copie[i];
            int j = i - 1;
            while (j >= 0 && copie[j].getNbExemplaires() > courant.getNbExemplaires()) {
                copie[j + 1] = copie[j];
                j--;
            }
            copie[j + 1] = courant;
        }
        StringBuilder texte = new StringBuilder();
        LocalDate aujourdHui = LocalDate.now();
        for (Article a : copie) {
            texte.append(a).append(" | prix actuel : ")
                    .append(String.format(Locale.FRANCE, "%.2f EUR", a.calculerPrix(aujourdHui)))
                    .append(System.lineSeparator());
        }
        return texte.length() == 0 ? "Aucun article." : texte.toString();
    }

    public String lister(String telephone) {
        String recherche = Article.verifierTexte(telephone, "Téléphone").replaceAll("\\s", "");
        StringBuilder texte = new StringBuilder();
        for (int i = 0; i < nbDepots; i++) {
            if (depots[i].getTelephone().equals(recherche)) texte.append(depots[i]).append(System.lineSeparator());
        }
        return texte.length() == 0 ? "Aucun dépôt pour ce client." : texte.toString();
    }

    public String lister(String numero, LocalDate debut, LocalDate fin) {
        // Nettoie et vérifie le numéro ISBN ou ISSN recherché.
        String n = Article.normaliserNumero(numero);
        if (debut == null || fin == null || debut.isAfter(fin)) throw new IllegalArgumentException("Période invalide.");
        StringBuilder texte = new StringBuilder();     // Construit progressivement le texte contenant les bons trouvés.
        for (int i = 0; i < nbDepots; i++) {
            BonDepot b = depots[i];
            if (b.contient(n) && !b.getDateEmission().isBefore(debut) && !b.getDateEmission().isAfter(fin)) {
                // Ajoute la représentation textuelle du bon (via toString()),
                // puis un saut de ligne adapté au système d'exploitation.
                texte.append(b).append(System.lineSeparator());
            }
        }
        return texte.length() == 0 ? "Aucun dépôt pour cette référence sur cette période." : texte.toString();
    }

    public String listerArticlesDeposes(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || debut.isAfter(fin)) throw new IllegalArgumentException("Période invalide.");
        long[] quantites = new long[nbArticles];
        for (int i = 0; i < nbDepots; i++) {
            BonDepot bon = depots[i];
            if (!bon.getDateEmission().isBefore(debut) && !bon.getDateEmission().isAfter(fin)) {
                for (LigneDepot ligne : bon.getLignes()) {
                    for (int j = 0; j < nbArticles; j++) {
                        if (articles[j].getNumero().equals(ligne.getNumero())) {
                            quantites[j] += ligne.getNbExemplaires();
                            break;
                        }
                    }
                }
            }
        }
        StringBuilder texte = new StringBuilder();
        for (int i = 0; i < nbArticles; i++) {
            if (quantites[i] > 0) {
                texte.append(articles[i].getNumero()).append(" | ").append(articles[i].getDescription())
                        .append(" | quantité déposée : ").append(quantites[i]).append(System.lineSeparator());
            }
        }
        return texte.length() == 0 ? "Aucun article déposé sur cette période." : texte.toString();
    }

    public String listerParType(int type) {
        if (type < 1 || type > 3) throw new IllegalArgumentException("Type invalide : 1 Livre, 2 Manuel, 3 Magazine.");
        StringBuilder texte = new StringBuilder();
        for (int i = 0; i < nbArticles; i++) {
            Article article = articles[i];
            if ((type == 1 && article instanceof Livre && !(article instanceof Manuel))
                    || (type == 2 && article instanceof Manuel)
                    || (type == 3 && article instanceof Magazine)) {
                texte.append(article).append(System.lineSeparator());
            }
        }
        return texte.length() == 0 ? "Aucun article de ce type." : texte.toString();
    }

    public String rechercherParMotCle(String motCle) {
        String recherche = Article.verifierTexte(motCle, "Mot-clé").toLowerCase(Locale.ROOT);
        StringBuilder texte = new StringBuilder();
        for (int i = 0; i < nbArticles; i++) {
            if (articles[i].getDescription().toLowerCase(Locale.ROOT).contains(recherche)) {
                texte.append(articles[i]).append(System.lineSeparator());
            }
        }
        return texte.length() == 0 ? "Aucun article correspondant à ce mot-clé." : texte.toString();
    }

    public String listerParPrix(double minimum, double maximum) {
        if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum < 0 || minimum > maximum) {
            throw new IllegalArgumentException("Prix invalides : 0 <= minimum <= maximum.");
        }
        StringBuilder texte = new StringBuilder();
        LocalDate aujourdHui = LocalDate.now();
        for (int i = 0; i < nbArticles; i++) {
            double prixActuel = articles[i].calculerPrix(aujourdHui);
            if (prixActuel >= minimum && prixActuel <= maximum) {
                texte.append(articles[i]).append(" | prix actuel : ")
                        .append(String.format(Locale.FRANCE, "%.2f EUR", prixActuel)).append(System.lineSeparator());
            }
        }
        return texte.length() == 0 ? "Aucun article dans cette fourchette de prix." : texte.toString();
    }

    public void versFichierArticles(String fichier) throws IOException {
        try (FileWriter sortie = new FileWriter(fichier)) {
            for (int i = 0; i < nbArticles; i++) sortie.write(articles[i].versFichier() + System.lineSeparator());
        }
    }

    public void versFichierDepots(String fichier) throws IOException {
        try (FileWriter sortie = new FileWriter(fichier)) {
            for (int i = 0; i < nbDepots; i++) sortie.write(depots[i].versFichier() + System.lineSeparator());
        }
    }

    private static String ligneObligatoire(BufferedReader entree) throws IOException {
        String ligne = entree.readLine();
        if (ligne == null || ligne.trim().isEmpty()) throw new IOException("Fichier incomplet ou ligne vide.");
        return ligne.trim();
    }

    // Chargement dans un tableau temporaire : une erreur ne détruit pas le catalogue actuel.
    public void depuisFichierArticles(String fichier) throws IOException {
        Etablissement temporaire = new Etablissement(nom);
        try (BufferedReader entree = new BufferedReader(new FileReader(fichier))) { //essaye de lire le fichier article en paramètre, voir s'il existe
            String numero;
            while ((numero = entree.readLine()) != null) {
                String n = Article.normaliserNumero(numero.trim());
                String[] champs = ligneObligatoire(entree).split("\\s*:\\s*", -1);
                if (champs.length < 4) throw new IOException("Ligne article incomplète.");
                String description = champs[0];
                double prix = Double.parseDouble(champs[1]); //transformer un texte en nombre décimal pour lire un prix.
                int quantite = Integer.parseInt(champs[2]);
                if (n.length() == 8 && champs.length == 5) {
                    temporaire.ajouter(description, prix, quantite, n, champs[3], LocalDate.parse(champs[4]));
                } else if (n.length() == 13 && champs.length == 4) {
                    temporaire.ajouter(description, prix, quantite, n, Integer.parseInt(champs[3]));
                } else if (n.length() == 13 && champs.length == 6) {
                    temporaire.ajouter(description, prix, quantite, n, Integer.parseInt(champs[3]), champs[4], champs[5]);
                } else throw new IOException("Format d'article incorrect.");
            }
        } catch (RuntimeException e) {
            throw new IOException("Fichier articles invalide : " + e.getMessage(), e); //retourne exception si le fichier existe pas 
        }
        articles = temporaire.articles;
        nbArticles = temporaire.nbArticles;
    }

    public void depuisFichierDepots(String fichier) throws IOException {
        Etablissement temporaire = new Etablissement(nom);
        int maximum = 0;
        try (BufferedReader entree = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = entree.readLine()) != null) {
                int numero = Integer.parseInt(ligne.trim());
                String[] champs = ligneObligatoire(entree).split("\\s*:\\s*", -1);
                if (champs.length != 3) throw new IOException("En-tête de bon incorrect.");
                BonDepot bon = new BonDepot(champs[0]);
                LocalDate date = LocalDate.parse(champs[1]);
                int nombre = Integer.parseInt(champs[2]);
                if (nombre <= 0 || nombre > BonDepot.MAX_LIGNES) throw new IOException("Nombre de lignes incorrect.");
                for (int i = 0; i < nombre; i++) {
                    String[] detail = ligneObligatoire(entree).split("\\s*:\\s*", -1);
                    if (detail.length != 2) throw new IOException("Ligne de dépôt incorrecte.");
                    if (rechercher(detail[1]) == null) throw new IOException("Référence absente du catalogue : " + detail[1]);
                    bon.ajouterLigne(detail[1], Integer.parseInt(detail[0]));
                }
                for (int i = 0; i < temporaire.nbDepots; i++) {
                    if (temporaire.depots[i].getNumero() == numero) throw new IOException("Numéro de bon en double.");
                }
                bon.restaurer(numero, date);
                temporaire.insererBon(bon);
                maximum = Math.max(maximum, numero);
            }
        } catch (RuntimeException e) {
            throw new IOException("Fichier dépôts invalide : " + e.getMessage(), e);
        }
        // Restaurer l'historique ne doit surtout pas ajouter une seconde fois les stocks.
        depots = temporaire.depots;
        nbDepots = temporaire.nbDepots;
        BonDepot.reprendreNumerotation(maximum);
    }

    @Override
    public String toString() {
        StringBuilder texte = new StringBuilder(nom + " | " + nbArticles + " articles | " + nbDepots + " dépôts");
        for (int i = 0; i < nbArticles; i++) texte.append(System.lineSeparator()).append(articles[i]);
        for (int i = 0; i < nbDepots; i++) texte.append(System.lineSeparator()).append(depots[i]);
        return texte.toString();
    }
}
