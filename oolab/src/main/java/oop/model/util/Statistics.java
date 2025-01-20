package oop.model.util;

import oop.model.RectangularMap;
import oop.model.StatisticsListener;
import oop.model.Vector2d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics implements StatisticsListener {
    private int day = 0;
    private int allLivingAnimalsNumber = 0;
    private int animalsNumber = 0;
    private int plantsNumber = 0;
    private int energySumAlive = 0;
    private int lifeSpanSum = 0;
    private int childrenSum = 0;
    private int freeFields = 0;
    private final SortedOccurrenceSet<Genom> popularGenoms = new SortedOccurrenceSet<>();

    public Statistics(int animalsNumber, int plantsNumber, int freeFields, int energySumAlive, int allLivingAnimalsNumber) {
        this.animalsNumber = animalsNumber;
        this.plantsNumber = plantsNumber;
        this.freeFields = freeFields;
        this.energySumAlive = energySumAlive;
        this.allLivingAnimalsNumber = allLivingAnimalsNumber;
    }

    public int getDay() {
        return day;
    }

    public int getPlantsNumber() {
        return plantsNumber;
    }

    public int getAnimalsNumber() {
        return animalsNumber;
    }

    public int getFreeFields() {
        return freeFields;
    }

    public SortedOccurrenceSet<Genom> getPopularGenoms() {
        return popularGenoms;
    }

    public void updateStatistics(UpdateType updateType, int value) {
        switch (updateType) {
            case ANIMAL -> animalsNumber=Math.max(value+animalsNumber,0);
            case PLANT -> plantsNumber=Math.max(value,0);
            case ENERGY -> energySumAlive=Math.max(value+energySumAlive,0);
            case AGE -> lifeSpanSum=Math.max(value+lifeSpanSum,0);
            case CHILD -> childrenSum=Math.max(value+childrenSum,0);
            case FIELD -> freeFields=Math.max(value,0);
            case ALL_ANIMALS -> allLivingAnimalsNumber=Math.max(value+allLivingAnimalsNumber,0);
            case DAYS_PASSED -> day+=value;
            default -> throw new IllegalArgumentException("Invalid updateType");
        }
    }

    public void updateStatistics(UpdateType updateType, Genom genom) {
        if (updateType ==  UpdateType.GENOM) {
            popularGenoms.addOrUpdate(genom);
        }
    }

    private double avgEnergyForLivingAnimals() {
        if (animalsNumber == 0) {
            return 0;
        }
        double result = (double) energySumAlive / animalsNumber;
        return Math.round(result*100) / 100.0;
    }

    private double avgLifeSpanForAnimals() {
        if (allLivingAnimalsNumber - animalsNumber == 0) {
            return 0;
        }
        double result = (double) lifeSpanSum / (allLivingAnimalsNumber - animalsNumber);
        return Math.round(result * 100) / 100.0; // Zaokrąglanie do dwóch miejsc po przecinku
    }

    private double avgChildrenForLivingAnimals() {
        if (animalsNumber == 0) {
            return 0;
        }
        double result = (double) childrenSum / allLivingAnimalsNumber;
        return Math.round(result * 100) / 100.0; // Zaokrąglanie do dwóch miejsc po przecinku
    }

    private String topGenom() {
        if (popularGenoms.getFirst() == null)
            return "brak";
        return popularGenoms.getFirst().toString();
    }

    private int topGenomCount() {
        if (popularGenoms.getFirst() == null)
            return 0;

        return popularGenoms.getCount(popularGenoms.getFirst());
    }

    @Override
    public String toString() {
        return "=== Statistics ===\n" +
                "Day: " + day + "\n" +
                "Number of Animals: " + animalsNumber + "\n" +
                "Number of Plants: " + plantsNumber + "\n" +
                "Free Fields: " + freeFields + "\n" +
                "Top Genom: " + topGenom() + "\n" +
                "Top Genom Count: " + topGenomCount() + "\n" +
                "Average Energy for Animals: " + avgEnergyForLivingAnimals() + "\n" +
                "Average Life Span for Animals: " + avgLifeSpanForAnimals() + "\n" +
                "Average Children per Living Animal: " + avgChildrenForLivingAnimals() + "\n";
    }

    public String getCsvFormat() {
        return String.format(
                "%d,%d,%d,%d,%s,%d,%.2f,%.2f,%.2f",
                day,          // Nr dnia
                animalsNumber,           // Liczba zwierząt
                plantsNumber,            // Liczba roślin
                freeFields,            // Łączna energia
                topGenom(),
                topGenomCount(),
                avgChildrenForLivingAnimals(),
                avgLifeSpanForAnimals(),
                avgChildrenForLivingAnimals()// Średnia długość życia
        );
    }
}
