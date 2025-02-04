package mihan.sossou.tp2;

public class Crossword<T> {
	
	private Grid<T> solution;
	private Grid<T> proposition;
	private Grid<T> horizontal;
	private Grid<T> vertical;
	
	public Crossword(int height, int width) {
		
		this.solution = new Grid<>(height, width);
		this.proposition = new Grid<>(height, width);
		this.horizontal = new Grid<>(height, width);
		this.vertical = new Grid<>(height, width);
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
		 if (!correctCoords(row, column)) {
	          throw new IllegalArgumentException("Coordonnées invalides");
		  }
		  return solution.getCell(row, column) == null;
	 }

	/**
	* Déclarer la case (row, column) noire ou blanche
	*/
	@SuppressWarnings("unchecked")
	public void setBlackSquare(int row, int column, boolean black) {
		if (!correctCoords(row, column)) {
	         throw new IllegalArgumentException("Coordonnées invalides");
	     }
		 solution.setCell(row, column, black ? null : (T) " ");
	}
	
	/**
	* @return la solution dans la case (row, column)
	* @pre correctCoords(row, column) && !isBlackSquare(row, column)
	*/
	public T getSolution(int row, int column) {
		
		if (!correctCoords(row, column) || isBlackSquare(row, column)) {
			throw new IllegalArgumentException("Coordonnées invalides ou case noire.");
		}
		return solution.getCell(row, column);
	}

	public void setSolution(int row, int column, T solution) {
		if (!correctCoords(row, column) || isBlackSquare(row, column)) {
			throw new IllegalArgumentException("Coordonnées invalides ou case noire.");
		}
			
		this.solution.setCell(row, column, solution);
	}
	
	public T getProposition(int row, int column) {
		if (!correctCoords(row, column) || isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides ou case noire");
        }
	     return proposition.getCell(row, column);
	}

	public void setProposition(int row, int column, T prop) {
		if (!correctCoords(row, column) || isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides ou case noire");
        }
		proposition.setCell(row, column, prop);
	}
	
	/**
	* 	@return la définition horizontale dans (row, column)		
	*	si horizontal, et la définition verticale sinon
	*	@pre correctCoords(row, column) && !isBlackSquare(row, column)
	*/
	public T getDefinition(int row, int column, boolean horizontal) {
		 if (!correctCoords(row, column) || isBlackSquare(row, column)) {
	            throw new IllegalArgumentException("Coordonnées invalides ou case noire");
	        }
		if (horizontal) {
			return this.horizontal.getCell(row, column);
		}else {
			return this.vertical.getCell(row, column);
		}
	}

	public void setDefinition(int row, int column, boolean horizontal, T definition) {
		if (!correctCoords(row, column) || isBlackSquare(row, column)) {
	            throw new IllegalArgumentException("Coordonnées invalides ou case noire");
	        }
		if (horizontal) {
			this.horizontal.setCell(row, column, definition);
		}else {
			this.vertical.setCell(row, column, definition);
		}
	}
}