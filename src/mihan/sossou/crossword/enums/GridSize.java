package mihan.sossou.crossword.enums;

public enum GridSize {
    SIZE(0, 0);

    private int height;
    private int width;

    GridSize(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
