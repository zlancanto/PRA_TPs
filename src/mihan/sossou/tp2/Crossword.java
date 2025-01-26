package mihan.sossou.tp2;

public class Crossword<T> {
	
	private Grid<T> solution;
	private Grid<T> proposition;
	private Grid<T> horizontal;
	private Grid<T> vertical;
	
	public Crossword(int hauteur, int largeur) {
		
		this.solution = new Grid<>(hauteur, largeur);
		this.proposition = new Grid<>(hauteur, largeur);
		this.horizontal = new Grid<>(hauteur, largeur);
		this.vertical = new Grid<>(hauteur, largeur);
	}
	
	public int getHeight() {
		
		return solution.getHeight();
	}
	
	public int getWidth() {

		return solution.getWidth();		
	}
	
	/**
	* Vérifier la validité des coordonnées
	* @return true si et seulement si (row, column)
	désignent une cellule existante de la grille
	*/
	public boolean correctCoords(int row, int column) {
		
		return solution.correctCoords(row, column);
	}
	
	/**
	* @return true si la case est noire, false sinon
	* @pre correctCoords(row, column)
	* */
	public boolean isBlackSquare(int row, int column) {		
		
		if (correctCoords(row, column)) {
			return solution.getCell(row, column) == null;
		}
	
		return false;
	}

	/**
	* Déclarer la case (row, column) noire ou blanche
	*/
	@SuppressWarnings("unchecked")
	public void setBlackSquare(int row, int column, boolean black) {
		
		if (black && solution.getCell(row, column) != null) {
			solution.setCell(row, column, null);
		}
		
		if (!black) {
			solution.setCell(row, column, (T) "");
		}		
	}
	
	/**
	* @return la solution dans la case (row, column)
	* @pre correctCoords(row, column) && !isBlackSquare(row, column)
	*/
	public char getSolution(int row, int column) {
		
		if (correctCoords(row, column) && !isBlackSquare(row, column)) {
			String cellule = (String) solution.getCell(row, column);
			return cellule.charAt(0);
		}
		
		return '\0';
	}

	@SuppressWarnings("unchecked")
	public void setSolution(int row, int column, char solution) {
			
		this.solution.setCell(row, column, (T) String.valueOf(solution));
	}
	
	public char getProposition(int row, int column) {
		
		if (correctCoords(row, column) && !isBlackSquare(row, column)) {
			String cellule = (String) proposition.getCell(row, column);
			return cellule.charAt(0);
		}
		
		return '\0';
	}

	@SuppressWarnings("unchecked")
	public void setProposition(int row, int column, char prop) {
		
		proposition.setCell(row, column, (T) String.valueOf(prop));
	}
	
	/**
	* 	@return la définition horizontale dans (row, column)		
	*	si horizontal, et la définition verticale sinon
	*	@pre correctCoords(row, column) && !isBlackSquare(row, column)
	*/
	public String getDefinition(int row, int column, boolean horizontal) {
		
		if (!correctCoords(row, column)) {
			throw new IllegalArgumentException("Cellule inexistante");
		}
		
		if (isBlackSquare(row, column)) {
			throw new IllegalArgumentException("Cellule inexistante");
		}
		
		return horizontal ? (String) this.horizontal.getCell(row, column) : (String) vertical.getCell(row, column);
	}

	@SuppressWarnings("unchecked")
	public void setDefinition(int row, int column, boolean horizontal, String definition) {
		
		if (horizontal) {
			this.horizontal.setCell(row, column, (T) definition);
		}else {
			vertical.setCell(row, column, (T) definition);
		}
	}
}