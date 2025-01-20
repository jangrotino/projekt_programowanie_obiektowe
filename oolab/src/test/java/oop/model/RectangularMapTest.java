package oop.model;

import oop.model.util.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RectangularMapTest {

    private RectangularMap map;
    private SimulationParameters parameters;

    @BeforeEach
    void setUp() {
        parameters = new SimulationParameters(10, 10, 5, 5, 5, 0,
                5, 3, 1, 7, 2, 4, 8);
        map = new RectangularMap(parameters);
    }

    @Test
    void testBoundaryInitialization() {
        Boundary boundary = map.getCurrentBounds();
        assertEquals(new Vector2d(0, 0), boundary.lowerBound());
        assertEquals(new Vector2d(9, 9), boundary.upperBound());
    }

    @Test
    void testEquatorBoundary() {
        Boundary equator = map.getEquatorBoundary();
        System.out.println(equator.lowerBound());
        System.out.println(equator.upperBound());
        assertNotNull(equator);
        assertTrue(equator.upperBound().x() > equator.lowerBound().x());
        assertTrue(equator.upperBound().y() > equator.lowerBound().y());
    }

    @Test
    void testAnimalPlacement() {
        Vector2d position = new Vector2d(5, 5);
        Animal animal = new Animal(UUID.randomUUID(), MapDirection.NORTH, position, 20, Genom.getRandomGenom(5), 0);
        Animal animal2 = new Animal(UUID.randomUUID(), MapDirection.NORTH, position, 20, Genom.getRandomGenom(5), 0);
        map.placeAnimal(position, animal);
        map.placeAnimal(position, animal2);

        assertTrue(map.getAnimals().containsKey(position));
        assertTrue(map.getAnimals().get(position).contains(animal));
        map.placeAnimal(position, animal2);
        assertTrue(map.getAnimals().get(position).contains(animal2));
    }

    @Test
    void testPlantPlacement() {
        Vector2d position = new Vector2d(2, 3);
        map.placePlant(position, 10);

        assertTrue(map.getPlants().containsKey(position));
        assertEquals(10, map.getPlants().get(position).getPlantEnergy());
    }

    @Test
    void testPositionCorrectionWithinBoundary() {
        //valid position
        Vector2d position = new Vector2d(5, 5);
        Vector2d correctedPosition = map.positionCorrector(position);
        assertEquals(new Vector2d(5, 5), correctedPosition);

        //north edge exceeded
        Vector2d northEdge = new Vector2d(10, 11);
        Vector2d correctedNorthEdge = map.positionCorrector(northEdge);
        assertEquals(new Vector2d(9, 9), correctedNorthEdge);

        //south edge exceeded
        Vector2d southEdge = new Vector2d(10, -1);
        Vector2d correctedSouthEdge = map.positionCorrector(southEdge);
        assertEquals(new Vector2d(9, 0), correctedSouthEdge);

        //left edge exceeded
        Vector2d leftEdge = new Vector2d(-1, 8);
        Vector2d correctedLeftEdge = map.positionCorrector(leftEdge);
        assertEquals(new Vector2d(9, 8), correctedLeftEdge);

        //right edge exceeded
        Vector2d rightEdge = new Vector2d(11, 8);
        Vector2d correctedRightEdge = map.positionCorrector(rightEdge);
        assertEquals(new Vector2d(0, 8), correctedRightEdge);

        //upper right corner
        Vector2d upperRightCorner = new Vector2d(11, 11);
        Vector2d correctedUpperRightCorner = map.positionCorrector(upperRightCorner);
        assertEquals(new Vector2d(9, 9), correctedUpperRightCorner);

        //lower right corner
        Vector2d lowerRightCorner = new Vector2d(11, -1);
        Vector2d correctedLowerRightCorner = map.positionCorrector(lowerRightCorner);
        assertEquals(new Vector2d(9, 0), correctedLowerRightCorner);

        //lower left corner
        Vector2d lowerLeftCorner = new Vector2d(-1, -1);
        Vector2d correctedLowerLeftCorner = map.positionCorrector(lowerLeftCorner);
        assertEquals(new Vector2d(0, 0), correctedLowerLeftCorner);

        //upper left corner
        Vector2d upperLeftCorner = new Vector2d(-1, 11);
        Vector2d correctedUpperLeftCorner = map.positionCorrector(upperLeftCorner);
        assertEquals(new Vector2d(0, 9), correctedUpperLeftCorner);
    }

    @Test
    void testAnimalEating() {
        // many animals same parameters (draw case)
        Vector2d plantPosition = new Vector2d(3, 3);
        map.placePlant(plantPosition, parameters.singlePlantEnergy());
        List<Animal> animals = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            animals.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, plantPosition, 20, Genom.getRandomGenom(5), 0));
            map.placeAnimal(plantPosition, animals.get(i));
        }
        map.eating();
        int cnt = 0;
        for(Animal animal : animals) {
            if(animal.getEnergy() == 25) {
                cnt += 1;
            }
        }
        assertTrue(map.getPlants().isEmpty());
        assertEquals(1, cnt);

        // three animals rivalization test by energy
        Vector2d plantPosition2 = new Vector2d(2, 2);
        map.placePlant(plantPosition2, parameters.singlePlantEnergy());
        List<Animal> animals2 = new ArrayList<>();
        for(int i = 0; i < 3 ; i++) {
            animals2.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, plantPosition2, i+1, Genom.getRandomGenom(5), 0));
            map.placeAnimal(plantPosition2, animals2.get(i));
        }
        map.eating();
        assertTrue(map.getPlants().isEmpty());
        assertEquals(8, animals2.getLast().getEnergy());

        // three animals rivalization test by age
        Vector2d plantPosition3 = new Vector2d(1, 1);
        map.placePlant(plantPosition3, parameters.singlePlantEnergy());
        List<Animal> animals3 = new ArrayList<>();
        for(int i = 0; i < 3 ; i++) {
            animals3.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, plantPosition3, Genom.getRandomGenom(5), 0, 1, i+1, 0));
            map.placeAnimal(plantPosition3, animals3.get(i));
        }
        map.eating();
        assertTrue(map.getPlants().isEmpty());
        System.out.println(animals3.getLast().getAge());
        assertEquals(6, animals3.getLast().getEnergy());

        // three animals rivalization test by children number
        Vector2d plantPosition4 = new Vector2d(0, 0);
        map.placePlant(plantPosition4, parameters.singlePlantEnergy());
        List<Animal> animals4 = new ArrayList<>();
        for(int i = 0; i < 3 ; i++) {
            animals4.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, plantPosition4, Genom.getRandomGenom(5), 0, 0, 0, i+1));
            map.placeAnimal(plantPosition4, animals4.get(i));
        }
        map.eating();
        assertTrue(map.getPlants().isEmpty());
        assertEquals(5, animals4.getLast().getEnergy());
    }

    @Test
    void testDirectionCorrector() {
        Vector2d vector = new Vector2d(10, 11);
        MapDirection direction = MapDirection.NORTH;

        assertEquals(MapDirection.SOUTH, map.directionCorrector(direction, vector));

        vector = new Vector2d(10, -1);
        direction = MapDirection.SOUTH;
        assertEquals(MapDirection.NORTH, map.directionCorrector(direction, vector));
    }

    @Test
    void testAnimalMovement() {
        Vector2d initialPosition1 = new Vector2d(1, 1);
        Animal animal1 = new Animal(UUID.randomUUID(), MapDirection.EAST, initialPosition1, 20, new Genom(new ArrayList<>(Arrays.asList(1, 4, 7, 3, 2))), 0);
        Animal animal2 = new Animal(UUID.randomUUID(), MapDirection.EAST, initialPosition1, 20, new Genom(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5))), 0);
        map.placeAnimal(initialPosition1, animal1);
        map.placeAnimal(initialPosition1, animal2);

        for(int i = 0; i < parameters.genomLength(); i++) {
            map.movingAllAnimals();
        }
        System.out.println(animal1.getPosition());
        System.out.println(animal2.getPosition());
        assertEquals(MapDirection.NORTH_WEST, animal1.getDirection());
        assertEquals(new Vector2d(5, 1), animal1.getPosition());
        assertEquals(MapDirection.SOUTH_EAST, animal2.getDirection());
        assertEquals(new Vector2d(2, 0), animal2.getPosition());
    }

    @RepeatedTest(10)
    void testAnimalBreeding() {

        //base case
        Vector2d position = new Vector2d(5, 5);

        Animal parent1 = new Animal(UUID.randomUUID(), MapDirection.EAST, position, 50, Genom.getRandomGenom(5), 0);
        Animal parent2 = new Animal(UUID.randomUUID(), MapDirection.NORTH, position, 50, Genom.getRandomGenom(5), 0);

        map.placeAnimal(position, parent1);
        map.placeAnimal(position, parent2);
        map.breedingAnimals();
        assertEquals(3, map.getAnimals().get(position).size());

        //animals with same parameters
        Vector2d position2 = new Vector2d(4, 4);
        List<Animal> animals = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            animals.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, position2, 20, Genom.getRandomGenom(5), 0));
            map.placeAnimal(position2, animals.get(i));
        }
        map.breedingAnimals();
        assertEquals(6, map.getAnimals().get(position2).size());

        //animals with various parameters
        Vector2d position3 = new Vector2d(3, 3);
        List<Animal> animals2 = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            animals2.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, position3, i+8, Genom.getRandomGenom(5), 0));
            map.placeAnimal(position3, animals2.get(i));
        }
        map.breedingAnimals();
        assertEquals(6, map.getAnimals().get(position3).size());
        assertEquals(9, animals2.get(animals2.size()-1).getEnergy());

        //animals below couplatingCost
        Vector2d position4 = new Vector2d(2, 2);
        List<Animal> animals3 = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            animals3.add(new Animal(UUID.randomUUID(), MapDirection.NORTH, position4, parameters.copulatingCost()-1, Genom.getRandomGenom(5), 0));
            map.placeAnimal(position4, animals3.get(i));
        }
        map.breedingAnimals();
        assertEquals(5, map.getAnimals().get(position4).size());
    }

    @Test
    void testDeathAnimalsRemoval() {

        //base case
        Vector2d position = new Vector2d(4, 4);

        Animal deadAnimal = new Animal(UUID.randomUUID(), MapDirection.NORTH, position, 0, Genom.getRandomGenom(5), 0);
        Animal aliveAnimal = new Animal(UUID.randomUUID(), MapDirection.SOUTH, position, 10, Genom.getRandomGenom(5), 0);

        map.placeAnimal(position, deadAnimal);
        map.placeAnimal(position, aliveAnimal);

        map.deathAnimalsRemoval();

        assertFalse(map.getAnimals().get(position).contains(deadAnimal));
        assertTrue(map.getAnimals().get(position).contains(aliveAnimal));

        //all animals all death

        Vector2d positionNew = new Vector2d(3, 3);

        Animal deadAnimal2 = new Animal(UUID.randomUUID(), MapDirection.NORTH, positionNew, 0, Genom.getRandomGenom(5), 0);
        Animal deadAnimal1 = new Animal(UUID.randomUUID(), MapDirection.SOUTH, positionNew, 0, Genom.getRandomGenom(5), 0);

        map.placeAnimal(positionNew, deadAnimal2);
        map.placeAnimal(positionNew, deadAnimal1);

        map.deathAnimalsRemoval();
        assertFalse(map.getAnimals().containsKey(positionNew));

    }



    @Test
    void testPlantsGrowing() {
        // one cycle
        map.plantsGrowing();
        assertEquals(parameters.startPlantNumber(),  map.getPlants().size());

        int cycles = (parameters.width() * parameters.height()) / parameters.plantsPerDay() + 1;
        // cycles to fulfill map
        for(int i = 0; i < cycles; i++) {
            map.plantsGrowing();
        }
        assertEquals(parameters.width() * parameters.height(),  map.getPlants().size());
    }
}