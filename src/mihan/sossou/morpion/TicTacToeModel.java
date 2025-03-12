package mihan.sossou.morpion;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToeModel
{

    /**
     * Taille du plateau de jeu (pour être extensible).
     */
    private final static int BOARD_WIDTH = 3;
    private final static int BOARD_HEIGHT = 3;

    /**
     * Nombre de pièces alignés pour gagner (idem).
     */
    private final static int WINNING_COUNT = 3;

    /**
     * Joueur courant.
     */
    private final ObjectProperty<Owner> turn = new SimpleObjectProperty<>(Owner.FIRST);

    /**
     * Vainqueur du jeu, NONE si pas de vainqueur.
     */
    private final ObjectProperty<Owner> winner = new SimpleObjectProperty<>(Owner.NONE);

    /**
     * Plateau de jeu.
     */
    private final ObjectProperty<Owner>[][] board;

    /**
     * Positions gagnantes.
     */
    private final BooleanProperty[][] winningBoard;

    /**
     * Constructeur privé.
     */
    private TicTacToeModel() {
        // Initialisation des tableaux
        board = new SimpleObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
        winningBoard = new BooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];

        // Initialisation du plateau de jeu et des positions gagnantes
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }

    }

    /**
     * @return la seule instance possible du jeu.
     */
    public static TicTacToeModel getInstance() {
        return TicTacToeModelHolder.INSTANCE;
    }

    /**
     * Classe interne selon le pattern singleton.
     */
    private static class TicTacToeModelHolder
    {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }

    public void restart() {
        // Initialisation du plateau de jeu et des positions gagnantes
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j].set(Owner.NONE);
                winningBoard[i][j].set(false);
            }
        }
        turn.set(Owner.FIRST);
        winner.set(Owner.NONE);
    }

    public final ObjectProperty<Owner> turnProperty() {
        return turn;
    }

    public final ObjectProperty<Owner> getSquare(int row, int column) {
        return board[row][column];
    }

    public final BooleanProperty getWinningSquare(int row, int column) {
        return winningBoard[row][column];
    }

    /**
     * Cette fonction ne doit donner le bon résultat que si le jeu est terminé.
     * L’affichage peut être caché avant la fin du jeu.
     *
     * @return résultat du jeu sous forme de texte
     */
    public final StringExpression getEndOfGameMessage() {
        return new StringBinding() {
            {
                super.bind(winner, gameOver());
            }

            @Override
            protected String computeValue() {
                if (!gameOver().get()) {
                    return "";
                }

                String gameOverToString = "Game Over. Le gagnant est le ";

                if (winner.get() == Owner.FIRST) {
                    return gameOverToString + "premier joueur.";
                } else if (winner.get() == Owner.SECOND) {
                    return gameOverToString + "deuxième joueur.";
                } else {
                    return "Match nul !";
                }
            }
        };
    }

    public void setWinner(Owner winner) {
        this.winner.set(winner);
    }

    public boolean validSquare(int row, int column) {

        // Vérifier si les coordonnées sont dans les limites du plateau
        return row >= 0 && row < BOARD_HEIGHT && column >= 0 && column < BOARD_WIDTH;
    }

    public void nextPlayer() {
        turn.set(turn.get().opposite());
    }

    /**
     * Jouer dans la case (row, column) quand c’est possible.
     */
    public void play(int row, int column) {

        // Vérifier si le mouvement est légal
        if (legalMove(row, column).get()) {
            // Mettre à jour la case avec le joueur courant
            board[row][column].set(turn.get());

            // Vérifier si un joueur a gagné après ce coup
            if (checkWinner(row, column)) {
                setWinner(turn.get()); // Définir le vainqueur
            }
            else if (gameOver().get()) {
                setWinner(Owner.NONE); // Si le jeu est terminé sans vainqueur, c'est un match nul
            }
            else {
                // Passer au joueur suivant
                nextPlayer();
            }
        }
    }

    /**
     * @return true s’il est possible de jouer dans la case c’est-à-dire la case est
     * libre et le jeu n’est pas terminé
     */
    public BooleanBinding legalMove(int row, int column) {
        // Vérifier si la case est valide et si elle est vide
        return Bindings.createBooleanBinding(
                () -> validSquare(row, column) && board[row][column].get() == Owner.NONE && !gameOver().get(),
                board[row][column], gameOver()
        );
    }

    public NumberExpression getScore(Owner owner) {
        IntegerProperty score = new SimpleIntegerProperty(0);

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (board[i][j].get() == owner) {
                    score.set(score.get() + 1);
                }
            }
        }

        return score;
    }

    /**
     * @return true si le jeu est terminé
     * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
     */
    public BooleanBinding gameOver() {
        // Convertir le tableau en une liste observable
        List<ObjectProperty<Owner>> observableBoard = new ArrayList<>();
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            observableBoard.addAll(Arrays.asList(board[row]));
        }

        return new BooleanBinding() {
            {
                // Lier le binding à la liste observable
                super.bind(winner);
                super.bind(FXCollections.observableArrayList(observableBoard));
            }

            @Override
            protected boolean computeValue() {
                // Vérifier si un joueur a gagné
                if (winner.get() != Owner.NONE) {
                    return true;
                }

                // Vérifier s'il reste des cases libres
                for (ObjectProperty<Owner> cell : observableBoard) {
                    if (cell.get() == Owner.NONE) {
                        return false;
                    }
                }

                // Si aucune case libre, le jeu est terminé
                return true;
            }
        };
    }

    public boolean checkWinner(int row, int column) {

        return checkWinnerHorizontal(row) || checkWinnerVertical(column) || checkWinnerDiagonal(row, column);
    }

    /**
     * Vérifier la victoire horizontalement
     *
     * @param row
     * @return
     */
    public boolean checkWinnerHorizontal(int row) {
        Owner currentPlayer = turn.get();
        int count = 0;

        for (int c = 0; c < BOARD_WIDTH; c++) {
            if (board[row][c].get() == currentPlayer) {
                count++;
            }
            else {
                count = 0;
            }
			if (count == WINNING_COUNT) {
				return true;
			}
        }

        return false;
    }

    /**
     * Vérifier la victoire verticalement
     *
     * @param column
     * @return
     */
    public boolean checkWinnerVertical(int column) {
        Owner currentPlayer = turn.get();
        int count = 0;

        //
        count = 0;
        for (int r = 0; r < BOARD_HEIGHT; r++) {
            if (board[r][column].get() == currentPlayer) {
                count++;
            }
            else {
                count = 0;
            }
			if (count == WINNING_COUNT) {
				return true;
			}
        }

        return false;
    }

    /**
     * Vérifier la victoire diagonalement
     *
     * @param row
     * @param column
     * @return
     */
    public boolean checkWinnerDiagonal(int row, int column) {
        Owner currentPlayer = turn.get();
        int count = 0;

        // Vérifier diagonale principale
        if (row == column) {
            for (int i = 0; i < Math.min(BOARD_WIDTH, BOARD_HEIGHT); i++) {
                if (board[i][i].get() == currentPlayer) {
                    count++;
                }
                else {
                    count = 0;
                }
				if (count == WINNING_COUNT) {
					return true;
				}
            }
        }

        // Vérifier diagonale secondaire
        count = 0;
        for (int i = 0; i < Math.min(BOARD_WIDTH, BOARD_HEIGHT); i++) {
            if (board[i][BOARD_WIDTH - i - 1].get() == currentPlayer) {
                count++;
            }
            else {
                count = 0;
            }
			if (count == WINNING_COUNT) {
				return true;
			}
        }

        return false;
    }
}
