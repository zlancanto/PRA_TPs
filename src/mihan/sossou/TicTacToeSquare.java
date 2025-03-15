package mihan.sossou.tp5;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class TicTacToeSquare extends TextField {

    private final int row;
    private final int column;
    private final TicTacToeModel model;

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
        setFont(new Font(24));
        setStyle("-fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px; -fx-background-color: transparent;");

        //Affichage dynamique de X et O
        textProperty().bind(Bindings.createStringBinding(() -> {
            Owner owner = model.getSquare(row, column).get();
            if (owner == Owner.FIRST) {
                return "X";
            } else if (owner == Owner.SECOND) {
                return "O";
            } else {
                return "";
            }
        }, model.getSquare(row, column)));
        
        //Agrandir la police uniquement si cette case fait partie de la combinaison gagnante
        model.getWinningSquare(row, column).addListener((obs, oldValue, isWinningSquare) -> {
            if (isWinningSquare) {
                setFont(new Font(40)); // Police plus grande
            }else {
                resetStyle(); //Réinitialiser le style lors du restart()
            }
        });
        
        //Écouteurs pour la souris (click, entered, exited) – voir code précédent
        setOnMouseClicked(e -> model.play(row, column));

        setOnMouseEntered(e -> {
            if (model.gameOver().get()) {
                setStyle("-fx-background-color: red; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
            } else {
                BooleanBinding legal = model.legalMove(row, column);
                if (legal.get()) {
                    setStyle("-fx-background-color: lightgreen; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
                } else {
                    setStyle("-fx-background-color: red; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
                }
            }
        });

        setOnMouseExited(e -> {
            Owner cellOwner = model.getSquare(row, column).get();
            if (cellOwner != Owner.NONE) {
                // On affiche un fond fixe selon le propriétaire.
                if (cellOwner == model.turnProperty().get()) {
                    setStyle("-fx-background-color: transparent; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
                } else {
                    setStyle("-fx-background-color: transparent; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
                }
            } else {
                setStyle("-fx-background-color: transparent; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
            }
        });
    }
    
    //Réinitialiser le style de la case
    private void resetStyle() {
        setFont(new Font(24));
        setStyle("-fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px; -fx-background-color: transparent;");
    }

    //Gestion de la couleur lors du survol
    private void handleMouseEntered(MouseEvent event) {
        if (model.gameOver().get()) {
            setStyle("-fx-background-color: red; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
        } else {
            boolean isLegalMove = model.legalMove(row, column).get();
            setStyle(isLegalMove
                    ? "-fx-background-color: lightgreen; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;"
                    : "-fx-background-color: red; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1px;");
        }
    }

    //Réinitialiser la couleur après le survol
    private void handleMouseExited(MouseEvent event) {
        resetStyle(); //On retourne au style normal après le survol
    }
}
