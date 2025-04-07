package mihan.sossou.crossword.model;

/**
 * Classe DTO (Data Transfer Object) pour représenter les informations
 * d'un mot extraites de la base de données.
 * Utilisée pour passer les données de la couche Database vers la couche Modèle (Crossword).
 */
public class CrosswordBD {
    private int ligne; // Ligne de départ du mot (1-based dans la BD)
    private int colonne; // Colonne de départ du mot (1-based dans la BD)
    private String solution; // Le mot solution
    private boolean horizontal; // Orientation du mot
    private String definition; // L'indice/définition du mot

    /**
     * Constructeur pour les données d'un mot depuis la BD.
     * @param ligne Ligne de départ (1-based).
     * @param colonne Colonne de départ (1-based).
     * @param solution Le mot solution.
     * @param horizontal Orientation (true si horizontal).
     * @param definition L'indice associé.
     */
    public CrosswordBD(int ligne, int colonne, String solution, boolean horizontal, String definition) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.solution = solution;
        this.horizontal = horizontal;
        this.definition = definition;
    }

    // --- Getters ---
    public int getLigne() { return ligne; }
    public int getColonne() { return colonne; }
    public String getSolution() { return solution; }
    public boolean isHorizontal() { return horizontal; }
    public String getDefinition() { return definition; }

    // Pourrait être utile pour le débogage
    @Override
    public String toString() {
        return "CrosswordBD{" +
                "ligne=" + ligne +
                ", colonne=" + colonne +
                ", solution='" + solution + '\'' +
                ", horizontal=" + horizontal +
                ", definition='" + definition + '\'' +
                '}';
    }
}
