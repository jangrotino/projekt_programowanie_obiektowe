package oop.model.presenter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oop.exceptions.InvalidGameParamsException;
import oop.model.Simulation;
import oop.model.SimulationParameters;

import java.io.*;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class MenuPresenter extends BasePresenter{

    @FXML
    private TextField formMapHeight;

    @FXML
    private TextField formMapWidth;

    @FXML
    private TextField formPlantStart;

    @FXML
    private TextField formPlantDaily;

    @FXML
    private TextField formPlantEnergy;

    @FXML
    private TextField formAnimalStart;

    @FXML
    private TextField formAnimalStartEnergy;

    @FXML
    private TextField formAnimalEnergyReproductionDepletion;

    @FXML
    private TextField formAnimalEnergyDailyDepletion;

    @FXML
    private TextField formAnimalEnergyToReproduce;

    @FXML
    private TextField formAnimalMutationMinimum;

    @FXML
    private TextField formAnimalMutationMaximum;

    @FXML
    private TextField formAnimalGenotypeLength;

    @FXML
    private Button createGame;

    @FXML
    private Button exportConfiguration;

    @FXML
    private Button importConfiguration;

    @FXML
    private CheckBox formExportStatistics;

    @FXML
    private void initialize() {
        createGame.setOnAction(event -> {
            try {
                SimulationParameters parameters = getWorldConfigFromParams();
                validateStartParameters(parameters);
                startNewSimulation(parameters);
            } catch (InvalidGameParamsException ex) {
                showErrorAlert("Invalid Parameters", "Please check your input.\n" + ex.getMessage());
            } catch (NumberFormatException ex) {
                showErrorAlert("Invalid Input", "Please enter only numeric values in all fields.");
            } catch (Exception ex) {
                showErrorAlert("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });
        exportConfiguration.setOnAction(event -> {
            try {
                exportConfigurationFile();
            } catch (Exception e) {
                showAlert("Error", "Error on saving configuration", "Cannot export the configuration file", Alert.AlertType.ERROR);
            }
        });

        importConfiguration.setOnAction(event -> {
            try {
                importConfigurationFile();
            } catch (Exception e) {
                showAlert("Error", "Error on importing configuration", "Cannot load the configuration file", Alert.AlertType.ERROR);
            }
        });
    }

    private void startNewSimulation(SimulationParameters parameters) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));

        // Załaduj scenę z FXML
        Object root = loader.load();

        if (!(root instanceof javafx.scene.Parent)) {
            throw new IllegalStateException("Root element of game.fxml is not a valid JavaFX parent element.");
        }

        GamePresenter presenter = loader.getController();

        // Utwórz nową symulację na podstawie parametrów wejściowych
        Simulation simulation = new Simulation(
                parameters.height(),
                parameters.width(),
                parameters.startPlantNumber(),
                parameters.plantsPerDay(),
                parameters.singlePlantEnergy(),
                parameters.startAnimalNumber(),
                parameters.startAnimalEnergy(),
                parameters.copulatingCost(),
                parameters.livingCost(),
                parameters.wellFedValue(),
                parameters.minMutationNum(),
                parameters.maxMutationNum(),
                parameters.genomLength()
        );

        presenter.setSimulation(simulation);
        presenter.setStatisticsExportEnabled(formExportStatistics.isSelected());
        if(formExportStatistics.isSelected()) {
            presenter.setFile(openFileExplorerAndGetFileType());
        }
        Platform.runLater(presenter::initialize);

        Thread simulationThread = new Thread(() -> {
            try {
                simulation.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        simulationThread.setDaemon(true);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setOnCloseRequest(event -> {
                simulation.stop();
                simulationThread.interrupt();
            });

            stage.setScene(new Scene((javafx.scene.Parent) root));
            stage.setTitle("Darwin World Simulation");
            stage.setMaximized(true);
            stage.show();
            simulationThread.start();
        });
    }

    private File openFileExplorerAndGetFileType() {
        // Tworzenie FileChooser
        FileChooser fileChooser = new FileChooser();

        // Dodanie filtrów rozszerzeń plików (opcjonalne)
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        // Otwieranie eksploratora plików
        Stage stage = (Stage) createGame.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
           return file;
        }

        return null; // Jeśli użytkownik nie wybrał pliku
    }

    private void exportConfigurationFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));


            // Otwórz okno wyboru zapisu
            Stage stage = (Stage) exportConfiguration.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("Parameter,Value"); // Nagłówki pliku CSV
                    writer.println("MapHeight," + formMapHeight.getText());
                    writer.println("MapWidth," + formMapWidth.getText());
                    writer.println("PlantStart," + formPlantStart.getText());
                    writer.println("PlantDaily," + formPlantDaily.getText());
                    writer.println("PlantEnergy," + formPlantEnergy.getText());
                    writer.println("AnimalStart," + formAnimalStart.getText());
                    writer.println("AnimalStartEnergy," + formAnimalStartEnergy.getText());
                    writer.println("AnimalEnergyReproductionDepletion," + formAnimalEnergyReproductionDepletion.getText());
                    writer.println("AnimalEnergyDailyDepletion," + formAnimalEnergyDailyDepletion.getText());
                    writer.println("AnimalEnergyToReproduce," + formAnimalEnergyToReproduce.getText());
                    writer.println("AnimalMutationMinimum," + formAnimalMutationMinimum.getText());
                    writer.println("AnimalMutationMaximum," + formAnimalMutationMaximum.getText());
                    writer.println("AnimalGenotypeLength," + formAnimalGenotypeLength.getText());
                }

                // Wyświetl komunikat powodzenia
                showAlert("Success", "Export Complete", "Configuration exported to: " + file.getPath(), Alert.AlertType.INFORMATION);
            }
        } catch (IOException e) {
            showAlert("Error", "Export Failed", "An error occurred while saving the configuration file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void importConfigurationFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage) importConfiguration.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            HashMap<String, String> formValues = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true; // Flaga, aby pominąć pierwszą linię (nagłówki)

                while ((line = reader.readLine()) != null) {
                    // Pomiń pierwszą linię z nagłówkami
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // Walidacja formatowania linii (dokładnie jeden przecinek)
                    if (!line.contains(",") || line.split(",", -1).length != 2) {
                        showAlert("Error", "Import Failed", "Invalid format in file. Each line must contain exactly one comma separating key and value.", Alert.AlertType.ERROR);
                        return;
                    }

                    String[] args = line.split(",", 2);
                    String key = args[0].trim();   // Klucz (Property)
                    String value = args[1].trim(); // Wartość (Value)

                    // Sprawdź, czy wartość jest liczbą (opcjonalne, w zależności od specyfikacji)
                    if (value.isEmpty() || !value.matches("\\d+")) {
                        showAlert("Error", "Import Failed", "Invalid value for key: " + key + ". Value must be a positive integer.", Alert.AlertType.ERROR);
                        return;
                    }

                    // Dodanie danych do mapy (używamy klucza bez żadnych zmian)
                    formValues.put(key, value);
                }

                // Przygotuj dane do formularza
                try {
                    fillFormWorldConfigParamsWithDirectMapping(formValues);
                    showAlert("Success", "Import Complete", "The configuration has been successfully imported.", Alert.AlertType.INFORMATION);
                } catch (InvalidKeyException e) {
                    showAlert("Error", "Import Failed", "Invalid key in configuration file: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Import Failed", "An error occurred while reading the file: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void validateStartParameters(SimulationParameters parameters) throws InvalidGameParamsException {
        if (parameters.width() <= 0 || parameters.height() <= 0 || parameters.width() > 200 || parameters.height() > 200) {
            throw new InvalidGameParamsException();
        }
        if (parameters.startPlantNumber() < 0 || parameters.plantsPerDay() < 0 || parameters.singlePlantEnergy() < 0) {
            throw new InvalidGameParamsException();
        }
        if (parameters.startAnimalNumber() <= 0 || parameters.startAnimalEnergy() <= 0) {
            throw new InvalidGameParamsException();
        }
        if (parameters.copulatingCost() <= 0 || parameters.livingCost() <= 0 || parameters.wellFedValue() <= 0) {
            throw new InvalidGameParamsException();
        }
        if (parameters.minMutationNum() < 0 || parameters.maxMutationNum() <= 0 || parameters.genomLength() <= 0) {
            throw new InvalidGameParamsException();
        }
        if (parameters.minMutationNum() > parameters.maxMutationNum() || parameters.maxMutationNum() > parameters.genomLength()) {
            throw new InvalidGameParamsException();
        }
        if (parameters.startAnimalNumber() > parameters.width() * parameters.height()) {
            throw new InvalidGameParamsException();
        }
    }

    private SimulationParameters getWorldConfigFromParams() {
        return new SimulationParameters(
                Integer.parseInt(formMapHeight.getText()),
                Integer.parseInt(formMapWidth.getText()),
                Integer.parseInt(formPlantStart.getText()),
                Integer.parseInt(formPlantDaily.getText()),
                Integer.parseInt(formPlantEnergy.getText()),
                Integer.parseInt(formAnimalStart.getText()),
                Integer.parseInt(formAnimalStartEnergy.getText()),
                Integer.parseInt(formAnimalEnergyReproductionDepletion.getText()),
                Integer.parseInt(formAnimalEnergyDailyDepletion.getText()),
                Integer.parseInt(formAnimalEnergyToReproduce.getText()),
                Integer.parseInt(formAnimalMutationMinimum.getText()),
                Integer.parseInt(formAnimalMutationMaximum.getText()),
                Integer.parseInt(formAnimalGenotypeLength.getText())
        );
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void fillFormWorldConfigParamsWithDirectMapping(HashMap<String, String> formValues) throws InvalidKeyException {
        for (Map.Entry<String, String> value : formValues.entrySet()) {
            switch (value.getKey()) {
                case "MapHeight":
                    formMapHeight.setText(value.getValue());
                    break;
                case "MapWidth":
                    formMapWidth.setText(value.getValue());
                    break;
                case "PlantStart":
                    formPlantStart.setText(value.getValue());
                    break;
                case "PlantDaily":
                    formPlantDaily.setText(value.getValue());
                    break;
                case "PlantEnergy":
                    formPlantEnergy.setText(value.getValue());
                    break;
                case "AnimalStart":
                    formAnimalStart.setText(value.getValue());
                    break;
                case "AnimalStartEnergy":
                    formAnimalStartEnergy.setText(value.getValue());
                    break;
                case "AnimalEnergyReproductionDepletion":
                    formAnimalEnergyReproductionDepletion.setText(value.getValue());
                    break;
                case "AnimalEnergyDailyDepletion":
                    formAnimalEnergyDailyDepletion.setText(value.getValue());
                    break;
                case "AnimalEnergyToReproduce":
                    formAnimalEnergyToReproduce.setText(value.getValue());
                    break;
                case "AnimalMutationMinimum":
                    formAnimalMutationMinimum.setText(value.getValue());
                    break;
                case "AnimalMutationMaximum":
                    formAnimalMutationMaximum.setText(value.getValue());
                    break;
                case "AnimalGenotypeLength":
                    formAnimalGenotypeLength.setText(value.getValue());
                    break;
                default:
                    throw new InvalidKeyException("Unexpected key found in configuration file: " + value.getKey());
            }
        }
    }
}


