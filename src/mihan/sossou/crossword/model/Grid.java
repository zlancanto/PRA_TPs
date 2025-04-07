package mihan.sossou.crossword.model;

/**
 * Classe générique représentant une grille 2D.
 * Utilisée comme classe de base pour Crossword.
 * @param <T> Le type d'objet contenu dans chaque cellule de la grille.
 */
public class Grid<T> {
    private int height; // Hauteur de la grille
    private int width; // Largeur de la grille
    private T[][] array; // Tableau 2D pour stocker les cellules

    /**
     * Constructeur de la grille.
     * @param height Hauteur de la grille (doit être >= 0).
     * @param width Largeur de la grille (doit être >= 0).
     * @throws IllegalArgumentException si hauteur ou largeur sont négatives.
     */
    @SuppressWarnings("unchecked") // Nécessaire pour le casting générique du tableau
    public Grid(int height, int width) {
        if (height < 0 || width < 0) {
            throw new IllegalArgumentException("Les dimensions doivent être positives");
        }
        this.height = height;
        this.width = width;
        // Crée un tableau d'Object et le caste (suppression d'avertissement nécessaire)
        this.array = (T[][]) new Object[height][width];
    }

    /**
     * Retourne la hauteur de la grille.
     * @return La hauteur.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retourne la largeur de la grille.
     * @return La largeur.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Vérifie si les coordonnées fournies sont valides (dans les limites de la grille).
     * @param row L'indice de ligne (0-based).
     * @param column L'indice de colonne (0-based).
     * @return true si les coordonnées sont valides, false sinon.
     */
    public boolean correctCoords(int row, int column) {
        return row >= 0 && row < height && column >= 0 && column < width;
    }

    /**
     * Retourne la valeur de la cellule aux coordonnées spécifiées.
     * @param row L'indice de ligne (0-based).
     * @param column L'indice de colonne (0-based).
     * @return La valeur de la cellule.
     * @throws IllegalArgumentException si les coordonnées sont invalides.
     */
    public T getCell(int row, int column) {
        if (!correctCoords(row, column)) {
            // Utilisation d'une exception plus spécifique pourrait être envisagée (IndexOutOfBoundsException)
            throw new IllegalArgumentException("Coordonnées invalides : (" + row + "," + column + ")");
        }
        return array[row][column];
    }

    /**
     * Définit la valeur de la cellule aux coordonnées spécifiées.
     * @param row L'indice de ligne (0-based).
     * @param column L'indice de colonne (0-based).
     * @param value La nouvelle valeur pour la cellule.
     * @throws IllegalArgumentException si les coordonnées sont invalides.
     */
    public void setCell(int row, int column, T value) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides : (" + row + "," + column + ")");
        }
        array[row][column] = value;
    }

    /**
     * Fournit une représentation textuelle simple de la grille.
     * Utile principalement pour le débogage en console.
     * @return Une chaîne représentant la grille.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Affiche la valeur de la cellule ou une chaîne vide si null
                sb.append(array[i][j] == null ? "" : array[i][j]);
                if (j < width - 1) sb.append("|"); // Séparateur de colonnes
            }
            if (i < height - 1) sb.append("\n"); // Séparateur de lignes
        }
        return sb.toString();
    }
}
