package oop.model;

public class Plant {
    private final Vector2d plantPosition;
    private final int energy;

    public Plant(Vector2d plantPosition, int energy) {
        this.plantPosition = plantPosition;
        this.energy = energy;
    }

    public Vector2d getPosition() {
        return plantPosition;
    }

    public int getPlantEnergy() {
        return energy;
    }
}
