package mihan.sossou.crossword.controller;

import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import mihan.sossou.crossword.model.Crossword;
import mihan.sossou.crossword.view.CrosswordView;

public class CrosswordController {

    private Crossword crossword;
    private CrosswordView view;
    private int currentRow = 0;
    private int currentCol = 0;

    public CrosswordController(Crossword crossword, CrosswordView view) {
        this.crossword = crossword;
        this.view = view;
        initializeGrid();
    }

    private void initializeGrid() {
        GridPane gridPane = view.getGridPane();
        for (int row = 0; row < crossword.getHeight(); row++) {
            for (int col = 0; col < crossword.getWidth(); col++) {
                Label cell = new Label(" ");
                cell.setStyle("-fx-border-color: black; -fx-padding: 10;");
                int finalRow = row, finalCol = col;
                cell.setOnMouseClicked(e -> setCurrentCell(finalRow, finalCol));
                gridPane.add(cell, col, row);
            }
        }
    }

    private void setCurrentCell(int row, int col) {
        currentRow = row;
        currentCol = col;
        System.out.println("Case sélectionnée : " + row + ", " + col);
    }

    public void handleKeyPress(KeyEvent event) {
        char input = event.getText().toUpperCase().charAt(0);
        if (Character.isLetter(input)) {
            crossword.setProposition(currentRow, currentCol, input);
            updateGrid();
        }
    }

    private void updateGrid() {
        GridPane gridPane = view.getGridPane();
        Label label = (Label) gridPane.getChildren().get(currentRow * crossword.getWidth() + currentCol);
        label.setText(String.valueOf(crossword.getProposition(currentRow, currentCol)));
    }
}

