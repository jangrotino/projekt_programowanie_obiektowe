package oop.model.presenter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import oop.model.*;
import oop.model.util.Genom;
import oop.model.util.Statistics;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class GamePresenter extends BasePresenter implements MapChangeListener {
    @FXML
    private GridPane mapGrid;
    @FXML
    private TextArea statisticsArea;

    @FXML
    private TextArea animalTracking;

    private RectangularMap map;
    private Simulation simulation;
    private final int tileSize = 20;
    private Statistics statistics;
    private boolean isStatisticsExportEnabled = false;
    private boolean isAnimalTrackingEnabled = false;
    Animal trackedAnimal;
    File file;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setStatisticsExportEnabled(boolean isStatisticsExportEnabled) {
        this.isStatisticsExportEnabled = isStatisticsExportEnabled;
    }

    public void setFile(File file) {
        this.file = file;
        String header = "Day,AnimalsNumber,PlantsNumber,FreeFields,TopGenome,TopGenomeCount,AvgChildren,AvgLifeSpan,AvgEnergy";
        try {
            writeDataToCsvFile(file, header);
        } catch (IOException e) {
            // Rejestrowanie błędu w konsoli
            System.err.println("Error while writing data to CSV file: " + e.getMessage());

            // Wyświetlenie komunikatu użytkownikowi
            showAlert(
                    "Error",
                    "File Write Failed",
                    "An error occurred while writing data to the file: " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void initialize() {
        if (simulation == null) {
            return;
        }
        map = simulation.getWorld();
        map.subscribeMapChangeListener(this);
        statistics = new Statistics(
                simulation.getParameters().startAnimalNumber(),
                simulation.getParameters().startPlantNumber(),
                (simulation.getParameters().height() + 1) * (simulation.getParameters().width() + 1),
                simulation.getParameters().startAnimalEnergy() * simulation.getParameters().startAnimalNumber(),
                simulation.getParameters().startAnimalNumber()
        );
        map.subscribeStatisticsListener(statistics);

        // Display initial statistics
        appendToStatistics(statistics.toString());
        renderMap();
    }

    @FXML
    private void handleStartSimulation() {
        simulation.run();
    }

    @FXML
    private void handleStopSimulation() {
        simulation.stop();
    }

    @FXML
    private void handleShowMostPopularFields() {
        Platform.runLater(() -> {
            handleStopSimulation();
            clearMap();
            renderMap();
            renderPopularFields();
        });
    }

    private void renderPopularFields() {
        HashMap<Vector2d, Integer> popularField = map.getPlantsFavPositions();

        for (int x = 0; x < simulation.getParameters().width(); x++) {
            for (int y = 0; y < simulation.getParameters().height(); y++) {
                Vector2d position = new Vector2d(x, y);

                if (popularField.containsKey(position)) {
                    // Pobranie wartości dla danego pola
                    int count = popularField.get(position);

                    // Obliczenie intensywności koloru (im większy count, tym ciemniejszy kolor)
                    double intensity = (double) count / (statistics.getDay() + 1); // Wartość między 0.0 a 1.0
                    Color color = Color.color(0, 1 - intensity, 0); // Zielony z mniejszym jasnością (ciemny przy większym count)

                    // Tworzenie prostokąta z dynamicznym kolorem
                    Rectangle tile = new Rectangle(tileSize, tileSize, color);

                    // Umieszczenie elementu w odpowiedniej pozycji w GridPane
                    GridPane.setColumnIndex(tile, x);
                    GridPane.setRowIndex(tile, y);
                    mapGrid.getChildren().add(tile);
                }
            }
        }
    }

    @FXML
    private void handleShowPopularGenom() {
        Platform.runLater(() -> {
            handleStopSimulation();
            clearMap();
            renderMap();
            renderPopularGenoms();
        });
    }

    private void renderPopularGenoms() {
        List<Genom> topThreeGenoms = statistics.getPopularGenoms().getTopThree();
        ConcurrentHashMap<Vector2d, ConcurrentSkipListSet<Animal>> animals = map.getAnimals();

        for (Vector2d position : animals.keySet()) {
            ConcurrentSkipListSet<Animal> animalsAtPosition = animals.get(position);
            for (Animal animal : animalsAtPosition) {
                for (Genom genom : topThreeGenoms) {
                    if(animal.getGenom().equals(genom)) {
                        Circle tile = new Circle(tileSize / 2.0, Color.BLUE);
                        GridPane.setColumnIndex(tile, position.x());
                        GridPane.setRowIndex(tile, position.y());
                        mapGrid.getChildren().add(tile);
                        break;
                    }
                }
            }
        }
    }

    private void appendToStatistics(String text) {
        Platform.runLater(() -> {
            statisticsArea.clear();
            statisticsArea.appendText(text + "\n");
        });
    }

    private String animalStats() {
        return String.format(
                "Selected Animal:\n" +
                        "Position: %s\n" +
                        "Energy: %d\n" +
                        "Genom: %s\n" +
                        "Children Count: %d\n" +
                        "Days Lived: %d\n" +
                        "Plants Eaten: %d\n",
                trackedAnimal.getPosition(),
                trackedAnimal.getEnergy(),
                trackedAnimal.getGenom().toString(),
                trackedAnimal.getChildNumber(),
                trackedAnimal.getAge(),
                trackedAnimal.getEatenPlants()
        );
    }

    private void appendToAnimalTracking() {
        Platform.runLater(() -> {
            animalTracking.clear();
            animalTracking.appendText(animalStats());
        });
    }

    @FXML
    private void endTracking() {
        isAnimalTrackingEnabled = false;
        animalTracking.clear();
    }
    @Override
    public void mapChanged(RectangularMap changedMap, String message) {
        if ("MAP_UPDATE".equals(message)) {
            Platform.runLater(() -> {
                clearMap();
                renderMap();
                appendToStatistics(statistics.toString());
                renderPlants();
                if(isAnimalTrackingEnabled) {
                    appendToAnimalTracking();
                }
                if(isStatisticsExportEnabled) {
                    try {
                        writeDataToCsvFile(file, statistics.getCsvFormat());
                    } catch (IOException e) {
                        // Rejestrowanie błędu w konsoli
                        System.err.println("Error while writing data to CSV file: " + e.getMessage());

                        // Wyświetlenie komunikatu użytkownikowi
                        showAlert(
                                "Error",
                                "File Write Failed",
                                "An error occurred while writing data to the file: " + e.getMessage(),
                                Alert.AlertType.ERROR
                        );
                    }
                }
                renderAnimals();
            });
        }
    }

    private void clearMap() {
        mapGrid.getChildren().clear();
    }

    private void renderMap() {
        for (int x = 0; x < simulation.getParameters().width(); x++) {
            for (int y = 0; y < simulation.getParameters().height(); y++) {
                Rectangle tile = new Rectangle(tileSize, tileSize, Color.LIGHTGRAY);
                tile.setStroke(Color.DARKGRAY);
                GridPane.setColumnIndex(tile, x);
                GridPane.setRowIndex(tile, y);
                mapGrid.getChildren().add(tile);
            }
        }
    }

    private void renderAnimals() {
        map.getAnimals().forEach((position, animalsAtPosition) -> {
            if (!animalsAtPosition.isEmpty()) {
                Animal animalToRender = animalsAtPosition.iterator().next();
                Circle animalTile = new Circle(tileSize / 2.0, getColorBasedOnEnergy(animalsAtPosition.iterator().next().getEnergy()));
                animalTile.setOnMouseClicked(event -> {
                    handleAnimalSelected(animalToRender); // Wywołanie metody wyboru zwierzęcia
                });
                GridPane.setColumnIndex(animalTile, position.x());
                GridPane.setRowIndex(animalTile, position.y());
                mapGrid.getChildren().add(animalTile);
            }
        });
    }

    private Color getColorBasedOnEnergy(int energy) {
        double ratio = Math.min((double) energy / simulation.getParameters().startAnimalEnergy(), 1.0);

        double redComponent = (1 - ratio);
        double greenComponent = ratio * 0.2;
        double blueComponent = ratio * 0.2;

        return new Color(redComponent, greenComponent, blueComponent, 1.0);
    }

    private void renderPlants() {
        map.getPlants().keySet().forEach(position -> {
            Rectangle plantTile = new Rectangle(tileSize, tileSize, Color.GREEN);
            GridPane.setColumnIndex(plantTile, position.x());
            GridPane.setRowIndex(plantTile, position.y());
            mapGrid.getChildren().add(plantTile);
        });
    }

    private void handleAnimalSelected(Animal animal) {
        // Sprawdzenie, czy symulacja jest zatrzymana
        if (simulation.isRunning()) {
            showAlert("Simulation Running", null, "Please stop the simulation to select an animal.", Alert.AlertType.WARNING);
            return;
        }
        isAnimalTrackingEnabled = true;
        trackedAnimal = animal;
        System.out.println(isAnimalTrackingEnabled);
        // Wyświetlenie informacji o wybranym zwierzęciu w polu 'animalTracking'
    }

    public void writeDataToCsvFile(File file, String data) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        // Tworzenie obiektu FileWriter w trybie dodawania (append) do pliku
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            // Zapis danych do pliku, a następnie nowej linii
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }
}