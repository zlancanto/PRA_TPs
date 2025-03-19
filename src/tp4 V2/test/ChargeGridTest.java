package mihan.sossou.tp4.test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import mihan.sossou.tp4.ChargeGrid;
import mihan.sossou.tp4.Crossword;

import java.sql.*;
import java.util.List;

public class ChargeGridTest {
    private ChargeGrid chargeGrid;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        chargeGrid = new ChargeGrid();

        // Connexion à la base de données
        connection = DriverManager.getConnection("jdbc:mysql://mysqln.istic.univ-rennes1.fr:3306/base_asossou", "user_asossou", "ISTIC@2025");

    }

    //Méthode pour récupérer la valeur de la colonne 'controle' dans GRILLE
    private String getControleFromDB(int numGrille) throws SQLException {
        String controle = "";
        String query = "SELECT controle FROM GRID WHERE numero_grille = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, numGrille);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    controle = rs.getString("controle");
                }
            }
        }
        return (controle != null) ? controle : "";
    }

    private void applyControlGrid(char[][] grid, String controle, int maxLigne, int maxColonne) {
        int index = 0;
        for (int i = 0; i <= maxLigne; i++) {
            for (int j = 0; j <= maxColonne; j++) {
                if (index < controle.length()) {
                    char controlChar = controle.charAt(index);
                    if (controlChar == '*') {
                        grid[i][j] = '*';  // Appliquer les cases noires correctement
                    }
                }
                index++;
            }
        }
    }

    
    @Test
    public void testExtractGrid() throws SQLException {
        int numGrille = 10;  // On teste la grille numéro 10

        // Récupération de la solution correcte depuis la BDD
        String expectedSolution = getControleFromDB(numGrille);
        assertNotNull("La solution de contrôle ne doit pas être nulle", expectedSolution);

        // Extraction des mots croisés avec extractGrid()
        List<Crossword> crosswords = chargeGrid.extractGrid(numGrille);
        assertNotNull("La liste ne doit pas être nulle", crosswords);
        assertFalse("La liste ne doit pas être vide", crosswords.isEmpty());

        // Reconstruction de la grille à partir des objets Crossword
        String actualSolution = buildGridFromEntries(crosswords, expectedSolution);
        
        System.out.println("Solution attendue (BDD) : " + expectedSolution);
        System.out.println("Solution générée : " + actualSolution);
        System.out.println("Liste des mots récupérés :");
        for (Crossword word : crosswords) {
            System.out.println(word);
        }

        assertEquals("La grille reconstruite doit être identique à la solution attendue",
                     expectedSolution, actualSolution);
    }

    //Méthode pour reconstruire la grille à partir des mots croisés récupérés
    private String buildGridFromEntries(List<Crossword> crosswords, String controle) {
        // On récupère la hauteur et la largeur max pour construire la grille
        int maxLigne = 0, maxColonne = 0;
        for (Crossword e : crosswords) {
            maxLigne = Math.max(maxLigne, e.getLigne());
            maxColonne = Math.max(maxColonne, e.getColonne() + e.getSolution().length() - 1);
        }

        // On initialise la grille avec des étoiles (cases noires)
        char[][] grid = new char[maxLigne + 1][maxColonne + 1];
        for (int i = 0; i <= maxLigne; i++) {
            for (int j = 0; j <= maxColonne; j++) {
                grid[i][j] = '.'; //
            }
        }

        // On place chaque mot à sa position
        for (Crossword entry : crosswords) {
            int row = entry.getLigne();
            int col = entry.getColonne();

            String word = entry.getSolution().toUpperCase();

            if (entry.isHorizontal()) {
                for (int i = 0; i < word.length(); i++) {
                    grid[row][col + i] = word.charAt(i);
                }
            } else { // Mot vertical
                for (int i = 0; i < word.length(); i++) {
                    grid[row + i][col] = word.charAt(i);
                }
            }
        }
        
        applyControlGrid(grid, controle, maxLigne, maxColonne);

        //Comparer avec la grille `controle` pour ajouter les cases noires (`*`)

        // On reconstruit la chaîne en lisant ligne par ligne
        StringBuilder reconstructed = new StringBuilder();
        for (int i = 0; i <= maxLigne; i++) {
            for (int j = 0; j <= maxColonne; j++) {
                reconstructed.append(grid[i][j]);
            }
            reconstructed.append("\n");  //Ajoute un saut de ligne après chaque ligne
        }
        return reconstructed.toString();
    }
}
