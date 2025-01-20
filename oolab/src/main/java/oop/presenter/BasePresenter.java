package oop.model.presenter;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract public class BasePresenter {

    @FXML
    protected Pane rootPane;

    protected void showAlert(String header, String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected Set<Node> getElementsWithClass(String className) {
        return rootPane.lookupAll("." + className);
    }
}