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
public class Livre extends Article {
    private final String isbn;
    private final int nbPages;

    public Livre(String description, double prixInitial, int nbExemplaires, String isbn, int nbPages) {
        super(description, prixInitial, nbExemplaires);
        this.isbn = normaliserNumero(isbn);
        if (this.isbn.length() != 13 || nbPages <= 0) {
            throw new IllegalArgumentException("ISBN ou nombre de pages incorrect.");
        }
        this.nbPages = nbPages;
    }

    @Override
    public String getNumero() { return isbn; }
    public int getNbPages() { return nbPages; }

    @Override
    public double calculerPrix(LocalDate date) {
        return getPrixInitial() * (date.getMonthValue() == 4 ? 0.5 : 1);
    }

    @Override
    public String versFichier() {
        return isbn + System.lineSeparator() + champsCommuns() + " : " + nbPages;
    }

    @Override
    public String toString() { return super.toString() + " | pages : " + nbPages; }
}
