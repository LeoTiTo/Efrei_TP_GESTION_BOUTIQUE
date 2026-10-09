/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp2_exo1;

import java.time.LocalDate;
import java.util.Locale;

/**
 *
 * @author lochu
 */
public class Magazine extends Article {
    private final String issn;
    private final String periodicite;
    private final LocalDate datePublication;

    public Magazine(String description, double prixInitial, int nbExemplaires, String issn,
            String periodicite, LocalDate datePublication) {
        super(description, prixInitial, nbExemplaires);
        this.issn = normaliserNumero(issn);
        this.periodicite = verifierTexte(periodicite, "Périodicité").toLowerCase(Locale.ROOT);
        if (this.issn.length() != 8 || datePublication == null
                || !(this.periodicite.equals("hebdomadaire") || this.periodicite.equals("mensuel")
                || this.periodicite.equals("trimestriel"))) {
            throw new IllegalArgumentException("ISSN, périodicité ou date incorrect.");
        }
        this.datePublication = datePublication;
    }

    @Override
    public String getNumero() { return issn; }
    public String getPeriodicite() { return periodicite; }
    public LocalDate getDatePublication() { return datePublication; }

    @Override
    public double calculerPrix(LocalDate date) {
        LocalDate seuil50;
        LocalDate seuil75;
        if (periodicite.equals("hebdomadaire")) {
            // réduction de 50 % après 2 semaines, de 75 % après 4 semaines.
            seuil50 = datePublication.plusWeeks(2);
            seuil75 = datePublication.plusWeeks(4);
        } else {
            // Si le magazine est mensuel, on choisit 2 mois.
            // Pour toute autre périodicité traitée ici, on choisit 6 mois.
            int mois = periodicite.equals("mensuel") ? 2 : 6;
            seuil50 = datePublication.plusMonths(mois);
            seuil75 = datePublication.plusMonths(2 * mois);
        }
        // « Plus de » impose une comparaison stricte, même à la date anniversaire.
        // On vérifie d'abord la réduction la plus forte :
        // une date dépassant seuil75 dépasse aussi seuil50.
        // isAfter signifie « strictement après » : le jour du seuil est exclu.
        if (date.isAfter(seuil75)) return getPrixInitial() * 0.25;
        if (date.isAfter(seuil50)) return getPrixInitial() * 0.5;
        return getPrixInitial();
    }

    @Override
    public String versFichier() {
        return issn + System.lineSeparator() + champsCommuns() + " : " + periodicite
                + " : " + datePublication;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + periodicite + " | publication : " + datePublication;
    }
}
