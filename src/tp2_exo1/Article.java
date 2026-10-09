/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp2_exo1;

import java.time.LocalDate;

/**
 *
 * @author lochu
 */
public abstract class Article {
    private final String description;
    private final double prixInitial;
    private int nbExemplaires;

    public Article(String description, double prixInitial, int nbExemplaires) {
        this.description = verifierTexte(description, "Description");
        if (!Double.isFinite(prixInitial) || prixInitial < 0 || nbExemplaires < 0) {
            throw new IllegalArgumentException("Prix et stock doivent être positifs ou nuls.");
        }
        this.prixInitial = prixInitial;
        this.nbExemplaires = nbExemplaires;
    }
    // Vérifie qu'un texte est renseigné et ne contient aucun caractère interdit.
    // Le paramètre nom indique le champ concerné dans le message d'erreur.
    // static permet d’appeler la méthode sur la classe, sans créer d’objet
    static String verifierTexte(String texte, String nom) {
        if (texte == null || texte.trim().isEmpty() || texte.contains(":")
                || texte.contains("\n") || texte.contains("\r")) {
            throw new IllegalArgumentException(nom + " vide ou contenant un caractère interdit.");
        }
        return texte.trim();
    }

    static String normaliserNumero(String numero) {
        if (numero == null) throw new IllegalArgumentException("Numéro absent.");
        String n = numero.replaceAll("[\\s-]", "");
        if (!n.matches("[0-9]{8}|[0-9]{13}")) {
            throw new IllegalArgumentException("Un ISSN comporte 8 chiffres et un ISBN 13 chiffres.");
        }
        return n;
    }

    public String getDescription() { return description; }
    public double getPrixInitial() { return prixInitial; }
    public int getNbExemplaires() { return nbExemplaires; }
    public abstract String getNumero();

    public boolean placerApres(Article autre) {
        return getNumero().compareTo(autre.getNumero()) > 0;
    }

    public void ajouter(int quantite) {
        if (quantite <= 0 || quantite > Integer.MAX_VALUE - nbExemplaires) {
            throw new IllegalArgumentException("Quantité invalide ou stock trop élevé.");
        }
        nbExemplaires += quantite;
    }

    public void retirer(int quantite) {
        if (quantite <= 0 || quantite > nbExemplaires) {
            throw new IllegalArgumentException("Quantité invalide ou stock insuffisant.");
        }
        nbExemplaires -= quantite;
    }

    public double calculerPrix() { return calculerPrix(LocalDate.now()); }
    public abstract double calculerPrix(LocalDate date);
    public abstract String versFichier();

    protected String champsCommuns() {
        return description + " : " + prixInitial + " : " + nbExemplaires;
    }

    @Override
    public String toString() {
        return getNumero() + " | " + description + " | stock : " + nbExemplaires
                + " | prix initial : " + prixInitial + " EUR";
    }
}
