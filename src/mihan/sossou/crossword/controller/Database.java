package mihan.sossou.crossword.controller;

import mihan.sossou.crossword.model.CrosswordBD;
import mihan.sossou.crossword.enums.GridSize;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la connexion et les requêtes à la base de données MySQL
 * contenant les grilles de mots croisés.
 */
public class Database {
    // L'objet de connexion à la BD
    private Connection connexion;
    // URL de connexion JDBC
    private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/base_zmihan";
    // Identifiants de connexion
    private final static String DB_USER = "root";
    private final static String DB_PASSWORD = "";

    /**
     * Constructeur. Établit la connexion à la base de données lors de l'instanciation.
     * Affiche une trace d'erreur si la connexion échoue.
     */
    public Database() {
        try {
            // Charge le driver MySQL
            connexion = connecterBD();
            System.out.println("Connexion à la base de données établie.");
        } catch (SQLException e) {
            System.err.println("ERREUR: Échec de la connexion à la base de données !");
            e.printStackTrace();

        }
    }

    /**
     * Établit et retourne une connexion à la base de données.
     *
     * @return L'objet Connection.
     * @throws SQLException Si la connexion échoue.
     */
    private static Connection connecterBD() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Récupère le nombre total de grilles disponibles dans la table GRID.
     *
     * @return Le nombre de grilles, ou 0 si erreur ou aucune grille.
     */
    public int gridSize() {
        int count = 0;
        // Vérifie si la connexion est valide
        if (connexion == null) {
            System.err.println("Erreur gridSize: Connexion BD non établie.");
            return 0;
        }
        String query = "SELECT COUNT(*) FROM GRID";

        try (Statement stmt = connexion.createStatement();
             // Exécute la requête
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                // Récupère la valeur de la première colonne (le COUNT)
                count = rs.getInt(1);
            }
            // Pas besoin de else ici, count reste 0 si pas de résultat

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération du nombre de grilles:");
            e.printStackTrace();
            // Retourne 0 en cas d'erreur SQL
        }
        return count;
    }

    /**
     * Extrait toutes les données (mots, définitions) pour une grille spécifique.
     * @param numGrille Le numéro de la grille à extraire.
     * @return Une List de CrosswordBD contenant les données,
     * ou une liste vide si erreur ou grille non trouvée.
     */
    public List<CrosswordBD> extractGrid(int numGrille) {
        List<CrosswordBD> crosswordsBD = new ArrayList<>();
        if (connexion == null) {
            System.err.println("Erreur extractGrid: Connexion BD non établie.");
            // Retourne liste vide
            return crosswordsBD;
        }
        // Requête paramétrée pour éviter les injections SQL
        String query = "SELECT horizontal, ligne, colonne, solution, definition FROM CROSSWORD WHERE numero_grille = ?";

        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
            pstmt.setInt(1, numGrille);
            ResultSet resultat = pstmt.executeQuery();

            // Itère sur tous les résultats (chaque ligne correspond à un mot/définition)
            while (resultat.next()) {
                // Récupère les données de chaque colonne
                boolean horizontal = resultat.getBoolean("horizontal");
                int ligne = resultat.getInt("ligne");
                int colonne = resultat.getInt("colonne");
                String solution = resultat.getString("solution");
                String definition = resultat.getString("definition");

                // Crée un objet DTO CrosswordBD et l'ajoute à la liste
                CrosswordBD crosswordData = new CrosswordBD(ligne, colonne, solution, horizontal, definition);
                crosswordsBD.add(crosswordData);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'extraction de la grille " + numGrille + ":");
            e.printStackTrace();
            // Retourne la liste potentiellement vide en cas d'erreur
        }
        return crosswordsBD;
    }

    /**
     * Récupère la hauteur et la largeur d'une grille spécifique.
     * Note: Modifie directement l'instance statique GridSize.SIZE
     * @param numGrille Le numéro de la grille.
     * @return L'instance GridSize.SIZE mise à jour, ou l'instance non modifiée si erreur/non trouvé.
     * @throws RuntimeException si la grille spécifiée n'existe pas dans la BD.
     */
    public GridSize getGridSize(int numGrille) {
        GridSize size = GridSize.SIZE;
        if (connexion == null) {
            System.err.println("Erreur getGridSize: Connexion BD non établie.");
            // Retourne l'instance potentiellement non initialisée
            return size;
        }
        String query = "SELECT hauteur, largeur FROM GRID WHERE numero_grille = ?";

        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
            pstmt.setInt(1, numGrille);
            ResultSet resultat = pstmt.executeQuery();

            if (resultat.next()) {
                int hauteur = resultat.getInt("hauteur");
                int largeur = resultat.getInt("largeur");
                // Modifie directement l'instance de l'enum
                size.setHeight(hauteur);
                size.setWidth(largeur);
            } else {
                // Si la grille n'est pas trouvée, lève une exception
                throw new RuntimeException("La grille spécifiée (" + numGrille + ") n'existe pas.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération de la taille de la grille " + numGrille + ":");
            e.printStackTrace();
            // En cas d'erreur SQL, l'instance 'size' n'est pas modifiée
        }
        // Retourne l'instance (potentiellement modifiée)
        return size;
    }

    /**
     * Méthode pour fermer la connexion à la base de données.
     * Utile à appeler lors de l'arrêt de l'application.
     */
    public void closeConnection() {
        if (connexion != null) {
            try {
                connexion.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion BD");
                e.printStackTrace();
            }
        }
    }
}
