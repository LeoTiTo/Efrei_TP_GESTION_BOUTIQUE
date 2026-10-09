/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp2_exo1;

import java.time.LocalDate;
import java.util.Arrays;

/**
 *
 * @author lochu
 */
public class BonDepot {
    public static final int MAX_LIGNES = 50;
    private static int prochainNumero = 1;
    private int numero;
    private final String telephone;
    private LocalDate dateEmission;
    private int nbArticles;
    private final LigneDepot[] lignes = new LigneDepot[MAX_LIGNES];

    // Le numéro est attribué à l'enregistrement pour éviter les trous en cas d'annulation.
    public BonDepot(String telephone) {
        this.telephone = Article.verifierTexte(telephone, "Téléphone").replaceAll("\\s", "");
        if (!this.telephone.matches("\\+?[0-9]{6,15}")) {
            throw new IllegalArgumentException("Téléphone incorrect (6 à 15 chiffres, + facultatif).");
        }
        dateEmission = LocalDate.now();
    }

    public void ajouterLigne(String numeroArticle, int quantite) {
        if (numero != 0) throw new IllegalStateException("Un bon enregistré ne peut plus être modifié.");
        if (nbArticles == MAX_LIGNES) throw new IllegalStateException("Bon de dépôt plein.");
        LigneDepot ligne = new LigneDepot(numeroArticle, quantite);
        if (contient(ligne.getNumero())) throw new IllegalArgumentException("Référence déjà présente sur le bon.");
        lignes[nbArticles++] = ligne;
    }

    void enregistrer() {
        if (numero != 0 || nbArticles == 0) throw new IllegalStateException("Bon déjà enregistré ou vide.");
        if (prochainNumero == Integer.MAX_VALUE) throw new IllegalStateException("Numérotation épuisée.");
        numero = prochainNumero++;
        dateEmission = LocalDate.now();
    }

    void restaurer(int numero, LocalDate date) {
        if (numero <= 0 || numero == Integer.MAX_VALUE || date == null || nbArticles == 0) {
            throw new IllegalArgumentException("Bon de dépôt invalide.");
        }
        this.numero = numero;
        dateEmission = date;
    }

    static void reprendreNumerotation(int dernierNumero) {
        prochainNumero = Math.max(prochainNumero, dernierNumero + 1);
    }

    public int getNumero() { return numero; }
    public String getTelephone() { return telephone; }
    public LocalDate getDateEmission() { return dateEmission; }
    public int getNbArticles() { return nbArticles; }
    public LigneDepot[] getLignes() { return Arrays.copyOf(lignes, nbArticles); }

    public boolean contient(String numeroArticle) {
        String n = Article.normaliserNumero(numeroArticle);
        for (int i = 0; i < nbArticles; i++) {
            if (lignes[i].getNumero().equals(n)) return true;
        }
        return false;
    }

    public String versFichier() {
        String sep = System.lineSeparator();
        StringBuilder resultat = new StringBuilder(numero + sep + telephone + " : "
                + dateEmission + " : " + nbArticles);
        for (int i = 0; i < nbArticles; i++) resultat.append(sep).append(lignes[i].versFichier());
        return resultat.toString();
    }

    @Override
    public String toString() {
        StringBuilder resultat = new StringBuilder("Bon n° " + numero + " | " + dateEmission
                + " | téléphone : " + telephone + " | articles distincts : " + nbArticles);
        for (int i = 0; i < nbArticles; i++) resultat.append(System.lineSeparator()).append("  ").append(lignes[i]);
        return resultat.toString();
    }
}
