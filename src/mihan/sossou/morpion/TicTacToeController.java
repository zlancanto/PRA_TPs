package mihan.sossou.morpion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TicTacToeController {
    @FXML private GridPane grid;
    @FXML private Label playerTurnLabel;
    @FXML private Label winnerLabel;
    @FXML private Button restartButton;

    private TicTacToeModel model;

    public void initialize() {
        model = TicTacToeModel.getInstance();
        playerTurnLabel.textProperty().bind(model.turnProperty().asString());
        winnerLabel.textProperty().bind(model.gameOver().asString());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                TicTacToeSquare square = new TicTacToeSquare(row, col);
                grid.add(square, col, row);
            }
        }
    }

    @FXML
    private void handleRestart() {
        model.restart();
    }
}
