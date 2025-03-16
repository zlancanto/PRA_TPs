package mihan.sossou.morpion;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
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
        for (int row = 0; row < TicTacToeModel.BOARD_HEIGHT; row++) {
            for (int col = 0; col < TicTacToeModel.BOARD_WIDTH; col++) {
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
        scoreXLabel.textProperty().bind(model.playerScoreDescription(Owner.FIRST));

        //Label affichant le score pour le joueur O (SECOND)
        Label scoreOLabel = new Label();
        scoreOLabel.textProperty().bind(model.playerScoreDescription(Owner.SECOND));

        //Affichage dynamique du nombre de cases libres
        Label freeCellsLabel = new Label();
        freeCellsLabel.textProperty().bind(model.casesLibresDescription());

        // Label pour afficher le message de fin de jeu (gagnant ou match nul)
        Label endOfGameLabel = new Label();
        endOfGameLabel.textProperty().bind(model.getEndOfGameMessage());

        scoreXLabel.styleProperty().bind(
            Bindings.when(model.turnProperty().isEqualTo(Owner.FIRST))
                .then("-fx-background-color: cyan;")
                .otherwise("-fx-background-color: red;")
        );

        scoreOLabel.styleProperty().bind(
            Bindings.when(model.turnProperty().isEqualTo(Owner.SECOND))
                .then("-fx-background-color: cyan;")
                .otherwise("-fx-background-color: red;")
        );
        

       
        HBox line2 = new HBox(20, scoreXLabel, scoreOLabel, freeCellsLabel);

        //Ajout des lignes au panneau d'information
        VBox infoBox = new VBox(restartButton, endOfGameLabel, line2);
        infoBox.setStyle("-fx-padding: 10 0 20 0;");

        //Utilisation d'un BorderPane pour disposer la grille et le panneau d'information
        BorderPane root = new BorderPane();
        root.setCenter(gridPane);
        root.setBottom(infoBox);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Morpion");
        primaryStage.show();
    }

}
