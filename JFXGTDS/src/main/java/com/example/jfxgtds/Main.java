package com.example.jfxgtds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml")); // Make sure Main.fxml is in the same folder/package
        Scene scene = new Scene(loader.load(), 1000, 600);

        primaryStage.setTitle("Government Tax Department System (JFXGTDS)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}