package mihan.sossou.crossword.model;

import javafx.beans.property.*;

public class CrosswordSquare {
	
	private ObjectProperty<Character> solution;
    private ObjectProperty<Character> proposition;
    private StringProperty horizontalDefinition;
    private StringProperty verticalDefinition;
    // Case noire ou non
    private BooleanProperty black;

    public CrosswordSquare() {
    	solution = new SimpleObjectProperty<>();
    	proposition = new SimpleObjectProperty<>();
        horizontalDefinition = new SimpleStringProperty();
        verticalDefinition = new SimpleStringProperty();
        black = new SimpleBooleanProperty(true);
    }

    public Character getSolution() {
        return solution.get();
    }

    public ObjectProperty<Character> solutionProperty() {
        return solution;
    }

    public void setSolution(Character solution) {
        this.solution.set(solution);
    }

    public Character getProposition() {
        return proposition.get();
    }

    public ObjectProperty<Character> propositionProperty() {
        return proposition;
    }

    public void setProposition(Character proposition) {
        this.proposition.set(proposition);
    }

    public String getHorizontalDefinition() {
        return horizontalDefinition.get();
    }

    public StringProperty horizontalDefinitionProperty() {
        return horizontalDefinition;
    }

    public void setHorizontalDefinition(String horizontalDefinition) {
        this.horizontalDefinition.set(horizontalDefinition);
    }

    public String getVerticalDefinition() {
        return verticalDefinition.get();
    }

    public StringProperty verticalDefinitionProperty() {
        return verticalDefinition;
    }

    public void setVerticalDefinition(String verticalDefinition) {
        this.verticalDefinition.set(verticalDefinition);
    }

    public boolean isBlack() {
        return black.get();
    }

    public BooleanProperty blackProperty() {
        return black;
    }

    public void setBlack(boolean black) {
        this.black.set(black);
    }
}