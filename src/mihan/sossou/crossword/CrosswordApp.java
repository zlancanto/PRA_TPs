package mihan.sossou.crossword;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mihan.sossou.crossword.view.CrosswordView;

import java.io.IOException;
import java.util.Objects;

public class CrosswordApp extends Application {

    private static final String STYLESHEET_PATH = "/style/";

    @Override
    public void start(Stage primaryStage) {
        CrosswordView view = new CrosswordView();
        Scene scene = new Scene(view.getRoot(), 800, 600);

        try {
            scene.getStylesheets()
                 .add(Objects.requireNonNull(getClass()
                 .getResource(STYLESHEET_PATH + "crossword-square.css"))
                 .toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Chemin du fichier de style introuvable !");
            e.printStackTrace();
        }

        primaryStage.setTitle("Crossword Puzzle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

