package mihan.sossou.crossword.controller;

import mihan.sossou.crossword.model.CrosswordBD;
import mihan.sossou.crossword.model.GridSize;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Connection connexion;
    private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/base_zmihan";

    public Database() {
        try {
            connexion = connecterBD();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return la connexion à la BD
     * @throws SQLException
     */
    private static Connection connecterBD() throws SQLException {
        Connection connect;
        connect = DriverManager.getConnection(DATABASE_URL, "root", "");
        return connect;
    }

    /**
     * @param numGrille
     * @return une grille spécifique de la BD
     */
    public List<CrosswordBD> extractGrid(int numGrille) {
        List<CrosswordBD> crosswordsBD = new ArrayList<>();
        String query = "SELECT horizontal, ligne, colonne, solution, definition FROM CROSSWORD WHERE numero_grille = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
            pstmt.setInt(1, numGrille);
            ResultSet resultat = pstmt.executeQuery();

            while (resultat.next()) {
                int ligne = resultat.getInt("ligne");
                int colonne = resultat.getInt("colonne");
                String solution = resultat.getString("solution");
                boolean horizontal = resultat.getBoolean("horizontal");
                String definition = resultat.getString("definition");

                CrosswordBD crosswordBD = new CrosswordBD(ligne, colonne, solution, horizontal, definition);
                crosswordsBD.add(crosswordBD);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return crosswordsBD;
    }

    /**
     * @param numGrille
     * @return la taille de la grille
     */
    public GridSize getGridSize(int numGrille) {

        int hauteur = 0;
        int largeur = 0;
        // Par défaut size = (hauteur = 0, largeur = 0)
        GridSize SIZE = GridSize.SIZE;
        String query = "SELECT hauteur, largeur, FROM GRID WHERE numero_grille = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
            pstmt.setInt(1, numGrille);
            ResultSet resultat = pstmt.executeQuery();

            hauteur = resultat.getInt("hauteur");
            largeur = resultat.getInt("largeur");

            // Mise à jour de size
            SIZE.setHeight(hauteur);
            SIZE.setWidth(largeur);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return SIZE;
    }

    /**
     * @return la liste des grilles disponibles dans la BD
     */
    public Map<Integer, String> availableGrids() {

        Map<Integer, String> grilles = new HashMap<>();
        String query = "SELECT numero_grille, nom_grille, hauteur, largeur FROM GRID";

        try (
                Statement stmt = connexion.createStatement();
                ResultSet resultat = stmt.executeQuery(query)
        ) {

            while (resultat.next()) {
                int numeroGrille = resultat.getInt("numero_grille");
                String nomGrille = resultat.getString("nom_grille");
                int hauteur = resultat.getInt("hauteur");
                int largeur = resultat.getInt("largeur");


                String description = nomGrille + " (" + hauteur + "x" + largeur + ")";
                grilles.put(numeroGrille, description);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return grilles;
    }
}
