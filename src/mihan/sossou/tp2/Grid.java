package mihan.sossou.tp2;

public class Grid<T> {
    private int height;
    private int width;
    private T[][] array;

    @SuppressWarnings("unchecked")
    public Grid(int height, int width) {
    	if (height < 0 || width < 0) {
            throw new IllegalArgumentException("Les dimensions doivent être positives");
        }
        this.height = height;
        this.width = width;
        this.array = (T[][]) new Object[height][width];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean correctCoords(int row, int column) {
        return row >= 0 && row < height && column >= 0 && column < width;
    }

    public T getCell(int row, int column) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        return array[row][column];
    }

    public void setCell(int row, int column, T value) {
        if (!correctCoords(row, column)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        array[row][column] = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(array[i][j] == null ? "" : array[i][j]);
                if (j < width - 1) sb.append("|");
            }
            if (i < height - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
