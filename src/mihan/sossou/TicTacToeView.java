package mihan.sossou.tp5;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TicTacToeView extends Application {

    @Override
    public void start(Stage primaryStage) {
    	
        //Récupération de l'instance du modèle
        TicTacToeModel model = TicTacToeModel.getInstance();

        //Création de la grille du jeu avec une GridPane
        GridPane gridPane = new GridPane();
        for (int row = 0; row < TicTacToeModel.BOARD_WIDTH; row++) {
            for (int col = 0; col < TicTacToeModel.BOARD_HEIGHT; col++) {
                TicTacToeSquare square = new TicTacToeSquare(row, col);
                gridPane.add(square, col, row);
            }
        }
        gridPane.setGridLinesVisible(true);

        //Bouton "Restart" qui relance la partie en appelant model.restart()
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> model.restart());

        //Label affichant le score pour le joueur X (FIRST)
        Label scoreXLabel = new Label();
        scoreXLabel.textProperty().bind(Bindings.concat(model.getScore(Owner.FIRST).asString(), " cases pour X"));

        //Label affichant le score pour le joueur O (SECOND)
        Label scoreOLabel = new Label();
        scoreOLabel.textProperty().bind(Bindings.concat(model.getScore(Owner.SECOND).asString(), " cases pour O"));
        
        
        //Calcul dynamique du nombre de cases libres
        IntegerBinding freeCellsBinding = Bindings.createIntegerBinding(() -> {
            int total = TicTacToeModel.BOARD_WIDTH * TicTacToeModel.BOARD_HEIGHT;
            int occupied = 0;

            for (int i = 0; i < TicTacToeModel.BOARD_WIDTH; i++) {
                for (int j = 0; j < TicTacToeModel.BOARD_HEIGHT; j++) {
                    if (model.getSquare(i, j).get() != Owner.NONE) {
                        occupied++; // Compter chaque case occupée
                    }
                }
            }
            return total - occupied; // Cases libres = total - cases occupées
        }, model.getBoardProperty()); // Observer toute la grille (Méthode dans le model)

        //Affichage dynamique du nombre de cases libres
        Label freeCellsLabel = new Label();
        freeCellsLabel.textProperty().bind(Bindings.concat(freeCellsBinding.asString(), " case libres"));

        // Label pour afficher le message de fin de jeu (gagnant ou match nul)
        Label endOfGameLabel = new Label();
        endOfGameLabel.textProperty().bind(model.getEndOfGameMessage());

        scoreXLabel.styleProperty().bind(
                Bindings.when(model.turnProperty().isEqualTo(Owner.FIRST))
                        .then("-fx-background-color: cyan; -fx-padding: 5;")
                        .otherwise("-fx-background-color: red; -fx-padding: 5;")
            );

        scoreOLabel.styleProperty().bind(
            Bindings.when(model.turnProperty().isEqualTo(Owner.SECOND))
                    .then("-fx-background-color: cyan; -fx-padding: 5;")
                    .otherwise("-fx-background-color: red; -fx-padding: 5;")
        );
        
        VBox infoBox = new VBox(10); // Espacement entre les lignes

        HBox line1 = new HBox(restartButton);
       
        HBox line2 = new HBox(20, scoreXLabel, scoreOLabel, freeCellsLabel);
        line2.setStyle("-fx-padding: 5;");


        //Ajout des lignes au panneau d'information
        infoBox.getChildren().addAll(line1, endOfGameLabel, line2);
        infoBox.setStyle("-fx-padding: 10;");

        //Utilisation d'un BorderPane pour disposer la grille et le panneau d'information
        BorderPane root = new BorderPane();
        root.setCenter(gridPane);
        root.setBottom(infoBox);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Morpion");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
