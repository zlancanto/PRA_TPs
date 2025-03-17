package mihan.sossou.crossword;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Crossword extends Grid<CrosswordSquare> {

	private ObservableList<Clue> verticalClues;
	private ObservableList<Clue> horizontalClues;

	private Crossword(int height, int width) {
		// Appel du constructeur de Grid
		super(height, width);

		this.verticalClues = FXCollections.observableArrayList();
		this.horizontalClues = FXCollections.observableArrayList();

		// Initialisation de la grille avec des cases blanches (non noires)
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				CrosswordSquare square = new CrosswordSquare();
				this.setCell(row, col, square);
			}
		}
	}

	public StringProperty propositionProperty(int row, int column) {
	}

	public boolean isBlackSquare(int row, int column) {
	}

	public void setBlackSquare(int row, int column, boolean black) {
	}

	public char getSolution(int row, int column) {
	}

	public void setSolution(int row, int column, char solution) {
	}

	public char getProposition(int row, int column) {
	}

	public void setProposition(int row, int column, char proposition) {
	}

	public String getDefinition(int row, int column, boolean horizontal) {
	}

	public void setDefinition(int row, int column, boolean horizontal, String definition) {
	}

	public ObservableList<Clue> getVerticalClues() {
	}

	public ObservableList<Clue> getHorizontalClues() {
	}

	public static Crossword createPuzzle(Database database, int puzzleNumber) {
	}

	public void printProposition() {
	}

	public void printSolution() {
	}
}