package oop.model;

import oop.model.util.Genom;

import java.util.*;

import static java.lang.Math.max;

public class Animal {
    private UUID id;
    private MapDirection direction;
    private Vector2d position;
    private int energy;
    private int age = 0;
    private int childNumber = 0;
    private Genom genom;
    private int curr_gene;
    private boolean isDead = false;
    private int eatenPlants = 0;

    public Animal() {

    }

    public Animal(UUID id, MapDirection direction, Vector2d position, int energy, Genom genom, int curr_gene) {
        this.id = id;
        this.position = position;
        this.direction = direction;
        this.energy = energy;
        this.genom = genom;
        this.curr_gene = curr_gene;
    }

    public Animal(UUID id, MapDirection direction, Vector2d position, Genom genom, int curr_gene, int energy, int age, int childNumber) {
        this.id = id;
        this.direction = direction;
        this.energy = energy;
        this.genom = genom;
        this.curr_gene = curr_gene;
        this.age = age;
        this.childNumber = childNumber;
        this.position = position;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public int getChildNumber() {
        return childNumber;
    }

    public int getEnergy() {
         return energy;
    }

    public void eating(int energy) {
        this.energy += energy;
        this.eatenPlants += 1;
    }

    public void subtractEnergy(int energy) {
        this.energy = Math.max(this.energy - energy, 0);
        if (this.energy == 0) {
            isDead = true;
        }
    }

    public int getAge() {
        return age;
    }

    public Vector2d getPosition() {
        return position;
    }

    public Genom getGenom() {
        return genom;
    }

    public int getEatenPlants() {
        return eatenPlants;
    }

    @Override
    public String toString() {
        return direction.toString();
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public void move(MoveValidator validator) {
        Vector2d unitVec = this.direction.toUnitVector();
        Vector2d newPosition = this.position.add(unitVec);
        this.position = validator.positionCorrector(newPosition);
        this.direction = validator.directionCorrector(MapDirection.getDirection(genom.getGene(curr_gene)), newPosition);
        curr_gene = (curr_gene + 1) % genom.getGenomSize();
        if (!isDead)
            age += 1;
    }

    public static Animal breeding(Animal father, Animal mother, SimulationParameters parameters) {
        father.subtractEnergy(parameters.copulatingCost());
        mother.subtractEnergy(parameters.copulatingCost());
        father.childNumber += 1;
        mother.childNumber += 1;
        Genom childGenom = Genom.genomMutation(Genom.genomForChild(father.genom, mother.genom, father.energy, mother.energy), parameters.minMutationNum(), parameters.maxMutationNum());
        return new Animal(UUID.randomUUID(), MapDirection.getRandomDirection(), father.getPosition(), parameters.startAnimalEnergy(), childGenom, Genom.getRandomGene(parameters.genomLength()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id);
    }

    public boolean equalsCustom(Animal animal) {
        return this.energy == animal.energy && this.age == animal.age && this.childNumber == animal.childNumber;
    }

    public static final Comparator<Animal> ANIMAL_COMPARATOR = Comparator
            .comparingInt(Animal::getEnergy) // Najpierw sortowanie wg energii (malejąco)
            .thenComparingInt(Animal::getAge)  // Następnie sortowanie wg wieku (malejąco)
            .thenComparingInt(Animal::getChildNumber) // Następnie liczba potomków (malejąco)
            .thenComparing(animal -> animal.id).reversed();        // Ostateczny porządek wg UUID

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}