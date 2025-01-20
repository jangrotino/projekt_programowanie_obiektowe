package oop.model;
import oop.model.util.*;

import javax.print.DocFlavor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;


public class RectangularMap implements MoveValidator {
    private final Boundary boundary;
    private final ConcurrentHashMap<Vector2d, ConcurrentSkipListSet<Animal>> animals = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Vector2d, Plant> plants = new ConcurrentHashMap<>();
    private final HashMap<Vector2d, Integer> plantsFavPositions = new HashMap<>();
    private final Boundary equatorBoundary;
    private static final double coverage = 0.2;
    private final SimulationParameters parameters;
    private List<MapChangeListener> mapChangeListeners = new ArrayList<>();
    private List<StatisticsListener> statisticsListeners = new ArrayList<>();

    public RectangularMap(SimulationParameters parameters) {
        boundary = new Boundary(new Vector2d(0, 0), new Vector2d(parameters.width()-1, parameters.height()-1));
        equatorBoundary = calculateEquator(parameters.width(), parameters.height());
        this.parameters = parameters;
    }

    public void subscribeMapChangeListener(MapChangeListener listener) {
        mapChangeListeners.add(listener);
    }

    public void unsubscribeMapChangeListener(MapChangeListener listener) {
        mapChangeListeners.remove(listener);
    }

    public void subscribeStatisticsListener(StatisticsListener listener) {
        statisticsListeners.add(listener);
    }
    public void unsubscribeStatisticsListener(StatisticsListener listener) {
        statisticsListeners.remove(listener);
    }

    private void fireMapUpdateEvent() {
        mapChanged("MAP_UPDATE");
    }

    private void mapChanged(String message) {
        for(MapChangeListener listener : mapChangeListeners) {
            listener.mapChanged(this, message);
        }
    }

    private void updateStatistics(UpdateType updateType, int value) {
        for(StatisticsListener listener : statisticsListeners) {
            listener.updateStatistics(updateType, value);
        }
    }

    private void updateStatistics(UpdateType updateType, Genom genom) {
        for(StatisticsListener listener : statisticsListeners) {
            listener.updateStatistics(updateType, genom);
        }
    }

    public void populateWithAnimals() {
        RandomPositionGenerator randomPositions = new RandomPositionGenerator(parameters.width(), parameters.height(), parameters.startAnimalNumber());
        for(Vector2d position : randomPositions) {
            Animal animal = new Animal(UUID.randomUUID(), MapDirection.getRandomDirection(), position, parameters.startAnimalEnergy(), Genom.getRandomGenom (parameters.genomLength()), 0);
            updateStatistics(UpdateType.GENOM, animal.getGenom());
            this.placeAnimal(position, animal);
        }
    }

    public void populateWithPlants() {
        RandomPositionGenerator randomPositions = new RandomPositionGenerator(parameters.width(), parameters.height(), parameters.startPlantNumber());
        for(Vector2d position : randomPositions) {
            this.placePlant(position, parameters.singlePlantEnergy());
            updateStatistics(UpdateType.PLANT, 1);
        }
    }

    public List<Vector2d> getPlantsPositions() {
        return plants.keySet().stream().toList();
    }

    public Boundary getCurrentBounds() {
        return boundary;
    }

    public HashMap<Vector2d, Integer> getPlantsFavPositions() {
        return plantsFavPositions;
    }

    public ConcurrentHashMap<Vector2d, Plant> getPlants() {
        return plants;
    }

    public ConcurrentHashMap<Vector2d, ConcurrentSkipListSet<Animal>> getAnimals() {
        return animals;
    }

    private Boundary calculateEquator(int width, int height) {
        width += 1;
        height += 1;

        int totalArea = width * height;
        int centralArea = (int) Math.round(coverage * totalArea);

        int centerX = Math.ceilDiv(height, 2);
        int pom = Math.ceilDiv(Math.ceilDiv(centralArea, width), 2);

        int lowerBound_X = 0;
        int lowerBound_Y =  centerX - pom + 1;
        int upperBound_X = width - 1;
        int upperBound_Y = centerX + pom;

        return new Boundary(new Vector2d(lowerBound_X, lowerBound_Y), new Vector2d(upperBound_X, upperBound_Y));
    }

    public Boundary getEquatorBoundary() {
        return equatorBoundary;
    }

    @Override
    public Vector2d positionCorrector(Vector2d position) {
        if (position.y() > boundary.upperBound().y()) {
            position = position.lowerLeft(boundary.upperBound());
            position = position.upperRight(boundary.lowerBound());
            return new Vector2d(position.x(), boundary.upperBound().y());
        }
        else if (position.y() < boundary.lowerBound().y()) {
            position = position.lowerLeft(boundary.upperBound());
            position = position.upperRight(boundary.lowerBound());
            return new Vector2d(position.x(), boundary.lowerBound().y());
        }
        else if(position.x() < boundary.lowerBound().x()) {
            return new Vector2d(boundary.upperBound().x(), position.y());
        }
        else if (position.x() > boundary.upperBound().x()) {
            return new Vector2d(boundary.lowerBound().x(), position.y());
        }

        return position;
    }

    public void deathAnimalsRemoval() {
        Iterator<Map.Entry<Vector2d, ConcurrentSkipListSet<Animal>>> iterator = animals.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Vector2d, ConcurrentSkipListSet<Animal>> entry = iterator.next();
            ConcurrentSkipListSet<Animal> animalSet = entry.getValue();

            // Check if the set is null or empty
            if (animalSet == null || animalSet.isEmpty()) {
                iterator.remove();
                continue;
            }

            // Remove animals with zero energy
            while (!animalSet.isEmpty() && animalSet.last().getEnergy() == 0) {
                updateStatistics(UpdateType.ANIMAL, -1);
                updateStatistics(UpdateType.AGE, animalSet.last().getAge());
                updateStatistics(UpdateType.CHILD, -animalSet.last().getChildNumber());
                animalSet.pollLast();
            }

            // Remove the map entry if the set becomes empty
            if (animalSet.isEmpty()) {
                iterator.remove();
            }
        }
        fireMapUpdateEvent();
    }


    public void eating() {
        for (Vector2d position : animals.keySet()) {
            if (plants.containsKey(position)) {
                ConcurrentSkipListSet<Animal> animalSet = animals.get(position);
                List<Animal> candidatesToEat = ResourceJudge.judge(new ArrayList<>(animalSet), false);

                Animal chosenAnimal = candidatesToEat.get(0);
                if (animalSet.contains(chosenAnimal)) {
                    animalSet.remove(chosenAnimal);
                    chosenAnimal.eating(parameters.singlePlantEnergy());
                    animalSet.add(chosenAnimal);
                }
                updateStatistics(UpdateType.PLANT, -1);
                updateStatistics(UpdateType.ENERGY, parameters.singlePlantEnergy());
                plants.remove(position);
            }
        }
        fireMapUpdateEvent();
    }

    public void movingAllAnimals() {
        Map<Vector2d, ConcurrentSkipListSet<Animal>> animalsToAdd = new HashMap<>();

        Iterator<Map.Entry<Vector2d, ConcurrentSkipListSet<Animal>>> iterator = animals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Vector2d, ConcurrentSkipListSet<Animal>> entry = iterator.next();
            Vector2d currentPosition = entry.getKey();
            ConcurrentSkipListSet<Animal> currentAnimals = entry.getValue();

            List<Animal> movedAnimals = new ArrayList<>();

            for (Animal animal : currentAnimals) {
                animal.move(this);
                animal.subtractEnergy(parameters.livingCost());
                Vector2d newPosition = animal.getPosition();
                animalsToAdd.computeIfAbsent(newPosition, k -> new ConcurrentSkipListSet<>(Animal.ANIMAL_COMPARATOR)).add(animal);
                movedAnimals.add(animal);
                updateStatistics(UpdateType.ENERGY, -parameters.livingCost());
            }
            currentAnimals.removeIf(movedAnimals::contains);

            if (currentAnimals.isEmpty()) {
                iterator.remove();
            }
        }

        for (Map.Entry<Vector2d, ConcurrentSkipListSet<Animal>> entry : animalsToAdd.entrySet()) {
            Vector2d newPosition = entry.getKey();
            ConcurrentSkipListSet<Animal> newAnimals = entry.getValue();

            animals.merge(newPosition, newAnimals, (existingSet, addedSet) -> {
                existingSet.addAll(addedSet);
                return existingSet;
            });
        }

        fireMapUpdateEvent();
    }

    public void breedingAnimals() {
        for (Vector2d position : animals.keySet()) {
            ConcurrentSkipListSet<Animal> animalTreeSet = animals.get(position);
            if (animalTreeSet.size() < 2) {
                continue;
            }
            List<Animal> animalCandidate = ResourceJudge.judge(new ArrayList<>(animalTreeSet), true);
            if (animalCandidate.get(0).getEnergy() < parameters.copulatingCost() || animalCandidate.get(1).getEnergy() < parameters.copulatingCost()) {
                continue;
            }
            Animal animalChild = Animal.breeding(animalCandidate.get(0), animalCandidate.get(1), parameters);
            this.placeAnimal(position, animalChild);
            updateStatistics(UpdateType.ANIMAL, 1);
            updateStatistics(UpdateType.GENOM, animalChild.getGenom());
            updateStatistics(UpdateType.ENERGY, -2*parameters.copulatingCost());
            updateStatistics(UpdateType.ENERGY, animalChild.getEnergy());
            updateStatistics(UpdateType.CHILD, animalCandidate.get(0).getChildNumber() + animalCandidate.get(1).getChildNumber() + 2);
            updateStatistics(UpdateType.ALL_ANIMALS, 1);
        }
        fireMapUpdateEvent();
    }

    public MapDirection directionCorrector(MapDirection direction, Vector2d position) {
        if (position.y() > boundary.upperBound().y() || position.y() < boundary.lowerBound().y()) {
            return MapDirection.reverseDirection(direction);
        }
        return direction;
    }

    public void placeAnimal(Vector2d position, Animal animal) {
        animals.computeIfAbsent(position, k -> new ConcurrentSkipListSet<>(Animal.ANIMAL_COMPARATOR));
        animals.get(position).add(animal);
    }

    public void placePlant(Vector2d position, int energy) {
        plants.putIfAbsent(position, new Plant(position, energy));
    }

    public void plantsGrowing() {
        List<Vector2d> newPlants = GrassGenerator.generateGrass(parameters.width(), parameters.height(), parameters.plantsPerDay(), this.getPlantsPositions(), this);
        for (Vector2d plants : newPlants) {
            this.placePlant(plants, parameters.singlePlantEnergy());
            plantsFavPositions.put(plants, plantsFavPositions.getOrDefault(plants, 0) + 1);
        }
        updateStatistics(UpdateType.PLANT, plants.size());
        updateStatistics(UpdateType.FIELD, (parameters.width()) * (parameters.height()) - plants.size());
        updateStatistics(UpdateType.DAYS_PASSED, 1);
        fireMapUpdateEvent();
    }
}
