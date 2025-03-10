package oop.model.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StatisticsTest {

    @Test
    public void testUpdateStatisticsInt() {
        // Initialize Statistics
        Statistics statistics = new Statistics(2, 3, 4, 5, 6);

        //Update statistics of Plant
        statistics.updateStatistics(UpdateType.PLANT, 2);
        assertEquals(2, statistics.getPlantsNumber());

        //Update statistics of Animals
        statistics.updateStatistics(UpdateType.ANIMAL, 3);
        assertEquals(5, statistics.getAnimalsNumber());

        //Update statistics of Field
        statistics.updateStatistics(UpdateType.FIELD, 2);
        assertEquals(2, statistics.getFreeFields());


    }

    @Test
    public void testUpdateStatisticsGenom() {
        // Initialize Statistics
        Statistics statistics = new Statistics(2, 3, 4, 5, 6);
        Genom genom1 = Genom.getRandomGenom(5);

        //Update popular genoms
        statistics.updateStatistics(UpdateType.GENOM, genom1);
        assertEquals(genom1, statistics.getPopularGenoms().getFirst());
    }
}