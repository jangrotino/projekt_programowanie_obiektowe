package oop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import oop.model.presenter.MenuPresenter;

import java.util.Objects;

public class World extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("menu.fxml"));
        GridPane viewRoot = loader.load();
        MenuPresenter presenter = loader.getController();

        stage.setTitle("Darwin World - Menu");

        // Ustawienie sceny i ikony
        stage.setTitle("Darwin World - Menu");
        stage.setMaximized(true);
        stage.setScene(new Scene(viewRoot));
        stage.show();
    }
}