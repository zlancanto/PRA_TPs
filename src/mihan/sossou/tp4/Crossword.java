package mihan.sossou.tp4;

public class Crossword {
    private String definition;
    private int horizontal;
    private int ligne;
    private int colonne;
    private String solution;

    public Crossword(String definition, int horizontal, int ligne, int colonne, String solution) {
        this.definition = definition;
        this.horizontal = horizontal;
        this.ligne = ligne;
        this.colonne = colonne;
        this.solution = solution;
    }
    
    public int getLigne() {
    	return ligne;
    }
    
    public String getSolution() {
    	return solution;
    }
    
    public int getColonne() {
    	return colonne;
    }
    
    public int getHorizontal() {
        return horizontal;
    }

	public String toString() {
        return definition + " (" + horizontal +")" + solution + " @ (" + ligne + "," + colonne + ")";
    }
}