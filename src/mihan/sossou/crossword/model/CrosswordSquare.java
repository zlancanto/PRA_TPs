package mihan.sossou.crossword.model;

import javafx.beans.property.*;

/**
 * Représente une case unique dans la grille de mots croisés.
 * Contient la solution, la proposition du joueur, les définitions
 * associées (si c'est une case de départ), et si c'est une case noire.
 */
public class CrosswordSquare {

    // Propriétés JavaFX pour permettre la liaison (binding) si nécessaire
    private ObjectProperty<Character> solution; // La lettre correcte pour cette case
    private ObjectProperty<Character> proposition; // La lettre entrée par le joueur
    private StringProperty horizontalDefinition; // Définition si un mot horizontal commence ici
    private StringProperty verticalDefinition; // Définition si un mot vertical commence ici
    private BooleanProperty black; // Indique si c'est une case noire

    /**
     * Constructeur par défaut. Initialise une case comme étant noire
     * avec des propriétés vides. L'état sera modifié lors du chargement
     * de la grille.
     */
    public CrosswordSquare() {
        solution = new SimpleObjectProperty<>();
        proposition = new SimpleObjectProperty<>();
        horizontalDefinition = new SimpleStringProperty();
        verticalDefinition = new SimpleStringProperty();
        black = new SimpleBooleanProperty(true); // Commence noire par défaut
    }

    // --- Getters et Setters pour la Solution ---
    public Character getSolution() { return solution.get(); }
    public ObjectProperty<Character> solutionProperty() { return solution; }
    public void setSolution(Character solution) { this.solution.set(solution); }

    // --- Getters et Setters pour la Proposition ---
    public Character getProposition() { return proposition.get(); }
    public ObjectProperty<Character> propositionProperty() { return proposition; }
    public void setProposition(Character proposition) { this.proposition.set(proposition); }

    // --- Getters et Setters pour la Définition Horizontale ---
    public String getHorizontalDefinition() { return horizontalDefinition.get(); }
    public StringProperty horizontalDefinitionProperty() { return horizontalDefinition; }
    public void setHorizontalDefinition(String horizontalDefinition) { this.horizontalDefinition.set(horizontalDefinition); }

    // --- Getters et Setters pour la Définition Verticale ---
    public String getVerticalDefinition() { return verticalDefinition.get(); }
    public StringProperty verticalDefinitionProperty() { return verticalDefinition; }
    public void setVerticalDefinition(String verticalDefinition) { this.verticalDefinition.set(verticalDefinition); }

    // --- Getters et Setters pour l'état Case Noire ---
    public boolean isBlack() { return black.get(); }
    public BooleanProperty blackProperty() { return black; }
    public void setBlack(boolean black) { this.black.set(black); }
}
