package mihan.sossou.morpion;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class TicTacToeSquare extends TextField {

    private final int row;
    private final int column;
    private final TicTacToeModel model;
    private final static String INITIAL_STYLE = "-fx-alignment: center;" +
                                                        " -fx-border-color: black;" +
                                                        " -fx-border-width: 1px; " +
                                                        "-fx-background-color: transparent;";
    private final static String HOVER_STYLE_RED = "-fx-alignment: center;" +
                                                          " -fx-border-color: black;" +
                                                          " -fx-border-width: 1px; " +
                                                          "-fx-background-color: red;";
    private final static String HOVER_STYLE_LIGHTGREEN = "-fx-alignment: center;" +
                                                                 " -fx-border-color: black;" +
                                                                 " -fx-border-width: 1px; " +
                                                                 "-fx-background-color: lightgreen;";

    public TicTacToeSquare(int row, int column) {
        super();
        this.row = row;
        this.column = column;
        this.model = TicTacToeModel.getInstance();

        //Désactiver l'édition et le focus pour ne pas afficher le caret clignotant
        setEditable(false);
        setFocusTraversable(false);

        //Configuration de la taille, police, et style de base
        setPrefSize(100, 100);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        setStyle(INITIAL_STYLE);

        //Affichage dynamique de X et O
        textProperty().bind(asString(row, column));

        /**
         * Écouteurs de changement de taille de fenêtre
         * pour adapter la taille de la police
         */
        widthProperty().addListener((obs, oldWidth, newWidth) -> adjustFontSize(3));
        heightProperty().addListener((obs, oldHeight, newHeight) -> adjustFontSize(3));

        //Agrandir la police uniquement si cette case fait partie de la combinaison gagnante
        model.getWinningSquare(row, column).addListener((obs, oldValue, isWinningSquare) -> {
            if (isWinningSquare) {
                // Police plus grande
                adjustFontSize(2);
            }
            else {
                //Réinitialiser le style lors du restart()
                resetStyle();
            }
        });

        /**
         *  Écouteurs de souris (click, entered, exited)
         */
        setOnMouseClicked(e -> {
            model.play(row, column);
            setStyleWhenMouseEntered();
        });
        setOnMouseEntered(e -> setStyleWhenMouseEntered());
        setOnMouseExited(e -> setStyleWhenMouseExited());
    }

    //Réinitialiser le style de la case
    private void resetStyle() {
        setStyle(INITIAL_STYLE);
        adjustFontSize(3);
    }

    /**
     * Style lors du survol
     */
    private void setStyleWhenMouseEntered() {
        BooleanBinding legal = model.legalMove(row, column);
        // S'il est possible de jouer dans la case
        if (legal.get()) {
            setStyle(HOVER_STYLE_LIGHTGREEN);
        }
        else {
            setStyle(HOVER_STYLE_RED);
        }
    }

    /**
     * Style quand on arrête de survoler la case
     */
    private void setStyleWhenMouseExited() {
        setStyle(INITIAL_STYLE);
    }

    /**
     * Ajuster la taille de la police en fonction de la taille de la case
      * @param divisionFactor
     */
    private void adjustFontSize(int divisionFactor) {
        double fontSize = Math.min(getWidth(), getHeight()) / divisionFactor;
        setFont(new Font(fontSize));
    }

    /**
     * @param row
     * @param column
     * @return affichage de la grille en String
     */
    private StringExpression asString(int row, int column) {
        return new StringBinding() {
            {
                super.bind(model.getSquare(row, column));
            }

            @Override
            protected String computeValue() {
                Owner owner = model.getSquare(row, column).get();
                if (owner == Owner.FIRST) {
                    return "X";
                }
                else if (owner == Owner.SECOND) {
                    return "O";
                }
                else {
                    return "";
                }
            }
        };
    }
}
