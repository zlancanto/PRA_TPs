package mihan.sossou.crossword.view;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.beans.binding.Bindings;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.util.StringConverter;
import mihan.sossou.crossword.model.CrosswordSquare;

public class CrosswordSquareView extends StackPane {

    private final CrosswordSquare current;
    private CrosswordSquareView left, right, top, bottom;
    private final Label displayLabel;
    private final TextField inputField;
    private Direction currentDirection = Direction.HORIZONTAL;

    public CrosswordSquareView(CrosswordSquare current) {
        this.current = current;
        this.displayLabel = new Label("");
        this.inputField = new TextField();

        setupBindings();
        setupStyle();
        setupEvents();

        // Par défaut, on affiche le label et on cache le champ texte
        getChildren().addAll(displayLabel, inputField);
        inputField.setVisible(false);

        // Appliquer la classe CSS par défaut
        getStyleClass().add("crossword-square");
        if (current.isBlack()) {
            getStyleClass().add("black");
        }
    }

    private void setupBindings() {
        // Liaison entre la proposition et l'affichage du Label
        displayLabel.textProperty()
                .bind(Bindings.createStringBinding(() -> current.getProposition() != null &&
                                current.getProposition() != ' ' ? current.getProposition()
                                .toString() : "",
                        current.propositionProperty()
                ));

        // Liaison entre le champ texte et la proposition
        inputField.textProperty().bindBidirectional(current.propositionProperty(), new StringConverter<>() {
            @Override
            public String toString(Character character) {
                return (character != null && character != ' ') ? character.toString() : "";
            }

            @Override
            public Character fromString(String string) {
                return (string != null && !string.isEmpty()) ? string.charAt(0) : ' ';
            }
        });

        // Style de la case en fonction de son statut (noire ou blanche)
        current.blackProperty().addListener((obs, wasBlack, isBlack) -> {
            if (isBlack) {
                getStyleClass().add("black");
            } else {
                getStyleClass().remove("black");
            }
        });
    }

    private void setupStyle() {
        setMinSize(40, 40);
        setMaxSize(40, 40);
        displayLabel.getStyleClass().add("label");
        inputField.getStyleClass().add("text-field");
    }

    private void setupEvents() {

        // Au clic, basculer entre le label et le champ texte
        setOnMouseClicked(this::handleMouseClicked);

        // Quand on fait une action dans la case
        inputField.setOnAction(this::handleOnAction);

        // Qaund on écrit dans la case
        inputField.textProperty().addListener(this::handleChangeInputField);

        // Quand on se dirige vers une autre case à travers le clavier
        inputField.setOnKeyPressed(this::handleKeyPressed);
    }

    /**
     * Quand on se dirige vers une autre case à travers le clavier
     * @param event
     */
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT -> moveTo(left);
            case RIGHT -> moveTo(right);
            case UP -> moveTo(top);
            case DOWN -> moveTo(bottom);
            case ENTER -> validateSolution();
            case BACK_SPACE -> eraseLetter();
            default -> {
            }
        }
    }

    /**
     * Qaund on écrit dans la case
     * @param obs
     * @param oldText
     * @param newText
     */
    private void handleChangeInputField(ObservableValue<? extends String> obs, String oldText, String newText) {
        if (newText.length() > 1) {
            inputField.setText(oldText);
        } else {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), displayLabel);
            st.setFromX(1);
            st.setToX(1.5);
            st.setFromY(1);
            st.setToY(1.5);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        }
    }

    /**
     * Quand on fait action
     *
     * @param actionEvent
     */
    private void handleOnAction(ActionEvent actionEvent) {
        displayLabel.setVisible(true);
        inputField.setVisible(false);
        getStyleClass().remove("current");
    }

    /**
     * Quand on click sur une case
     *
     * @param mouseEvent
     */
    private void handleMouseClicked(MouseEvent mouseEvent) {
        if (!current.isBlack()) {
            displayLabel.setVisible(false);
            inputField.setVisible(true);
            inputField.requestFocus();
            getStyleClass().add("current");
        }
    }

    private void moveTo(CrosswordSquareView next) {
        if (next != null) {
            // On modifie l'affichage de la case courante
            inputField.setVisible(false);
            displayLabel.setVisible(true);

            // On modifie l'affichage de la case suivante
            next.inputField.setVisible(true);
            next.inputField.requestFocus();
        }
    }

    private void validateSolution() {
        if (current.getProposition().equals(current.getSolution())) {
            getStyleClass().add("correct");
        } else {
            getStyleClass().remove("correct");
        }
    }

    private void eraseLetter() {
        inputField.setText("");
        if (currentDirection == Direction.HORIZONTAL && left != null) {
            moveTo(left);
        } else if (currentDirection == Direction.VERTICAL && top != null) {
            moveTo(top);
        }
    }

    // --- Getters et setters pour les voisins ---

    public CrosswordSquareView getLeft() {
        return left;
    }

    public void setLeft(CrosswordSquareView left) {
        this.left = left;
    }

    public CrosswordSquareView getRight() {
        return right;
    }

    public void setRight(CrosswordSquareView right) {
        this.right = right;
    }

    public CrosswordSquareView getTop() {
        return top;
    }

    public void setTop(CrosswordSquareView top) {
        this.top = top;
    }

    public CrosswordSquareView getBottom() {
        return bottom;
    }

    public void setBottom(CrosswordSquareView bottom) {
        this.bottom = bottom;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    private enum Direction {
        HORIZONTAL, VERTICAL
    }
}