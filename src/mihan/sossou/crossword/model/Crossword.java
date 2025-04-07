package mihan.sossou.crossword.model;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mihan.sossou.crossword.controller.Database;
import mihan.sossou.crossword.enums.GridSize;

import java.util.List;

public class Crossword extends Grid<CrosswordSquare> {

    private ObservableList<Clue> verticalClues;
    private ObservableList<Clue> horizontalClues;

    private CrosswordSquare currentSquare;
    private Clue verticalClueCurrent;
    private Clue horizontalClueCurrent;
    private int currentRow = -1;
    private int currentColumn = -1;
    private boolean currentDirectionIsHorizontal = true;

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

    // Il y avait problem
    public ObjectProperty<Character> propositionProperty(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire : aucune proposition possible");
        }
        return getCell(row, column).propositionProperty();
    }

    public boolean isBlackSquare(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }
        return getCell(row, column).isBlack();
    }

    public void setBlackSquare(int row, int column, boolean black) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }
        CrosswordSquare square = getCell(row, column);

        // On met la case à jour quand l'état est different
        if (square.isBlack() == black) {
            return;
        }
        square.setBlack(black);

        if (black) {
            square.setSolution(null);
            square.setProposition(null);
            square.setHorizontalDefinition(null);
            square.setVerticalDefinition(null);
        } else {
            if (square.getSolution() == null) square.setSolution(' ');
            if (square.getProposition() == null) square.setProposition(' ');
        }
    }

    public char getSolution(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }

        if (isBlackSquare(row, column)) {
            return '\0';
        }
        Character solution = getCell(row, column).getSolution();
        return (solution != null) ? solution : '\0';
    }

    public void setSolution(int row, int column, char solution) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }

        CrosswordSquare square = getCell(row, column);

        if (square.isBlack()) {
            square.setBlack(false);
        }
        square.setSolution(solution);

        if(square.getProposition() == null) {
            square.setProposition(' ');
        }
    }

    public char getProposition(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }

        if (isBlackSquare(row, column)) {
            return '\0';
        }

        Character proposition = getCell(row, column).getProposition();
        return (proposition != null) ? proposition : ' ';
    }

    public void setProposition(int row, int column, char proposition) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou case sans solution");
        }

        CrosswordSquare square = getCell(row, column);
        square.setProposition(Character.toUpperCase(proposition));
    }

    public String getDefinition(int row, int column, boolean horizontal) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }

        if (isBlackSquare(row, column)) {
            return null;
        }

        CrosswordSquare square = getCell(row, column);

        return horizontal ? square.getHorizontalDefinition() : square.getVerticalDefinition();
    }

    public void setDefinition(int row, int column, boolean horizontal, String definition) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides: (" + row + "," + column + ")");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou case sans solution");
        }

        CrosswordSquare square = getCell(row, column);
        if (horizontal) {
            square.setHorizontalDefinition(definition);
        }
        else {
            square.setVerticalDefinition(definition);
        }
    }

    /**
     * @param database
     * @param puzzleNumber
     * @return une grille de mots croisés
     */
    public static Crossword createPuzzle(Database database, int puzzleNumber) {

        if (database == null) {
            throw new IllegalArgumentException("La BD ne peut être nulle");
        }

        GridSize GRID_SIZE = database.getGridSize(puzzleNumber);
        if (GRID_SIZE == null ||
                GRID_SIZE.getHeight() <= 0 ||
                GRID_SIZE.getWidth() <= 0) {
            throw new RuntimeException("Taille de grille invalide reçue pour le numéro de puzzle : " + puzzleNumber);
        }

        // Création d'une grille vide
        int height = GRID_SIZE.getHeight();
        int width = GRID_SIZE.getWidth();
        Crossword crossword = new Crossword(height, width);

        // Extraire les données de la grille
        List<CrosswordBD> crosswordsBD = database.extractGrid(puzzleNumber);

        // Remplir les cases qui ont des solutions
        if (crosswordsBD == null || crosswordsBD.isEmpty()) {
            throw new RuntimeException("Aucune donnée de mots croisés n'a été trouvée dans la BD pour le numéro de l'énigme.: " + puzzleNumber);
        } else {
            crossword = addPuzzleValues(crossword, crosswordsBD);
        }
        determineBlackSquares(crossword);

        return crossword;
    }

    /**
     * Affiche sur une ligne tous les caractères proposés par le joueur
     */
    public void printProposition() {
        System.out.println("--- Propositions ---");
        for (int row = 0; row < getHeight(); row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < getWidth(); col++) {
                if (isBlackSquare(row, col)) {
                    line.append("*");
                } else {
                    char proposition = getProposition(row, col);
                    // On utilise '_' pour les caractères vides/nuls, sinon la proposition
                    line.append((proposition == '\0' || proposition == ' ') ? "_" : proposition);
                }
                line.append(" ");
            }
            System.out.println(line);
        }
        System.out.println("--------------------");
    }

    /**
     * Prints the solution grid to the console.
     */
    public void printSolution() {
        System.out.println("--- Solution ---");
        for (int row = 0; row < getHeight(); row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < getWidth(); col++) {
                if (isBlackSquare(row, col)) {
                    line.append("*");
                } else {
                    char solution = getSolution(row, col);
                    // On utilise '?' pour les solutions nulles/vides.
                    line.append((solution == '\0') ? "?" : solution);
                }
                line.append(" "); // Add space for better readability
            }
            System.out.println(line);
        }
        System.out.println("----------------");
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

    /**
     * Interroge la grille et marque toute case qui n'a pas reçu de solution, comme un carré noir.
     * @param crossword The crossword grid.
     */
    private static void determineBlackSquares(Crossword crossword) {
        for (int r = 0; r < crossword.getHeight(); r++) {
            for (int c = 0; c < crossword.getWidth(); c++) {
                CrosswordSquare square = crossword.getCell(r, c);
                /*
                * Si une case n'a pas de caractère de solution après avoir traité tous les mots,
                * et qu'elle n'a pas été explicitement rendue non noire, on la marque comme noire.
                * */
                if (square.getSolution() == null || square.getSolution() == '\0') {
                    // Vérifier s'il fait partie d'un placement de mot qui a initialisé la proposition
                    if (square.getProposition() == null) {
                        crossword.setBlackSquare(r, c, true);
                    } else if (square.getProposition() == ' ' && square.getSolution() == null) {
                        crossword.setBlackSquare(r, c, true);
                    }
                } else {
                    // S'il y a une solution, on s'assure qu'elle n'est pas noire.
                    if (square.isBlack()) {
                        crossword.setBlackSquare(r, c, false);
                    }
                    if (square.getProposition() == null) {
                        square.setProposition(' ');
                    }
                }
            }
        }
    }


    /* Getters and Setters */

    public ObservableList<Clue> getVerticalClues() {
        return verticalClues;
    }

    public ObservableList<Clue> getHorizontalClues() {
        return horizontalClues;
    }

    public CrosswordSquare getCurrentSquare() {
        if (correctCoords(currentRow, currentColumn)) {
            return getCell(currentRow, currentColumn);
        }
        return null; // Or handle appropriately
    }

    public void setCurrentSquare(CrosswordSquare currentSquare) {
        this.currentSquare = currentSquare;
    }

    public Clue getVerticalClueCurrent() {
        return verticalClueCurrent;
    }

    public void setVerticalClueCurrent(Clue verticalClueCurrent) {
        this.verticalClueCurrent = verticalClueCurrent;
    }

    public Clue getHorizontalClueCurrent() {
        return horizontalClueCurrent;
    }

    public void setHorizontalClueCurrent(Clue horizontalClueCurrent) {
        this.horizontalClueCurrent = horizontalClueCurrent;
    }

    public int getCurrentRow() { return currentRow; }
    public int getCurrentColumn() { return currentColumn; }
    public boolean isCurrentDirectionHorizontal() { return currentDirectionIsHorizontal; }

    /**
     * Définit la position de la cellule focalisée actuelle.
     * @param row Row index.
     * @param column Column index.
     */
    public void setCurrentPosition(int row, int column) {
        if (correctCoords(row, column) && !isBlackSquare(row, column)) {
            this.currentRow = row;
            this.currentColumn = column;
        } else {
            throw new IllegalArgumentException("Tentative de définir la position actuelle sur un carré invalide ou noir: " + row + "," + column);
        }
    }

    /**
     * Bascule la direction d'entrée actuelle (horizontale/verticale).
     */
    public void toggleCurrentDirection() {
        this.currentDirectionIsHorizontal = !this.currentDirectionIsHorizontal;
    }

    public void setCurrentDirection(boolean horizontal) {
        this.currentDirectionIsHorizontal = horizontal;
    }

    /**
     * Déplacement de la position actuelle en fonction de la direction et du pas.
     * @param horizontal Direction du mouvement.
     * @param step +1 pour le suivant, -1 pour le précédent.
     * @return Vrai si le déplacement a été réussi, faux sinon (par exemple, il a touché la limite ou le carré noir).
     */
    public boolean moveToNextCell(boolean horizontal, int step) {
        int nextRow = currentRow;
        int nextCol = currentColumn;

        do {
            if (horizontal) {
                nextCol += step;
            } else {
                nextRow += step;
            }
            // Vérifier les limites
            if (!correctCoords(nextRow, nextCol)) {
                return false; // Déplacé hors limites
            }
            // Vérifier si la cellule suivante n'est pas noire
            if (!isBlackSquare(nextRow, nextCol)) {
                setCurrentPosition(nextRow, nextCol);
                return true; // A trouvé une cellule suivante valide
            }
            // Poursuivre la boucle si la cellule suivante est noire
        } while (correctCoords(nextRow, nextCol));

        // No valid non-black cell found in this direction
        return false;
    }

}