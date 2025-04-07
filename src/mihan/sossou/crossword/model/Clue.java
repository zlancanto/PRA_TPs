package mihan.sossou.crossword.model;

/**
 * Représente un indice (définition) pour un mot dans la grille.
 * Contient le texte de l'indice, sa position de départ (ligne, colonne)
 * et son orientation (horizontale ou verticale).
 */
public class Clue {

	private String clue;
	private int row;
	private int column;
	private boolean horizontal;

	/**
	 * Constructeur pour un indice.
	 * @param clue Le texte de l'indice.
	 * @param row La ligne de départ (0-based).
	 * @param column La colonne de départ (0-based).
	 * @param horizontal L'orientation (true pour horizontal).
	 */
	public Clue(String clue, int row, int column, boolean horizontal) {
		this.clue = clue;
		this.row = row;
		this.column = column;
		this.horizontal = horizontal;
	}

	public String getClue() {
		return clue;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public boolean isHorizontal() {
		return horizontal;
	}


	@Override
	public String toString() {
		// Ajoute 1 à row/column pour un affichage 1-based, plus intuitif pour l'utilisateur
		return clue + " ("+ (row + 1) + "," + (column + 1) + ")";
	}

	// Pourrait être utile pour la comparaison dans le contrôleur (ex: mise en évidence)
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Clue clue = (Clue) o;
		return row == clue.row && column == clue.column && horizontal == clue.horizontal && java.util.Objects.equals(clue, clue.clue);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(clue, row, column, horizontal);
	}
}
