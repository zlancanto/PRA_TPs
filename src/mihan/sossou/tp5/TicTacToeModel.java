package mihan.sossou.tp5;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Stream;

public class TicTacToeModel {

	/**
	 * Taille du plateau de jeu (pour être extensible).
	 */
	public static final int BOARD_WIDTH = 3;
	public static final int BOARD_HEIGHT = 3;

	/**
     * Nombre de pièces alignés pour gagner (idem).
     */
	public static final int WINNING_COUNT = 3;

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
		board = new SimpleObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
		winningBoard = new BooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];

		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
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
	private static class TicTacToeModelHolder {
		private static final TicTacToeModel INSTANCE = new TicTacToeModel();
	}

	/**
	 * Réinitialise le plateau pour une nouvelle partie.
	 */
	public void restart() {
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				board[i][j].set(Owner.NONE);
				winningBoard[i][j].set(false);
			}
		}
		setWinner(Owner.NONE);
		turn.set(Owner.FIRST);
	}

	/**
	 * @return la propriété du joueur courant (pour le binding dans l'interface).
	 */
	public ObjectProperty<Owner> turnProperty() {
		return turn;
	}

	/**
 	 * @param row
	 * @param column
	 * @return la propriété associée à la case (row, column).
	 */
	public ObjectProperty<Owner> getSquare(int row, int column) {
		if (!validSquare(row, column)) {
			throw new IndexOutOfBoundsException("Cellule [" + row + "][" + column + "] inexistante");
		}
		return board[row][column];
	}

	/**
 	 * @param row
	 * @param column
	 * @return la propriété indiquant si la case (row, column) fait partie de
	 * l'alignement gagnant.
	 */
	public BooleanProperty getWinningSquare(int row, int column) {
		if (!validSquare(row, column)) {
			throw new IndexOutOfBoundsException("Cellule [" + row + "][" + column + "] inexistante");
		}
		return winningBoard[row][column];
	}

	/**
     * Cette fonction ne doit donner le bon résultat que si le jeu est terminé.
     * L’affichage peut être caché avant la fin du jeu.
     *
     * @return résultat du jeu sous forme de texte
     */
	public StringExpression getEndOfGameMessage() {
		return new StringBinding() {
			{
				for (int i = 0; i < BOARD_HEIGHT; i++) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						super.bind(board[i][j]);
					}
				}
				super.bind(winner);
			}

			@Override
			protected String computeValue() {
				if (winner.get() == Owner.FIRST) {
					return "Game over. Le gagnant est le premier joueur.";
				} else if (winner.get() == Owner.SECOND) {
					return "Game over. Le gagnant est le deuxième joueur.";
				} else if (isBoardFull()) {
					return "Match nul.";
				}
				return "";
			}
		};
	}

	/**
	 * Met à jour le gagnant du jeu
	 * @param winner
	 */
	public void setWinner(Owner winner) {
		this.winner.set(winner);
	}

	/**
	 * @param row
	 * @param column
	 * @return si la case est valide ou pas
	 */
	public boolean validSquare(int row, int column) {
		return (row >= 0 && row < BOARD_HEIGHT) && (column >= 0 && column < BOARD_WIDTH);
	}

	/**
	 * Passe au joueur suivant
	 */
	public void nextPlayer() {
		turn.set(turnProperty().get().opposite());
	}

	/**
	 * Jouer dans la case (row, column) quand c’est possible.
	 * @param row
	 * @param column
	 */
	public void play(int row, int column) {
		// On ne peut jouer que si la case est vide et que le jeu n'est pas terminé.
		if (legalMove(row, column).get()) {
			board[row][column].set(turnProperty().get());
			// Vérifier la victoire après le coup.
			if (checkVictory(row, column)) {
				setWinner(turnProperty().get());
				turn.set(Owner.NONE);
			} else if (isBoardFull()) {
				// Match nul : aucun gagnant (on laisse winner à NONE pour signaler cela).
				setWinner(Owner.NONE);
			} else {
				nextPlayer();
			}
		}
	}

	/**
	* @return true s’il est possible de jouer dans la case c’est-à-dire la case est
	* libre et le jeu n’est pas terminé
	*/
	public BooleanBinding legalMove(int row, int column) {
		return new BooleanBinding() {
			{
				super.bind(board[row][column], winner);
			}

			@Override
			protected boolean computeValue() {
				return validSquare(row,column) && board[row][column].get() == Owner.NONE && winner.get() == Owner.NONE;
			}
		};
	}

	/**
 	 * @param owner
	 * @return une NumberBinding représentant le nombre de cases occupées par le
	 * joueur spécifié.
	 */
	public NumberExpression getScore(Owner owner) {

		int score = 0;

		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (board[i][j].get() == owner) {
					score++;
				}
			}
		}

		return new SimpleIntegerProperty(score);
	}

	/**
     * @return true si le jeu est terminé
     * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
     */
	public BooleanBinding gameOver() {
		return new BooleanBinding() {
			{
				for (int i = 0; i < BOARD_HEIGHT; i++) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						super.bind(board[i][j]);
					}
				}
				super.bind(winner);
			}

			@Override
			protected boolean computeValue() {
				return winner.get() != Owner.NONE || isBoardFull();
			}
		};
	}

	/**
	 *
	 * @param owner
	 * @return le nombre de cases occupées par un joueur
	 */
	public StringExpression playerScoreDescription(Owner owner) {
		return new StringBinding() {
			{
				for (int i = 0; i < BOARD_HEIGHT; i++) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						super.bind(board[i][j]);
					}
				}
			}

			@Override
			protected String computeValue() {
				int score = getScore(owner).intValue();
				return score + " case" + ((score == 1 || score == 0) ? " " : "s ") + "pour " + (owner == Owner.FIRST ? "X" : "O");
			}
		};
	}

	/**
 	 * @return vrai si la grille est pleine.
	 */
	private boolean isBoardFull() {
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (board[i][j].get() == Owner.NONE) {
					return false;
				}
			}
		}
		return true;
	}

	/**
 	 * @param row
	 * @param col
	 * @return true si le coup joué à (row, col) a permis de gagner la partie.
	 */
	private boolean checkVictory(int row, int col) {
	    Owner player = board[row][col].get();
	    boolean hasWon = false;

	    // Vérifier chaque direction indépendemment
	    if (checkDirection(row, col, 1, 0, player)) { // Ligne
	        hasWon = true;
	    }
	    if (checkDirection(row, col, 0, 1, player)) { // Colonne
	        hasWon = true;
	    }
	    if (checkDirection(row, col, 1, 1, player)) { // Diagonale principale
	        hasWon = true;
	    }
	    if (checkDirection(row, col, 1, -1, player)) { // Diagonale secondaire
	        hasWon = true;
	    }

		// Renvoie true si au moins une combinaison gagnante a été trouvée
	    return hasWon;
	}

	/**
	 * Vérifie dans une direction donnée (dRow, dCol)
	 * si on a WINNING_COUNT cases alignées.
	 * @param row
	 * @param col
	 * @param dRow
	 * @param dCol
	 * @param player
	 * @return true si on a @WINNING_COUNT cases alignées.
	 */
	private boolean checkDirection(int row, int col, int dRow, int dCol, Owner player) {
		int count = 1;
		// Direction positive
		int r = row + dRow;
		int c = col + dCol;
		while (validSquare(r, c) && board[r][c].get() == player) {
			count++;
			r += dRow;
			c += dCol;
		}
		// Direction négative
		r = row - dRow;
		c = col - dCol;
		while (validSquare(r, c) && board[r][c].get() == player) {
			count++;
			r -= dRow;
			c -= dCol;
		}
		if (count >= WINNING_COUNT) {
			highlightWinningCells(row, col, dRow, dCol, player);
			return true;
		}
		return false;
	}

	/**
	 * Met en évidence les cases faisant partie de l'alignement gagnant.
	 * @param row
	 * @param col
	 * @param dRow
	 * @param dCol
	 * @param player
	 */
	private void highlightWinningCells(int row, int col, int dRow, int dCol, Owner player) {
		// Marquer la direction positive
		int r = row;
		int c = col;
		while (validSquare(r, c) && board[r][c].get() == player) {
			winningBoard[r][c].set(true);
			r += dRow;
			c += dCol;
		}
		// Marquer la direction négative
		r = row - dRow;
		c = col - dCol;
		while (validSquare(r, c) && board[r][c].get() == player) {
			winningBoard[r][c].set(true);
			r -= dRow;
			c -= dCol;
		}
	}

	public StringExpression casesLibresDescription() {
		return new StringBinding() {
			{
				for (int i = 0; i < BOARD_HEIGHT; i++) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						super.bind(board[i][j]);
					}
				}
			}

			@Override
			protected String computeValue() {
				int caseLibres = 0;
				for (int i = 0; i < BOARD_HEIGHT; i++) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						if (board[i][j].get() == Owner.NONE) {
							caseLibres++;
						}
					}
				}
				return caseLibres + " case" + ((caseLibres == 1 || caseLibres == 0) ? " libre" : "s libres");
			}
		};
	}
}
