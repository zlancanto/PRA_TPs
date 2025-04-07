package mihan.sossou.crossword.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import mihan.sossou.crossword.model.Clue;
import mihan.sossou.crossword.model.Crossword;
import mihan.sossou.crossword.model.CrosswordSquare;

import java.util.Optional;
import java.util.Random;


/**
 * Controller pour CrosswordView.fxml.
 * Gère les interactions utilisateur avec la grille et les listes d'indices.
 */
public class CrosswordController {

    @FXML private BorderPane mainPane;
    @FXML private SplitPane splitPane;
    @FXML private ScrollPane gridScrollPane;
    @FXML private GridPane gridPane;
    @FXML private ListView<Clue> horizontalCluesListView;
    @FXML private ListView<Clue> verticalCluesListView;
    @FXML private Label statusBar; // Barre de statut optionnelle

    private Database database;
    private Crossword crosswordModel;
    private TextField currentTextField = null; // Garde une trace du TextField actuellement focus

    // Indicateurs pour éviter les boucles d'événements
    private boolean internalFocusChange = false;
    private boolean internalClueSelectionChange = false;

    // Pour la mise en évidence de l'indice courant
    private Clue currentDirectionClue = null;


    // Constantes pour le style CSS
    private static final String CSS_CLASS_CELL = "crossword-cell";
    private static final String CSS_CLASS_BLACK_CELL = "black-cell";
    private static final String CSS_CLASS_FOCUSED_CELL = "focused-cell";
    private static final String CSS_CLASS_HIGHLIGHTED_CELL = "highlighted-cell";
    private static final String CSS_CLASS_CORRECT_GUESS = "correct-guess-cell"; // Nouveau
    private static final String CSS_CLASS_CURRENT_CLUE = "current-direction-clue"; // Nouveau


    /**
     * Initialise la classe contrôleur. Appelée automatiquement après
     * le chargement du fichier FXML.
     */
    @FXML
    private void initialize() {
        // Configure les ListView pour afficher les objets Clue
        configureClueListView(horizontalCluesListView);
        configureClueListView(verticalCluesListView);

        // Ajoute des écouteurs pour gérer la sélection d'indices dans les listes
        horizontalCluesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (!internalClueSelectionChange) {
                        handleClueSelection(newVal, true);
                    }
                });
        verticalCluesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (!internalClueSelectionChange) {
                        handleClueSelection(newVal, false);
                    }
                });

        // Ajoute un écouteur de clavier au gridPane pour la navigation et l'entrée
        gridPane.setFocusTraversable(true);
        gridPane.setOnKeyPressed(this::handleGridKeyPress);

        // Met le focus initial sur le gridPane
        Platform.runLater(() -> gridPane.requestFocus());

        // Raccourci Ctrl+W (sera ajouté dans CrosswordApp)
    }

    // --- Méthodes de gestion du modèle et de l'UI ---

    public void setDatabase(Database database) { this.database = database; }
    public GridPane getGridPane() { return gridPane; }

    public void loadPuzzle(int puzzleNumber) {
        if (database == null) { showError("Base de données non initialisée."); return; }
        try {
            System.out.println("Chargement grille #" + puzzleNumber);
            crosswordModel = Crossword.createPuzzle(database, puzzleNumber);
            System.out.println("Grille chargée. Dimensions: " + crosswordModel.getHeight() + "x" + crosswordModel.getWidth());
            populateGrid();
            populateClueLists();
            clearFocusAndHighlight();
            currentDirectionClue = null; // Réinitialise l'indice courant
            findAndFocusFirstCell();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la grille #" + puzzleNumber); e.printStackTrace();
            showError("Impossible de charger la grille: " + e.getMessage());
            gridPane.getChildren().clear();
            horizontalCluesListView.getItems().clear(); verticalCluesListView.getItems().clear();
        }
    }

    private void findAndFocusFirstCell() {
        for (int r = 0; r < crosswordModel.getHeight(); r++) {
            for (int c = 0; c < crosswordModel.getWidth(); c++) {
                if (!crosswordModel.isBlackSquare(r, c)) {
                    Node cellNode = getNodeByRowColumnIndex(r, c, gridPane);
                    if (cellNode instanceof TextField) {
                        boolean hasHorizontal = crosswordModel.getDefinition(r, c, true) != null;
                        boolean hasVertical = crosswordModel.getDefinition(r, c, false) != null;
                        crosswordModel.setCurrentDirection(hasHorizontal || !hasVertical);
                        focusCell((TextField) cellNode, r, c);
                        return;
                    }
                }
            }
        }
        System.out.println("Aucune cellule de départ valide trouvée.");
    }

    private void populateGrid() {
        gridPane.getChildren().clear(); gridPane.getRowConstraints().clear(); gridPane.getColumnConstraints().clear();
        if (crosswordModel == null) return;
        int numRows = crosswordModel.getHeight(); int numCols = crosswordModel.getWidth();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                TextField cell = createCellTextField(row, col);
                gridPane.add(cell, col, row);
            }
        }
        gridPane.requestFocus();
    }

    /**
     * Crée et configure un TextField pour une seule cellule de la grille.
     * Ajoute l'animation et la gestion du style 'correct'.
     */
    private TextField createCellTextField(int row, int col) {
        TextField textField = new TextField();
        textField.getStyleClass().add(CSS_CLASS_CELL);
        textField.setAlignment(Pos.CENTER);
        textField.setPrefSize(35, 35); textField.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); textField.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        CrosswordSquare square = crosswordModel.getCell(row, col);

        if (square.isBlack()) {
            textField.getStyleClass().add(CSS_CLASS_BLACK_CELL);
            textField.setDisable(true); textField.setEditable(false);
        } else {
            Character prop = square.getProposition();
            textField.setText((prop != null && prop != ' ' && prop != '\0') ? prop.toString() : "");

            // Écouteur pour mise à jour modèle ET animation ET style 'correct'
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                // Enlève le style 'correct' dès qu'on modifie la lettre
                textField.getStyleClass().remove(CSS_CLASS_CORRECT_GUESS);
                handleTextInput(textField, row, col, oldVal, newVal);

                // Animation ScaleTransition si une lettre est ajoutée
                if ((oldVal == null || oldVal.isEmpty()) && (newVal != null && !newVal.isEmpty())) {
                    animateCellEntry(textField);
                }
            });

            // Écouteur de focus
            textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused && !internalFocusChange) {
                    if (currentTextField != textField) {
                        System.out.println(">>> Focus Listener triggered for (" + row + "," + col + ")");
                        boolean hasHorizontal = crosswordModel.getDefinition(row, col, true) != null;
                        boolean hasVertical = crosswordModel.getDefinition(row, col, false) != null;
                        crosswordModel.setCurrentDirection(hasHorizontal || !hasVertical);
                        focusCell(textField, row, col);
                    }
                }
            });

            // Écouteur de clic
            textField.setOnMouseClicked(event -> handleCellClick(event, textField, row, col));
        }
        return textField;
    }

    /**
     * Anime l'entrée de texte dans une cellule.
     */
    private void animateCellEntry(TextField textField) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), textField);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Gère l'entrée de texte. Ne déplace PAS le focus si la cellule est vidée.
     */
    private void handleTextInput(TextField textField, int row, int col, String oldValue, String newValue) {
        if (crosswordModel == null || crosswordModel.isBlackSquare(row, col)) return;

        String processedValue = "";
        boolean wasCleared = false; // Pour savoir si on a effacé

        if (newValue != null && !newValue.isEmpty()) {
            char lastChar = newValue.charAt(newValue.length() - 1);
            if (Character.isLetter(lastChar)) {
                processedValue = Character.toString(Character.toUpperCase(lastChar));
            } else if (newValue.length() < oldValue.length()) {
                processedValue = ""; wasCleared = true;
            } else {
                processedValue = oldValue != null ? oldValue : "";
            }
        } else {
            processedValue = ""; wasCleared = true;
        }

        final String finalValue = processedValue;
        Platform.runLater(() -> {
            textField.setText(finalValue);
            textField.positionCaret(finalValue.length());
        });

        char propositionChar = finalValue.isEmpty() ? ' ' : finalValue.charAt(0);
        crosswordModel.setProposition(row, col, propositionChar);

        // Déplace seulement si une lettre a été ajoutée (pas si effacée)
        if (!finalValue.isEmpty()) {
            moveToNextEditableCell(crosswordModel.isCurrentDirectionHorizontal(), 1);
        }
        // Le déplacement arrière pour Backspace est géré dans handleGridKeyPress
    }


    private void handleCellClick(MouseEvent event, TextField textField, int row, int col) {
        if (crosswordModel == null || crosswordModel.isBlackSquare(row, col)) return;
        System.out.println(">>> Click detected on (" + row + "," + col + ")");

        if (textField == currentTextField) {
            crosswordModel.toggleCurrentDirection();
            System.out.println("... Direction toggled to: " + (crosswordModel.isCurrentDirectionHorizontal() ? "Horizontal" : "Vertical"));
            highlightWord(); // Re-surligne et met à jour l'indice courant
            updateCurrentDirectionClueHighlight(); // Met à jour l'indice rouge
        } else {
            boolean hasHorizontal = crosswordModel.getDefinition(row, col, true) != null;
            boolean hasVertical = crosswordModel.getDefinition(row, col, false) != null;
            crosswordModel.setCurrentDirection(hasHorizontal || !hasVertical);
            System.out.println("... Focusing different cell (" + row + "," + col + ")");
            focusCell(textField, row, col); // Appelle highlightWord et selectClues...
        }
        event.consume();
    }

    /**
     * Gère les pressions de touches (flèches, Entrée, Effacer, Tab, Espace, lettres).
     */
    private void handleGridKeyPress(KeyEvent event) {
        if (crosswordModel == null) return;
        if (currentTextField == null) {
            findAndFocusFirstCell();
            if (currentTextField == null) return;
        }

        KeyCode code = event.getCode();
        boolean horizontal = crosswordModel.isCurrentDirectionHorizontal();
        boolean moved = false;
        int currentRow = crosswordModel.getCurrentRow();
        int currentCol = crosswordModel.getCurrentColumn();

        switch (code) {
            case UP:    moved = moveToNextEditableCell(false, -1); break;
            case DOWN:  moved = moveToNextEditableCell(false, 1); break;
            case LEFT:  moved = moveToNextEditableCell(true, -1); break;
            case RIGHT: moved = moveToNextEditableCell(true, 1); break;

            case BACK_SPACE:
                if (currentTextField != null) {
                    // 1. Efface le texte de la case COURANTE
                    currentTextField.setText(""); // Déclenche handleTextInput qui met à jour le modèle mais ne déplace plus

                    // 2. Déplace le curseur en arrière dans la direction OPPOSEE
                    boolean moveSuccess = moveToNextEditableCell(!horizontal, -1); // Essaye direction opposée
                    if (!moveSuccess) { // Si bloqué, essaye même direction
                        moveToNextEditableCell(horizontal, -1);
                    }
                    moved = true;
                }
                break;

            case DELETE: // Efface sans bouger (comportement standard)
                if (currentTextField != null) {
                    currentTextField.setText(""); // Déclenche handleTextInput
                    moved = true;
                }
                break;

            case TAB:   // Bascule direction
                crosswordModel.toggleCurrentDirection();
                highlightWord();
                updateCurrentDirectionClueHighlight(); // Met à jour l'indice rouge
                moved = true;
                break;

            case ENTER: // Vérifie le mot courant
                checkCurrentWord();
                moved = true;
                break;

            default:
                if (code.isLetterKey() && currentTextField != null) {
                    if (!currentTextField.isFocused()) {
                        internalFocusChange = true;
                        currentTextField.requestFocus();
                        internalFocusChange = false;
                    }
                    // Laisse l'écouteur du TextField gérer l'entrée et le déplacement avant
                }
                break;
        }
        if (moved) event.consume();
    }

    /**
     * Vérifie les lettres du mot courant contre la solution et applique le style vert.
     */
    private void checkCurrentWord() {
        if (crosswordModel == null || currentTextField == null) return;

        int r = crosswordModel.getCurrentRow();
        int c = crosswordModel.getCurrentColumn();
        if (r < 0 || c < 0) return;
        boolean horizontal = crosswordModel.isCurrentDirectionHorizontal();

        // Trouve le début et la fin du mot
        int startR = r, startC = c;
        while (crosswordModel.correctCoords(startR, startC) && !crosswordModel.isBlackSquare(startR, startC)) {
            if (horizontal) { if (!crosswordModel.correctCoords(startR, startC - 1) || crosswordModel.isBlackSquare(startR, startC - 1)) break; startC--; }
            else { if (!crosswordModel.correctCoords(startR - 1, startC) || crosswordModel.isBlackSquare(startR - 1, startC)) break; startR--; }
        }

        int currentR = startR, currentC = startC;
        while (crosswordModel.correctCoords(currentR, currentC) && !crosswordModel.isBlackSquare(currentR, currentC)) {
            char proposition = crosswordModel.getProposition(currentR, currentC);
            char solution = crosswordModel.getSolution(currentR, currentC);
            Node cellNode = getNodeByRowColumnIndex(currentR, currentC, gridPane);

            if (cellNode instanceof TextField) {
                // Enlève d'abord pour éviter accumulation si incorrect
                cellNode.getStyleClass().remove(CSS_CLASS_CORRECT_GUESS);
                // Ajoute si correct ET non vide
                if (proposition != ' ' && proposition != '\0' && proposition == solution) {
                    cellNode.getStyleClass().add(CSS_CLASS_CORRECT_GUESS);
                }
            }
            if (horizontal) currentC++; else currentR++;
        }
    }


    private boolean moveToNextEditableCell(boolean horizontal, int step) {
        if (crosswordModel == null || currentTextField == null) return false;
        int startRow = crosswordModel.getCurrentRow();
        int startCol = crosswordModel.getCurrentColumn();
        if (startRow < 0 || startCol < 0) return false;
        int numRows = crosswordModel.getHeight(); int numCols = crosswordModel.getWidth();
        int r = startRow; int c = startCol;

        do {
            if (horizontal) c += step; else r += step;
            if ( (r < 0 || r >= numRows) || (c < 0 || c >= numCols) ) return false; // Limite atteinte
            if (!crosswordModel.isBlackSquare(r, c)) {
                Node cellNode = getNodeByRowColumnIndex(r, c, gridPane);
                if (cellNode instanceof TextField) {
                    // Important: Mettre à jour la direction *avant* d'appeler focusCell si nécessaire
                    // Ici, on préserve la direction courante lors du déplacement par flèches
                    crosswordModel.setCurrentDirection(horizontal); // Non, on garde la direction actuelle
                    focusCell((TextField) cellNode, r, c);
                    return true;
                }
            }
        } while (true);
    }


    private void focusCell(TextField textField, int row, int col) {
        if (textField == null || crosswordModel == null || crosswordModel.isBlackSquare(row, col)) {
            System.err.println("Tentative de focus sur une cellule invalide: " + row + "," + col); return;
        }
        if (textField == currentTextField) {
            if (!textField.isFocused()) {
                internalFocusChange = true; textField.requestFocus(); internalFocusChange = false;
            }
            return; // Déjà la cellule courante
        }
        System.out.println("--- Focusing cell (" + row + "," + col + ") ---");
        clearFocusAndHighlight();
        currentTextField = textField;
        crosswordModel.setCurrentPosition(row, col);
        textField.getStyleClass().add(CSS_CLASS_FOCUSED_CELL);
        highlightWord(); // Surligne le mot
        selectCluesForCurrentCell(); // Sélectionne indices dans les listes
        updateCurrentDirectionClueHighlight(); // Met à jour l'indice rouge
        ensureCellVisible(textField);
        internalFocusChange = true;
        Platform.runLater(() -> {
            System.out.println("... Demande de mise au point pour (" + row + "," + col + ") via Platform.runLater");
            textField.requestFocus(); textField.selectAll(); internalFocusChange = false;
        });
        System.out.println("--- Cellule de mise au point (" + row + "," + col + ") terminé ---");
    }

    private void ensureCellVisible(TextField cell) {
        if (gridScrollPane == null || cell == null) return;
        double cellMinY = cell.getLayoutY(); double cellHeight = cell.getHeight();
        double viewportHeight = gridScrollPane.getViewportBounds().getHeight(); double gridHeight = gridPane.getHeight();
        double currentVvalue = gridScrollPane.getVvalue(); double targetVvalue = currentVvalue;
        double viewportMinY = (gridHeight - viewportHeight) * currentVvalue; double viewportMaxY = viewportMinY + viewportHeight;
        if (cellMinY < viewportMinY) targetVvalue = cellMinY / (gridHeight - viewportHeight);
        else if (cellMinY + cellHeight > viewportMaxY) targetVvalue = (cellMinY + cellHeight - viewportHeight) / (gridHeight - viewportHeight);
        double cellMinX = cell.getLayoutX(); double cellWidth = cell.getWidth();
        double viewportWidth = gridScrollPane.getViewportBounds().getWidth(); double gridWidth = gridPane.getWidth();
        double currentHvalue = gridScrollPane.getHvalue(); double targetHvalue = currentHvalue;
        double viewportMinX = (gridWidth - viewportWidth) * currentHvalue; double viewportMaxX = viewportMinX + viewportWidth;
        if (cellMinX < viewportMinX) targetHvalue = cellMinX / (gridWidth - viewportWidth);
        else if (cellMinX + cellWidth > viewportMaxX) targetHvalue = (cellMinX + cellWidth - viewportWidth) / (gridWidth - viewportWidth);
        if (Math.abs(targetVvalue - currentVvalue) > 1e-6) gridScrollPane.setVvalue(Math.max(0, Math.min(1, targetVvalue)));
        if (Math.abs(targetHvalue - currentHvalue) > 1e-6) gridScrollPane.setHvalue(Math.max(0, Math.min(1, targetHvalue)));
    }

    private void clearFocusAndHighlight() {
        if (currentTextField != null) currentTextField.getStyleClass().remove(CSS_CLASS_FOCUSED_CELL);
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField) {
                node.getStyleClass().removeAll(CSS_CLASS_HIGHLIGHTED_CELL, CSS_CLASS_FOCUSED_CELL, CSS_CLASS_CORRECT_GUESS);
            }
        }
        // Ne pas effacer le style de l'indice courant ici
    }

    private void highlightWord() {
        if (crosswordModel == null || currentTextField == null) return;
        int r = crosswordModel.getCurrentRow(); int c = crosswordModel.getCurrentColumn();
        if (r < 0 || c < 0) return;
        boolean horizontal = crosswordModel.isCurrentDirectionHorizontal();

        // Efface seulement le surlignage jaune, pas le focus ou le vert
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField) node.getStyleClass().remove(CSS_CLASS_HIGHLIGHTED_CELL);
        }

        int startR = r, startC = c;
        while (crosswordModel.correctCoords(startR, startC) && !crosswordModel.isBlackSquare(startR, startC)) {
            if (horizontal) { if (!crosswordModel.correctCoords(startR, startC - 1) || crosswordModel.isBlackSquare(startR, startC - 1)) break; startC--; }
            else { if (!crosswordModel.correctCoords(startR - 1, startC) || crosswordModel.isBlackSquare(startR - 1, startC)) break; startR--; }
        }

        int currentR = startR, currentC = startC;
        while (crosswordModel.correctCoords(currentR, currentC) && !crosswordModel.isBlackSquare(currentR, currentC)) {
            Node cellNode = getNodeByRowColumnIndex(currentR, currentC, gridPane);
            if (cellNode instanceof TextField) {
                if (!cellNode.getStyleClass().contains(CSS_CLASS_HIGHLIGHTED_CELL)) cellNode.getStyleClass().add(CSS_CLASS_HIGHLIGHTED_CELL);
            }
            if (horizontal) currentC++; else currentR++;
        }

        // Réapplique le focus si nécessaire
        if (currentTextField != null) {
            if (!currentTextField.getStyleClass().contains(CSS_CLASS_FOCUSED_CELL)) currentTextField.getStyleClass().add(CSS_CLASS_FOCUSED_CELL);
        }
    }

    private void populateClueLists() {
        horizontalCluesListView.getItems().clear(); verticalCluesListView.getItems().clear();
        if (crosswordModel != null) {
            horizontalCluesListView.setItems(crosswordModel.getHorizontalClues());
            verticalCluesListView.setItems(crosswordModel.getVerticalClues());
        }
    }

    /**
     * Configure la ListView pour appliquer le style rouge à l'indice courant.
     */
    private void configureClueListView(ListView<Clue> listView) {
        listView.setCellFactory(param -> new ListCell<Clue>() {
            @Override
            protected void updateItem(Clue item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getClue() == null) {
                    setText(null);
                    // Important: Retirer le style si la cellule est réutilisée et vide
                    getStyleClass().remove(CSS_CLASS_CURRENT_CLUE);
                } else {
                    setText(item.toString());
                    // Applique ou retire le style en fonction de si c'est l'indice courant
                    if (item.equals(currentDirectionClue)) {
                        if (!getStyleClass().contains(CSS_CLASS_CURRENT_CLUE)) {
                            getStyleClass().add(CSS_CLASS_CURRENT_CLUE);
                        }
                    } else {
                        getStyleClass().remove(CSS_CLASS_CURRENT_CLUE);
                    }
                }
            }
        });
    }

    /**
     * Met à jour quelle cellule dans les ListViews doit avoir le style rouge.
     */
    private void updateCurrentDirectionClueHighlight() {
        if (crosswordModel == null || currentTextField == null) {
            currentDirectionClue = null;
        } else {
            int r = crosswordModel.getCurrentRow();
            int c = crosswordModel.getCurrentColumn();
            boolean horizontal = crosswordModel.isCurrentDirectionHorizontal();
            currentDirectionClue = findClueContaining(
                    horizontal ? horizontalCluesListView.getItems() : verticalCluesListView.getItems(),
                    r, c, horizontal);
        }

        // Force les ListViews à redessiner leurs cellules pour appliquer le style
        horizontalCluesListView.refresh();
        verticalCluesListView.refresh();
    }


    private void selectCluesForCurrentCell() {
        if (crosswordModel == null || currentTextField == null) return;
        int r = crosswordModel.getCurrentRow(); int c = crosswordModel.getCurrentColumn();
        if (r < 0 || c < 0) return;

        Clue horizontalMatch = findClueContaining(horizontalCluesListView.getItems(), r, c, true);
        Clue verticalMatch = findClueContaining(verticalCluesListView.getItems(), r, c, false);

        internalClueSelectionChange = true;
        try {
            horizontalCluesListView.getSelectionModel().clearSelection();
            verticalCluesListView.getSelectionModel().clearSelection();
            if (horizontalMatch != null && verticalMatch != null) {
                horizontalCluesListView.getSelectionModel().select(horizontalMatch);
                horizontalCluesListView.scrollTo(horizontalMatch);
                verticalCluesListView.getSelectionModel().select(verticalMatch);
                verticalCluesListView.scrollTo(verticalMatch);
            }
        } finally {
            Platform.runLater(() -> internalClueSelectionChange = false);
        }
    }

    private Clue findClueContaining(ObservableList<Clue> clues, int row, int col, boolean horizontal) {
        if (clues == null) return null;
        for (Clue clue : clues) {
            if (clue.isHorizontal() == horizontal) {
                int startRow = clue.getRow(); int startCol = clue.getColumn();
                int length = getWordLength(startRow, startCol, horizontal);
                if (horizontal) { if (row == startRow && col >= startCol && col < startCol + length) return clue; }
                else { if (col == startCol && row >= startRow && row < startRow + length) return clue; }
            }
        }
        return null;
    }

    private int getWordLength(int startRow, int startCol, boolean horizontal) {
        if (crosswordModel == null || !crosswordModel.correctCoords(startRow, startCol) || crosswordModel.isBlackSquare(startRow, startCol)) return 0;
        int length = 0; int r = startRow; int c = startCol;
        while (crosswordModel.correctCoords(r, c) && !crosswordModel.isBlackSquare(r, c)) {
            length++; if (horizontal) c++; else r++;
        } return length;
    }

    private void handleClueSelection(Clue selectedClue, boolean horizontal) {
        if (selectedClue == null || crosswordModel == null) return;
        System.out.println(">>> handleClueSelection triggered for: " + selectedClue + " (Horizontal: " + horizontal + ")");
        int startRow = selectedClue.getRow(); int startCol = selectedClue.getColumn();

        if (startRow == crosswordModel.getCurrentRow() && startCol == crosswordModel.getCurrentColumn()) {
            if (crosswordModel.isCurrentDirectionHorizontal() != horizontal) {
                crosswordModel.setCurrentDirection(horizontal);
                highlightWord();
                updateCurrentDirectionClueHighlight(); // Met à jour l'indice rouge
            } return;
        }
        Node cellNode = getNodeByRowColumnIndex(startRow, startCol, gridPane);
        if (cellNode instanceof TextField) {
            TextField targetCell = (TextField) cellNode;
            crosswordModel.setCurrentDirection(horizontal);
            focusCell(targetCell, startRow, startCol); // Appelle highlightWord et updateCurrent...
        } else {
            System.err.println("Impossible de trouver le TextField pour l'indice sélectionné à : " + startRow + "," + startCol);
        }
    }

    // --- Gestionnaires de Menu ---
    @FXML private void handleLoadPuzzle() { /* ... (inchangé) ... */
        if (database == null) { showError("BD non disponible."); return; }
        int maxGrid = database.gridSize();
        if (maxGrid <= 0) { showError("Aucun puzzle n'a été trouvé dans la BD."); return; }
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Charger une Grille");
        dialog.setHeaderText("Entrez le numéro de la grille (1-" + maxGrid + ")");
        dialog.setContentText("Numéro:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(numStr -> {
            try {
                int puzzleNum = Integer.parseInt(numStr);
                if (puzzleNum > 0 && puzzleNum <= maxGrid) loadPuzzle(puzzleNum);
                else showError("Numéro de grille invalide. Entrez un nombre entre 1 et " + maxGrid + ".");
            } catch (NumberFormatException e) { showError("Entrée invalide. Veuillez entrer un nombre.");
            } catch (Exception e) { showError("Erreur lors du chargement de la grille: " + e.getMessage()); }
        });
    }
    @FXML private void handleLoadRandomPuzzle() { /* ... (inchangé) ... */
        if (database == null) { showError("La base de données n'est pas disponible."); return; }
        int maxPuzzles = database.gridSize();
        if (maxPuzzles <= 0) { showError("Aucun crossword n'est disponible dans la BD."); return; }
        Random random = new Random();
        int randomPuzzleNum = random.nextInt(maxPuzzles) + 1;
        loadPuzzle(randomPuzzleNum);
    }
    @FXML private void handleExit() { Platform.exit(); }
    @FXML private void handleAbout() { /* ... (inchangé) ... */
        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("À propos de Mots Croisés");
        aboutAlert.setHeaderText("Mots Croisés JavaFX");
        aboutAlert.setContentText("Développé par SOSSOU Horacio & MIHAN Zlanca-Nto.\nBasé sur les exigences du TP6 PRA (2024/25).\nUtilise JavaFX.");
        aboutAlert.showAndWait();
    }

    // --- Méthodes Utilitaires ---
    public void showError(String message) { /* ... (inchangé) ... */
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur"); alert.setHeaderText(null); alert.setContentText(message);
            alert.showAndWait();
        });
    }
    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) { /* ... (inchangé) ... */
        Node result = null; ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            Integer nodeRow = GridPane.getRowIndex(node); Integer nodeCol = GridPane.getColumnIndex(node);
            int r = (nodeRow == null) ? 0 : nodeRow; int c = (nodeCol == null) ? 0 : nodeCol;
            if (r == row && c == column) { result = node; break; }
        } return result;
    }
}
