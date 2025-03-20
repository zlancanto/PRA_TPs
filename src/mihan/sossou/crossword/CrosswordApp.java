package mihan.sossou.crossword;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mihan.sossou.crossword.view.CrosswordView;

public class CrosswordApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        CrosswordView view = new CrosswordView();
        Scene scene = new Scene(view.getRoot(), 800, 600);

        primaryStage.setTitle("Mots Croisés");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

