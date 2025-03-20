package mihan.sossou.crossword.model;

public class CrosswordBD {
    private int ligne;
    private int colonne;
    private String solution;
    private boolean horizontal;
    private String definition;

    public CrosswordBD(int ligne, int colonne, String solution, boolean horizontal, String definition) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.solution = solution;
        this.horizontal = horizontal;
        this.definition = definition;
    }

    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public String getSolution() {
        return solution;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
