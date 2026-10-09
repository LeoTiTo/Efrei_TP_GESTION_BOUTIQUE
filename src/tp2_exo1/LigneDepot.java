/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp2_exo1;

/**
 *
 * @author lochu
 */
public class LigneDepot {
    private final String numero;
    private final int nbExemplaires;

    public LigneDepot(String numero, int nbExemplaires) {
        this.numero = Article.normaliserNumero(numero);
        if (nbExemplaires <= 0) throw new IllegalArgumentException("Quantité déposée non positive.");
        this.nbExemplaires = nbExemplaires;
    }

    public String getNumero() { return numero; }
    public int getNbExemplaires() { return nbExemplaires; }
    public String versFichier() { return nbExemplaires + " : " + numero; }

    @Override
    public String toString() { return numero + " | quantité déposée : " + nbExemplaires; }
}
