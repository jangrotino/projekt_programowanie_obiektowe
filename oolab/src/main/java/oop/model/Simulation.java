package oop.model;

public class Simulation {

    private final SimulationParameters parameters;
    private final RectangularMap world;
    private volatile boolean running;
    private Thread simulationThread;

    public Simulation(int height, int width, int startPlantNumber, int plantsPerDay, int singlePlantEnergy,
                      int startAnimalNumber, int startAnimalEnergy, int copulatingCost, int livingCost,
                      int wellFedValue, int minMutationNum, int maxMutationNum, int genomLength) {

        parameters = new SimulationParameters(height, width, startPlantNumber, plantsPerDay, singlePlantEnergy,
                startAnimalNumber, startAnimalEnergy, copulatingCost, livingCost, wellFedValue, minMutationNum, maxMutationNum, genomLength);
        world = new RectangularMap(parameters);
        running = false;
    }

    public SimulationParameters getParameters() {
        return parameters;
    }

    public RectangularMap getWorld() {
        return world;
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        running = true;
        simulationThread = new Thread(() -> {
            try {
                world.populateWithPlants();
                world.populateWithAnimals();
                while (running) {
                    world.deathAnimalsRemoval();
                    world.movingAllAnimals();
                    world.eating();
                    world.breedingAnimals();
                    world.plantsGrowing();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        simulationThread.start();
    }

    public void stop() {
        running = false;
        if (simulationThread != null && simulationThread.isAlive()) {
            try {
                simulationThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void nextStep() {
        world.deathAnimalsRemoval();
        world.movingAllAnimals();
        world.eating();
        world.breedingAnimals();
        world.plantsGrowing();
    }
}