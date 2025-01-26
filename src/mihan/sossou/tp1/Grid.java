package mihan.sossou.tp1;

public class Grid {
	
	private int height;
	private int width;
	private String[][] array;
	
	/**
	 * Constructeur permettant d’obtenir une grille dotée d’un tableau
	 * de dimensions conformes aux valeurs respectives de height et
	 * de width, dont tous les éléments contiennent la valeur null.
	 * @pre height >= 0 et width >= 0
	 */
	public Grid(int height, int width) {
		assert height >= 0 : "height doit être >= 0";
		assert width >= 0 : "width doit être >= 0";
		this.height = height;
		this.width = width;
		this.array = new String[height][width];
	}

	// Accesseurs (getters)
	public int getHeight() {	
		
		return height;
	}

	public int getWidth() {
		
		return width;
	}
	
	/**
	 * Vérifier la validité des coordonnées
	 * @return true si et seulement si (row, column)
	 */
	public boolean correctCoords(int row, int column) {
		
		return (0 <= row && row < width ) && (0 <= column && column < height);
	}
	
	/**
	 * @return valeur de la cellule ayant pour coordonnées
	 * (row, column), où (row, column) est compris entre 0 et
	 * getHeight()-1 (resp. getWidth()-1)
	 * @pre correctCoords(row, column)
	 */
	public String getCell(int row, int column) {
		
		if (!correctCoords(row, column)) {
	        throw new IllegalArgumentException("Cellule [" + row + "][" + column + "] inexistante");
	    }
	    return array[row][column];
	}
	
	/**
	 * Modifier de la cellule de rangée row et colonne column
	 * @pre coordCorrectes(row, column)
	 */
	public void setCell(int row, int column, String string) {
		
		assert correctCoords(row, column) : "Cellule [" + row + "][" + column + "] inexistante : Mise à jour impossible";
	    array[row][column] = string;
	}
	
	/**
	* @return texte sur height lignes ; colonnes séparées par des
	* (voir exemple plus loin)
	*/
	@Override
	public String toString() {
	    StringBuilder table = new StringBuilder();
	    
	    for (int row = 0; row < height; ++row) {
	        for (int column = 0; column < width; ++column) {
	            // Ajouter la valeur de la cellule ou "null" si la cellule est vide
	            table.append(array[row][column] == null ? " " : array[row][column]);
	            
	            // Ajouter un séparateur entre les colonnes sauf pour la dernière
	            if (column < width - 1) {
	                table.append(" | ");
	            }
	        }
	        table.append("\n"); // Ajouter une nouvelle ligne après chaque ligne
	    }
	    
	    return table.toString();
	}
	
	/*public static void main(String[] args) {
		
		Grid myGrid = new Grid(3,5) ;
		
		for (int l = 0; l < myGrid.getHeight(); l++) {
			
			String lineNumber = Integer.toString(l);
			
			for (int c = 0; c < myGrid.getWidth(); c++) {
				
				myGrid.setCell (l, c, lineNumber + "," + Integer.toString(c));
			}
		}
		System.out.println(myGrid) ;

	}*/
}
