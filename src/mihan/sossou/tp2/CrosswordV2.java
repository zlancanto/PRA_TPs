package mihan.sossou.tp2;

import java.util.Iterator;

public class CrosswordV2 extends Grid<CrosswordSquare> {
	
	public CrosswordV2(int height, int width) {
		
		super (height, width);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				setCell(i,j, new CrosswordSquare());
			}
			
		}
	}
	
	/**
	* Vérifier la validité des coordonnées
	* @return true si et seulement si (row, column)
	désignent une cellule existante de la grille
	*/
	public boolean correctCoords(int row, int column) {
		return correctCoords(row, column);
	}
	
	/**
	* @return true si la case est noire, false sinon
	* @pre correctCoords(row, column)
	* */
	 public boolean isBlackSquare(int row, int column) {
		 if (!correctCoords(row, column)) {
	          throw new IllegalArgumentException("Coordonnées invalides");
		  }
		  return getCell(row, column).status();
	 }

	/**
	* Déclarer la case (row, column) noire ou blanche
	*/
	@SuppressWarnings("unchecked")
	public void setBlackSquare(int row, int column, boolean black) {
		if (!correctCoords(row, column)) {
	         throw new IllegalArgumentException("Coordonnées invalides");
	     }
		 getCell(row, column).setBlack(black);
	}
	
	/**
	* @return la solution dans la case (row, column)
	* @pre correctCoords(row, column) && !isBlackSquare(row, column)
	*/
	public char getSolution(int row, int column) {
		CrosswordSquare square = getCell(row, column);
		if (square.status() || square.getSolution() == null) {
			throw new IllegalArgumentException("Case noire ou sans solution");
		}
		return square.getSolution();
	}

	public void setSolution(int row, int column, char solution) {
		CrosswordSquare square = getCell(row, column);
		if (square.status() || square.getSolution() == null) {
			throw new IllegalArgumentException("Case noire ou sans solution");
		}
		square.setSolution(solution);
	}
	
	public char getProposition(int row, int column) {
		CrosswordSquare square = getCell(row, column);
		if (square.status() || square.getProposition() == null) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }
	     return square.getProposition();
	}

	public void setProposition(int row, int column, char prop) {
		CrosswordSquare square = getCell(row, column);
		if (square.status() || square.getProposition() == null) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }
		square.setProposition(prop);
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