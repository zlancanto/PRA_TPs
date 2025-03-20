package mihan.sossou.crossword.view;

import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class CrosswordView {

    private BorderPane root;
    private GridPane gridPane;
    private ListView<String> horizontalClues;
    private ListView<String> verticalClues;

    public CrosswordView() {
        root = new BorderPane();
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        horizontalClues = new ListView<>();
        verticalClues = new ListView<>();

        root.setCenter(gridPane);
        root.setLeft(verticalClues);
        root.setBottom(horizontalClues);
    }

    public BorderPane getRoot() {
        return root;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public ListView<String> getHorizontalClues() {
        return horizontalClues;
    }

    public ListView<String> getVerticalClues() {
        return verticalClues;
    }
}

