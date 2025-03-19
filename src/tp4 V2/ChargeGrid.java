package mihan.sossou.tp4;

import java.sql.*;
import java.util.*;

public class ChargeGrid {
    private Connection connexion;

    public ChargeGrid() {
        try {
            connexion = connecterBD();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection connecterBD() throws SQLException {
        Connection connect;
        // Configuration de la connexion à la base de données
        connect = DriverManager.getConnection("jdbc:mysql://mysqln.istic.univ-rennes1.fr:3306/base_asossou", "user_asossou", "ISTIC@2025");
        return connect;
    }

    // Retourne la liste des grilles disponibles dans la BD
    public Map<Integer, String> availableGrids() {
    	
    	HashMap<Integer, String> grilles = new HashMap<Integer, String>();
    	 String query = "SELECT numero_grille, nom_grille, hauteur, largeur FROM GRID";

         try (Statement stmt = connexion.createStatement();
              ResultSet resultat = stmt.executeQuery(query)) {

             while (resultat.next()) {
                 int numeroGrille = resultat.getInt("numero_grille");
                 String nomGrille = resultat.getString("nom_grille");
                 int hauteur = resultat.getInt("hauteur");
                 int largeur = resultat.getInt("largeur");
                 

                 String description = nomGrille + "("+hauteur+"x"+largeur+")";
                 grilles.put(numeroGrille, description);
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
        return grilles;
    }

    // Extrait une grille spécifique de la BD
    public List<Crossword> extractGrid(int numGrille) {
    	List<Crossword> crosswords = new ArrayList<>();
    	String query = "SELECT horizontal, ligne, colonne, solution FROM CROSSWORD WHERE numero_grille = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
            pstmt.setInt(1, numGrille);
            ResultSet resultat = pstmt.executeQuery();

            while (resultat.next()) {
                //String definition = resultat.getString("definition");
                boolean horizontal = resultat.getInt("horizontal") == 1;
                int ligne = resultat.getInt("ligne");
                int colonne = resultat.getInt("colonne");
                String solution = resultat.getString("solution");
                
                Crossword crossword = new Crossword(horizontal, ligne,colonne, solution);
                crosswords.add(crossword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crosswords;
    }
    
  
    
    public static void main(String[] args) {
    	ChargeGrid chargeGrid = new ChargeGrid();
    	
    	Map<Integer, String> grilles = chargeGrid.availableGrids();

        System.out.println(grilles);
    
        List<Crossword> entries = chargeGrid.extractGrid(4);

        for (Crossword entry : entries) {
            System.out.println(entry);
        }

	}
    
}