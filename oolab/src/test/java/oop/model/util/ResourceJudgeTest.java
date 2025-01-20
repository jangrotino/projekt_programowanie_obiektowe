package oop.model.util;

import oop.model.Animal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceJudgeTest {

    @Test
    void judgeSingleElementTest() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));

        List<Animal> result = ResourceJudge.judge(animals, false);

        assertEquals(1, result.size());
    }

    @Test
    void judgeSingleElementWithEqualTests() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));

        List<Animal> result = ResourceJudge.judge(animals, false);

        assertEquals(1, result.size());
    }

    @Test
    void judgePairTests() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));

        List<Animal> result = ResourceJudge.judge(animals, true);

        assertEquals(2, result.size());
    }

    @Test
    void judgePairWithEqualTests() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));
        animals.add(new Animal(UUID.randomUUID(), null, null, 5, null, 0));

        List<Animal> result = ResourceJudge.judge(animals, true);

        assertEquals(2, result.size());
    }
}