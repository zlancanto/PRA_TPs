package mihan.sossou.tp5;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Stream;

public class TicTacToeModel {

	// Dimensions du plateau et nombre de cases alignées pour gagner.
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
		board = new ObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
		winningBoard = new BooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];

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
	private static class TicTacToeModelHolder {
		private static final TicTacToeModel INSTANCE = new TicTacToeModel();
	}

	// Réinitialise le plateau pour une nouvelle partie.
	public void restart() {
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				board[i][j].set(Owner.NONE);
				winningBoard[i][j].set(false);
			}
		}
		setWinner(Owner.NONE);
		turn.set(Owner.FIRST);
	}

	// Renvoie la propriété du joueur courant (pour le binding dans l'interface).
	public ObjectProperty<Owner> turnProperty() {
		return turn;
	}

	// Renvoie la propriété associée à la case (row, column).
	public ObjectProperty<Owner> getSquare(int row, int column) {
		if (!validSquare(row, column)) {
			throw new IndexOutOfBoundsException("Coordonnées invalides.");
		}
		return board[row][column];
	}
	
	// Renvoie la propriété indiquant si la case (row, column) fait partie de
	// l'alignement gagnant.
	public BooleanProperty getWinningSquare(int row, int column) {
		if (!validSquare(row, column)) {
			throw new IndexOutOfBoundsException("Coordonnées invalides.");
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
		return Bindings.createStringBinding(() -> {
			if (winner.get() == Owner.FIRST) {
				return "Game over. Le gagnant est le premier joueur.";
			} else if (winner.get() == Owner.SECOND) {
				return "Game over. Le gagnant est le deuxième joueur.";
			} else if (isBoardFull()) {
				return "Match nul.";
			}
			return "";
		}, Stream.concat(Stream.of(winner), Stream.of(getBoardProperty())).toArray(Observable[]::new));
	}
	
	public void setWinner(Owner winner) {
		this.winner.set(winner);
	}

	public boolean validSquare(int row, int col) {
		return row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH;
	}
	
	public void nextPlayer() {
		turn.set(turn.get().opposite());
	}
	
	// Méthode qui permet de jouer un coup à la position (row, column).
	public void play(int row, int column) {
		// On ne peut jouer que si la case est vide et que le jeu n'est pas terminé.
		if (legalMove(row, column).get()) {
			board[row][column].set(turn.get());
			// Vérifier la victoire après le coup.
			if (checkVictory(row, column)) {
				setWinner(turn.get());
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
	
	// Retourne une NumberBinding représentant le nombre de cases occupées par le
	// joueur spécifié.
	public NumberExpression getScore(Owner owner) {
		// Rassembler toutes les dépendances (chaque case du plateau)
		ObservableList<ObservableValue<?>> dependencies = FXCollections.observableArrayList();
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				dependencies.add(board[i][j]);
			}
		}
		// Créer un IntegerBinding qui calcule le score
		IntegerBinding scoreBinding = Bindings.createIntegerBinding(() -> {
			int score = 0;
			for (int i = 0; i < BOARD_WIDTH; i++) {
				for (int j = 0; j < BOARD_HEIGHT; j++) {
					if (board[i][j].get() == owner) {
						score++;
					}
				}
			}
			return score;
		}, dependencies.toArray(new ObservableValue<?>[0]));
		return scoreBinding;
	}
	
	  /**
     * @return true si le jeu est terminé
     * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
     */
	public BooleanBinding gameOver() {
		return new BooleanBinding() {
			{
				for (int i = 0; i < BOARD_WIDTH; i++) {
					for (int j = 0; j < BOARD_HEIGHT; j++) {
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

	// Renvoie vrai si la grille est pleine.
	public boolean isBoardFull() {
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				if (board[i][j].get() == Owner.NONE) {
					return false;
				}
			}
		}
		return true;
	}

	public Observable[] getBoardProperty() {
		Observable[] properties = new Observable[BOARD_WIDTH * BOARD_HEIGHT];
		int index = 0;

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				properties[index++] = getSquare(i, j);
			}
		}
		return properties;
	}

	// Vérifie si le coup joué à (row, col) a permis de gagner la partie.
	private boolean checkVictory(int row, int col) {
	    Owner player = board[row][col].get();
	    boolean hasWon = false;
	
	    // ✅ Vérifier chaque direction indépendamment
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
	
	    return hasWon; // ✅ Renvoie true si au moins une combinaison gagnante a été trouvée
	}


	// Vérifie dans une direction donnée (dRow, dCol) si on a WINNING_COUNT cases
	// alignées.
	private boolean checkDirection(int row, int col, int dRow, int dCol, Owner player) {
		int count = 1;
		// Direction positive
		int r = row + dRow, c = col + dCol;
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

	// Met en évidence les cases faisant partie de l'alignement gagnant.
	private void highlightWinningCells(int row, int col, int dRow, int dCol, Owner player) {
		// Marquer la direction positive
		int r = row, c = col;
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



}
