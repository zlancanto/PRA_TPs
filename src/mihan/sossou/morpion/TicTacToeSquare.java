package mihan.sossou.morpion;

import javafx.beans.property.*;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class TicTacToeSquare extends TextField {
    private static TicTacToeModel model = TicTacToeModel.getInstance();
    private final int row;
    private final int column;
    private ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);
    private BooleanProperty winnerProperty = new SimpleBooleanProperty(false);

    public TicTacToeSquare(final int row, final int column) {
        this.row = row;
        this.column = column;
        setFont(new Font(24));
        setEditable(false);
        setPrefSize(50, 50);

        // Bind le texte à l'état de la case
        textProperty().bind(ownerProperty.asString());

        // Gestion des événements de la souris
        setOnMouseClicked(this::handleClick);
        setOnMouseEntered(this::handleMouseEnter);
        setOnMouseExited(this::handleMouseExit);

        // Mise à jour de la couleur en fonction du propriétaire
        ownerProperty.addListener((obs, oldVal, newVal) -> updateStyle());
        winnerProperty.addListener((obs, oldVal, newVal) -> updateStyle());
    }

    public ObjectProperty<Owner> ownerProperty() {
        return ownerProperty;
    }

    public BooleanProperty winnerProperty() {
        return winnerProperty;
    }

    private void handleClick(MouseEvent event) {
        if (model.validSquare(row, column)) {
            model.play(row, column);
        }
    }

    private void handleMouseEnter(MouseEvent event) {
        if (model.validSquare(row, column)) {
            setStyle("-fx-background-color: lightgreen;");
        } else {
            setStyle("-fx-background-color: lightcoral;");
        }
    }

    private void handleMouseExit(MouseEvent event) {
        updateStyle();
    }

    private void updateStyle() {
        if (winnerProperty.get()) {
            setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        } else if (ownerProperty.get() == Owner.FIRST) {
            setStyle("-fx-background-color: lightblue;");
        } else if (ownerProperty.get() == Owner.SECOND) {
            setStyle("-fx-background-color: lightcoral;");
        } else {
            setStyle("");
        }
    }
}
