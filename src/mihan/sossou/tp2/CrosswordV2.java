package mihan.sossou.tp2;

public class CrosswordV2 extends Grid<CrosswordSquare>
{

    public CrosswordV2(int height, int width) {

        super(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setCell(i, j, new CrosswordSquare());
            }

        }
    }

    /**
     * @return true si la case est noire, false sinon
     * @pre correctCoords(row, column)
     */
    public boolean isBlackSquare(int row, int column) {

        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        return getCell(row, column).isStatut();
    }

    /**
     * Déclarer la case (row, column) noire ou blanche
     */
    public void setBlackSquare(int row, int column, boolean black) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        CrosswordSquare square = getCell(row, column);
        if (!square.isStatut() && black) {
            square.setSolution(null);
            square.setProposition(null);
            square.setHorizontal(null);
            square.setVertical(null);
            square.setStatut(true);
        }
        else if (square.isStatut() && !black) {
            square.setSolution(' ');
            square.setProposition(' ');
            square.setStatut(false);
        }
    }

    /**
     * @return la solution dans la case (row, column)
     * @pre correctCoords(row, column) && !isBlackSquare(row, column)
     */
    public char getSolution(int row, int column) {

        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        return getCell(row, column).getSolution();
    }

    public void setSolution(int row, int column, char solution) {

        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        CrosswordSquare square = getCell(row, column);
        square.setSolution(solution);
        if (square.isStatut()) {
            square.setStatut(false);
        }
    }

    public char getProposition(int row, int column) {

        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        CrosswordSquare square = getCell(row, column);

        return square.getProposition();
    }

    public void setProposition(int row, int column, char prop) {

        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

        if (isBlackSquare(row, column)) {
            throw new IllegalArgumentException("Case noire ou sans solution");
        }

        CrosswordSquare square = getCell(row, column);
        square.setProposition(prop);
    }

    /**
     * @return la définition horizontale dans (row, column)
     * si horizontal, et la définition verticale sinon
     * @pre correctCoords(row, column) && !isBlackSquare(row, column)
     */
    public String getDefinition(int row, int column, boolean horizontal) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

		if (isBlackSquare(row, column)) {
			throw new IllegalArgumentException("Case noire ou sans solution");
		}

		CrosswordSquare square = getCell(row, column);

        if (horizontal) {
            return square.getHorizontal();
        }
        else {
            return square.getVertical();
        }
    }

    public void setDefinition(int row, int column, boolean horizontal, String definition) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }

		if (isBlackSquare(row, column)) {
			throw new IllegalArgumentException("Case noire ou sans solution");
		}

		CrosswordSquare square = getCell(row, column);
        if (horizontal) {
			square.setHorizontal(definition);
        }
        else {
			square.setVertical(definition);
        }
    }
}
