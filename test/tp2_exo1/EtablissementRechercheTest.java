package tp2_exo1;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class EtablissementRechercheTest {
    public static void main(String[] args) throws Exception {
        String livre = "9782707356741";
        String manuel = "9782210446694";
        String magazine = "00208245";
        LocalDate debut = LocalDate.of(2026, 10, 1);
        LocalDate fin = LocalDate.of(2026, 10, 15);
        Etablissement boutique = new Etablissement("Boutique test");
        boutique.ajouter("Java debutant", 20, 10, livre, 100);
        boutique.ajouter("Grammaire", 40, 3, manuel, 200, "Francais", "College");
        boutique.ajouter("Geo", 8, 7, magazine, "mensuel", LocalDate.now().minusYears(2));

        Path fichier = Files.createTempFile("tp2-depots-test-", ".txt");
        try {
            Files.write(fichier, ("1\n0612345678 : 2026-10-01 : 1\n2 : " + livre
                    + "\n2\n0612345678 : 2026-10-15 : 2\n3 : " + livre + "\n7 : " + manuel
                    + "\n3\n0612345678 : 2026-09-30 : 1\n50 : " + magazine
                    + "\n4\n0612345678 : 2026-10-16 : 1\n70 : " + magazine + "\n")
                    .getBytes(StandardCharsets.UTF_8));
            boutique.depuisFichierDepots(fichier.toString());
        } finally {
            Files.deleteIfExists(fichier);
        }
        String avant = boutique.toString();

        String periode = boutique.listerArticlesDeposes(debut, fin);
        assert ligne(periode, livre).contains("quantité déposée : 5") : periode;
        assert ligne(periode, manuel).contains("quantité déposée : 7") : periode;
        assert periode.indexOf(livre) == periode.lastIndexOf(livre) : "Article répété";
        assert !periode.contains(magazine) : "Dépôt hors période retenu";
        assert ligne(boutique.listerArticlesDeposes(debut, debut), livre)
                .contains("quantité déposée : 2") : "Borne de début exclue";
        assert ligne(boutique.listerArticlesDeposes(fin, fin), livre)
                .contains("quantité déposée : 3") : "Borne de fin exclue";

        String livres = boutique.listerParType(1);
        assert livres.contains(livre) && !livres.contains(manuel) && !livres.contains(magazine);
        String manuels = boutique.listerParType(2);
        assert manuels.contains(manuel) && !manuels.contains(livre) && !manuels.contains(magazine);
        String magazines = boutique.listerParType(3);
        assert magazines.contains(magazine) && !magazines.contains(livre) && !magazines.contains(manuel);

        String recherche = boutique.rechercherParMotCle("  jAvA  ");
        assert recherche.contains(livre) && !recherche.contains(manuel) && !recherche.contains(magazine);
        assert boutique.rechercherParMotCle("debu").contains(livre) : "Recherche partielle absente";
        assert boutique.rechercherParMotCle("introuvable").startsWith("Aucun");

        String prix = boutique.listerParPrix(2, 2);
        assert prix.contains(magazine) && !prix.contains(livre) && !prix.contains(manuel)
                : "Le filtre doit utiliser le prix réduit et inclure les bornes";
        assert boutique.listerParPrix(8, 8).startsWith("Aucun") : "Prix initial utilisé";
        assert boutique.listerParPrix(0, 1.99).startsWith("Aucun");

        refuser(() -> boutique.listerArticlesDeposes(fin, debut));
        refuser(() -> boutique.listerArticlesDeposes(null, fin));
        refuser(() -> boutique.listerArticlesDeposes(debut, null));
        refuser(() -> boutique.listerParType(0));
        refuser(() -> boutique.listerParType(4));
        refuser(() -> boutique.rechercherParMotCle("  "));
        refuser(() -> boutique.rechercherParMotCle(null));
        refuser(() -> boutique.listerParPrix(10, 5));
        refuser(() -> boutique.listerParPrix(-1, 5));
        refuser(() -> boutique.listerParPrix(Double.NaN, 5));
        refuser(() -> boutique.listerParPrix(0, Double.POSITIVE_INFINITY));

        Etablissement vide = new Etablissement("Vide");
        assert vide.listerArticlesDeposes(debut, fin).startsWith("Aucun");
        assert vide.listerParType(1).startsWith("Aucun");
        assert vide.rechercherParMotCle("java").startsWith("Aucun");
        assert vide.listerParPrix(0, 100).startsWith("Aucun");
        assert boutique.toString().equals(avant) : "Les recherches ont modifié les données";
        System.out.println("OK : période, type, mot-clé, prix et validations.");
    }

    private static String ligne(String texte, String numero) {
        for (String ligne : texte.split("\\R")) {
            if (ligne.contains(numero)) return ligne;
        }
        throw new AssertionError("Article absent : " + numero);
    }

    private static void refuser(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException attendu) {
            return;
        }
        throw new AssertionError("Saisie invalide acceptée");
    }
}
