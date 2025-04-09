package mihan.sossou.crossword.app;

import javafx.application.Application;
import javafx.application.Platform; // Importation de Platform
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode; // Importation de KeyCode
import javafx.scene.input.KeyCodeCombination; // Importation de KeyCodeCombination
import javafx.scene.input.KeyCombination; // Importation de KeyCombination
import javafx.stage.Stage;
import mihan.sossou.crossword.controller.CrosswordController;
import mihan.sossou.crossword.controller.Database;

import java.io.IOException;
import java.net.URL;

/**
 * Classe principale de l'application Mots Croisés.
 * Charge la vue FXML, initialise le contrôleur, et affiche la fenêtre principale (Stage).
 */
public class CrosswordApp extends Application {

    private Database database;
    private CrosswordController controller;

    @Override
    public void init() throws Exception {
        super.init();
        // Initialise la connexion à la base de données
        try {
            database = new Database();
        } catch (Exception e) {
            System.err.println("Échec de la connexion à la base de données. Vérifiez les paramètres de connexion et le serveur BD.");
            e.printStackTrace();
            // Envisager Platform.exit() si la BD est essentielle
            throw new RuntimeException("La connexion à la base de données a échoué", e);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charge le fichier FXML
            // S'assure que le fichier FXML est dans le bon chemin de ressources
            URL fxmlUrl = getClass().getResource("/mihan/sossou/crossword/view/CrosswordView.fxml");
            if (fxmlUrl == null) {
                System.err.println("Impossible de charger le fichier FXML. Vérifiez le chemin : /mihan/sossou/crossword/view/CrosswordView.fxml");
                throw new IOException("Fichier FXML non trouvé.");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Récupère l'instance du contrôleur
            controller = loader.getController();
            if (controller == null) {
                System.err.println("Contrôleur non injecté par FXMLLoader. Vérifiez l'attribut fx:controller dans FXML.");
                throw new RuntimeException("L'initialisation du contrôleur a échoué.");
            }
            // Passe l'instance de la base de données au contrôleur
            controller.setDatabase(database);

            // Charge une grille aléatoire au démarrage
            int initialPuzzleNumber = (int) (Math.random() * 10) + 1; // Ou obtenir depuis config, args, ou aléatoire
            try {
                // Vérifie si des grilles existent
                if (database.gridSize() > 0) {
                    // charger aléatoirement un crossword
                    controller.loadPuzzle(initialPuzzleNumber);
                } else {
                    System.err.println("Aucune grille trouvée dans la base de données.");
                    // afficher un message d'erreur dans l'UI
                    controller.showError("Aucune grille disponible dans la base de données.");
                }
            } catch (Exception e) {
                System.err.println("Échec du chargement de la grille initiale : " + initialPuzzleNumber); e.printStackTrace();
                // Optionnellement, afficher un message d'erreur dans l'UI
                controller.showError("Erreur lors du chargement de la grille : " + e.getMessage());
            }
            // --- Fin Chargement Grille Initiale ---

            // Crée la scène
            Scene scene = new Scene(root);

            // Charge la feuille de style CSS
            URL cssUrl = getClass().getResource("/mihan/sossou/crossword/view/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS chargé avec succès.");
            } else {
                System.err.println("Attention : Fichier CSS non trouvé à /mihan/sossou/crossword/view/styles.css");
            }

            // *** AJOUT RACCOURCI CTRL+W ***
            final KeyCombination ctrlW = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
            scene.setOnKeyPressed(event -> {
                if (ctrlW.match(event)) {
                    System.out.println("Ctrl+W pressé - Fermeture de l'application.");
                    Platform.exit();
                }
            });

            // Configure la fenêtre principale (Stage)
            primaryStage.setTitle("Crossword Puzzle");
            primaryStage.setScene(scene);
            // Ajuste la taille de la fenêtre au contenu de la scène
            primaryStage.sizeToScene();
            // Autorise le redimensionnement
            primaryStage.setResizable(true);
            primaryStage.show();

            // Demande le focus sur le gridPane après l'affichage de la fenêtre
            // pour activer la navigation clavier immédiatement.
            if (controller.getGridPane() != null) {
                controller.getGridPane().requestFocus();
            }

        } catch (IOException e) {
            System.err.println("Échec du chargement de l'interface utilisateur de l'application."); e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Une erreur d'exécution s'est produite au démarrage."); e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        // Effectue tout nettoyage nécessaire, comme fermer les connexions BD
        System.out.println("Fermeture de l'application Mots Croisés.");
        database.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
