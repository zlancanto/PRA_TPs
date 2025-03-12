package mihan.sossou.morpion;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TicTacToeView extends Application {
    private static final int SIZE = 3;
    private TicTacToeModel model = TicTacToeModel.getInstance();

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TicTacToeSquare square = new TicTacToeSquare(row, col);
                grid.add(square, col, row);
            }
        }

        Label playerTurnLabel = new Label();
        playerTurnLabel.textProperty().bind(model.turnProperty().asString());

        Label winnerLabel = new Label();
        winnerLabel.textProperty().bind(model.gameOver().asString());

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> model.restart());

        VBox root = new VBox(10, playerTurnLabel, grid, winnerLabel, restartButton);
        Scene scene = new Scene(root, 300, 350);

        primaryStage.setTitle("Morpion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
