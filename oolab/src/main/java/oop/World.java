package oop.model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class World extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Tworzenie prostego GUI
        StackPane root = new StackPane();
        root.getChildren().add(new Label("Hello, JavaFX World!"));

        // Scene (szerokość, wysokość)
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("JavaFX Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Rozpocznij JavaFX
    }
}