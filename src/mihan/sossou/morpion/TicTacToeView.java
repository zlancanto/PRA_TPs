package mihan.sossou.morpion;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TicTacToeView extends Application {
    private static final int SIZE = 3;
    private TicTacToeModel model = TicTacToeModel.getInstance();

    @Override
    public void start(Stage primaryStage) {
        // Grille de jeu
        GridPane grid = new GridPane();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TicTacToeSquare square = new TicTacToeSquare(row, col);
                grid.add(square, col, row);
            }
        }

        // Bouton "Restart"
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> model.restart());

        // Description du gagnant
        Label winnerLabel = new Label();
        winnerLabel.textProperty().bind(model.getEndOfGameMessage());

        // Score du joueur 1
        Label firstPlayerDescriptionLabel = new Label();
        firstPlayerDescriptionLabel.textProperty().bind(model.playerScoreDescription(Owner.FIRST));

        // Score du joueur 2
        Label secondPlayerDescriptionLabel = new Label();
        secondPlayerDescriptionLabel.textProperty().bind(model.playerScoreDescription(Owner.SECOND));

        // Cases libres
        Label casesLibresDescriptionLabel = new Label();
        casesLibresDescriptionLabel.textProperty().bind(model.casesLibresDescription());

        // Affichages des scores et cases libres sur une ligne
        HBox stateGameDescriptionHBox = new HBox(firstPlayerDescriptionLabel,
                secondPlayerDescriptionLabel,
                casesLibresDescriptionLabel
        );
        stateGameDescriptionHBox.setSpacing(20);

        VBox root = new VBox(10, grid, restartButton, winnerLabel, stateGameDescriptionHBox);
        Scene scene = new Scene(root, 300, 350);

        primaryStage.setTitle("Morpion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
