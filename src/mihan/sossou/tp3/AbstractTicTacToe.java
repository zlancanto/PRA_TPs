package mihan.sossou.tp3;

public interface AbstractTicTacToe {

	int BOARD_WIDTH = 3;
	int BOARD_HEIGHT = 3;
	int WINNING_COUNT = 3;

	/**
	 * Recommencer le jeu.
	 */
	void restart();

	/**
	 * @return joueur courant
	 */
	Owner getTurn();

	/**
	 * @param row    numéro de ligne
	 * @param column numéro de colonne
	 * @return valeur de la case (row, column)
	 */
	Owner getSquare(int row, int column);

	/**
	 * @return gagnant du jeu
	 */
	Owner getWinner();

	/**
	 * @param row    numéro de ligne
	 * @param column numéro de colonne
	 * @return true si les paramètres pointent sur une case du plateau de jeu
	 */
	boolean validSquare(int row, int column);

	/**
	 * changer le joueur courant
	 */
	void nextPlayer();

	/**
	 * Jouer dans la case (row, column) quand c'est possible.
	 *
	 * @param column numéro de colonne
	 * @param row    numéro de ligne
	 */
	void play(int row, int column);

	/**
	 * @param row    numéro de ligne
	 * @param column numéro de colonne
	 * @return true s'il est possible de jouer dans la case (row, column)
	 *         c'est-à-dire la case est libre et le jeu n'est pas terminé
	 */
	boolean legalMove(int row, int column);

	/**
	 * @return nombre de coups joués depuis le début du jeu
	 */
	int numberOfRounds();

	/**
	 * @return true si le jeu est terminé (soit un joueur a gagné, soit il n'y a
	 *         plus de cases à jouer)
	 */
	boolean gameOver();

}
