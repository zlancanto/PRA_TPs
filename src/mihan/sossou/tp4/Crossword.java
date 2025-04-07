package mihan.sossou.tp4;

public class Crossword{
    private boolean horizontal;
    private int ligne;
    private int colonne;
    private String solution;
 
    public Crossword(boolean horizontal, int ligne, int colonne, String solution) {
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
    
    public boolean isHorizontal() { 
    	return horizontal; 
    }

    @Override
    public String toString() {
        return "Crossword{" +
                "ligne=" + ligne +
                ", colonne=" + colonne +
                ", horizontal=" + horizontal +
                ", solution='" + solution + '\'' +
                '}';
    }
}

