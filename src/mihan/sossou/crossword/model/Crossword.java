package mihan.sossou.crossword.model;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mihan.sossou.crossword.controller.Database;

import java.util.List;

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
        return null;
    }

    public boolean isBlackSquare(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        return getCell(row, column).isBlack();
    }

    public void setBlackSquare(int row, int column, boolean black) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        CrosswordSquare square = getCell(row, column);
        if (!square.isBlack() && black) {
            square.setSolution(null);
            square.setProposition(null);
            square.setHorizontalDefinition(null);
            square.setVerticalDefinition(null);
            square.setBlack(true);
        }
        else if (square.isBlack() && !black) {
            square.setSolution(' ');
            square.setProposition(' ');
            square.setBlack(false);
        }
    }

    public char getSolution(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        return getCell(row, column).getSolution();
    }

    public void setSolution(int row, int column, char solution) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        CrosswordSquare square = getCell(row, column);
        square.setSolution(solution);
        if (square.isBlack()) {
            square.setBlack(false);
        }
    }

    public char getProposition(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        CrosswordSquare square = getCell(row, column);

        return square.getProposition();
    }

    public void setProposition(int row, int column, char proposition) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        CrosswordSquare square = getCell(row, column);
        square.setProposition(proposition);
    }

    public String getDefinition(int row, int column, boolean horizontal) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        CrosswordSquare square = getCell(row, column);

        if (horizontal) {
            return square.getHorizontalDefinition();
        }
        else {
            return square.getVerticalDefinition();
        }
    }

    public void setDefinition(int row, int column, boolean horizontal, String definition) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        CrosswordSquare square = getCell(row, column);
        if (horizontal) {
            square.setHorizontalDefinition(definition);
        }
        else {
            square.setVerticalDefinition(definition);
        }
    }

    public ObservableList<Clue> getVerticalClues() {
        return verticalClues;
    }

    public ObservableList<Clue> getHorizontalClues() {
        return horizontalClues;
    }

    public static Crossword createPuzzle(Database database, int puzzleNumber) {

        GridSize GRID_SIZE = database.getGridSize(puzzleNumber);

        if (GRID_SIZE == GridSize.SIZE) {
            throw new RuntimeException("Impossible de créer un puzzle de taille zéro");
        }

        // Création d'une grille vide
        int height = GRID_SIZE.getHeight();
        int width = GRID_SIZE.getWidth();
        Crossword crossword = new Crossword(height, width);

        // Extraire les données de la grille
        List<CrosswordBD> crosswordsBD = database.extractGrid(puzzleNumber);

        // Remplir les cases qui ont des solutions
        crossword = addPuzzleValues(crossword, crosswordsBD);

        return crossword;
    }

    /**
     * Affiche sur une ligne tous les caractères proposés par le joueur
     */
    public void printProposition() {

        for (int row = 0; row < getHeight(); row++) {

            StringBuilder propositions = new StringBuilder();

            for (int col = 0; col < getWidth(); col++) {

                if (isBlackSquare(row, col)) {
                    // Afficher '*' en cas de case noire
                    propositions.append("*");
                }
                else {
                    char proposition = getProposition(row, col);
                    // Afficher '_' en cas de case non rempli
                    propositions.append(proposition != '\0' ? proposition : "_");
                }
            }

            // Afficher la ligne formatée
            System.out.println(propositions.toString().toUpperCase());
        }
    }

    public void printSolution() {

        for (int row = 0; row < getHeight(); row++) {

            StringBuilder solutions = new StringBuilder();

            for (int col = 0; col < getWidth(); col++) {
                // Case noire : afficher '*'
                if (isBlackSquare(row, col)) {
                    solutions.append("*");
                }
                // Case avec solution : afficher la lettre
                else {
                    char solution = getSolution(row, col);
                    solutions.append(solution);
                }
            }

            // Afficher la ligne formatée
            System.out.println(solutions.toString().trim());
        }
    }

    /**
     * @param crossword
     * @param crosswordsBD
     * @return le crossword du puzzle rempli avec toutes les valeurs de la BD
     */
    private static Crossword addPuzzleValues(Crossword crossword, List<CrosswordBD> crosswordsBD) {

        for (CrosswordBD crosswordBD : crosswordsBD) {
            // -1 car dans la BD les cases sont numérotées à partir de 1
            int row = crosswordBD.getLigne() - 1;
            int column = crosswordBD.getColonne() - 1;
            String solution = crosswordBD.getSolution();
            boolean horizontal = crosswordBD.isHorizontal();
            String definition = crosswordBD.getDefinition();

            // Récupération de la case (row, column)
            CrosswordSquare crosswordSquare = crossword.getCell(row, column);

            if (horizontal) {
                // Mise à jour de la définition horizontale
                crosswordSquare.setHorizontalDefinition(definition);
                crossword.setCell(row, column, crosswordSquare);

                // Création de l'objet clue associé à la case
                Clue clue = new Clue(definition, row, column, true);
                crossword.horizontalClues.add(clue);

                // Mise à jour de l'état et de la solution de la case
                for (int j = 0; j < solution.length(); j++) {
                    crossword.setBlackSquare(row, column + j, false);
                    crossword.setSolution(row, column + j, solution.charAt(j));
                }
            }
            else {
                // Mise à jour de la définition verticale
                crosswordSquare.setVerticalDefinition(definition);
                crossword.setCell(row, column, crosswordSquare);

                // Création de l'objet clue associé à la case
                Clue clue = new Clue(definition, row, column, false);
                crossword.verticalClues.add(clue);

                // Mise à jour de l'état et de la solution de la case
                for (int i = 0; i < solution.length(); i++) {
                    crossword.setBlackSquare(row + i, column, false);
                    crossword.setSolution(row + i, column, solution.charAt(i));
                }
            }
        }

        return crossword;
    }
}