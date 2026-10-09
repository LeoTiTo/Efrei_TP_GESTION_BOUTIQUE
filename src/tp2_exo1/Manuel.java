/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp2_exo1;

/**
 *
 * @author lochu
 */
public class Manuel extends Livre {
    private final String matEns;
    private final String niveauSco;

    public Manuel(String description, double prixInitial, int nbExemplaires, String isbn,
            int nbPages, String matEns, String niveauSco) {
        super(description, prixInitial, nbExemplaires, isbn, nbPages);
        this.matEns = verifierTexte(matEns, "Matière");
        this.niveauSco = verifierTexte(niveauSco, "Niveau scolaire");
    }

    public String getMatEns() { return matEns; }
    public String getNiveauSco() { return niveauSco; }

    @Override
    public String versFichier() { return super.versFichier() + " : " + matEns + " : " + niveauSco; }

    @Override
    public String toString() {
        return super.toString() + " | matière : " + matEns + " | niveau : " + niveauSco;
    }
}
