package mihan.sossou.crossword.view;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import mihan.sossou.crossword.controller.ClueController;
import mihan.sossou.crossword.model.Clue;
import mihan.sossou.crossword.model.Crossword;

import java.util.Iterator;

public class ClueView extends Label {

    private final Clue clue;
    private boolean isSelected;
    private final Crossword crossword;

    public ClueView(Clue clue, Crossword crossword) {
        this.clue = clue;
        this.crossword = crossword;
        this.isSelected = false;
        setText(clue.getClue());

        setupStyle();
        setupEvents();
    }



    private void setupStyle() {
        getStyleClass().add("clue-view");
    }

    private void setupEvents() {
        setOnMouseClicked(event -> {
            if (!isSelected) {
                select();
            } else {
                deselect();
            }
        });
    }

    public void select() {
        getStyleClass().add("selected1");
        ClueController.updateCurrentCluesAndSquare(clue, crossword);
        isSelected = true;
    }

    public void deselect() {
        getStyleClass().remove("selected");
        isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
