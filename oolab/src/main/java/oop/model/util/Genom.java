package oop.model.util;

import java.util.*;
import java.util.stream.Collectors;

public class Genom {
    private final List<Integer> genom;
    private static final int maxGeneNum = 7;

    public Genom(List<Integer> genom) {
        this.genom = genom;
    }

    public List<Integer> getGenom() {
        return genom;
    }

    public int getGene(int index) {
        return genom.get(index);
    }

    public int getGenomSize() {
        return genom.size();
    }

    public static Genom genomForChild(Genom father, Genom mother, int fatherEnergy, int motherEnergy) {
        int totalEnergy = fatherEnergy + motherEnergy;
        int percentage = (int) ((double) fatherEnergy / totalEnergy * 100);
        int genomPartLength = (int) ((percentage / 100.0) * father.getGenom().size());

        Random random = new Random();
        List<Integer> childGenom = new ArrayList<>();
        int randomSplit = random.nextBoolean() ? 1 : 0; // 0 for mother, 1 for father

        if (randomSplit == 1) {
            for (int i = 0; i < genomPartLength; i++) {
                childGenom.add(father.getGene(i));
            }

            for (int i = genomPartLength; i < mother.getGenom().size(); i++) {
                childGenom.add(mother.getGene(i));
            }
        } else {
            for (int i = 0; i < mother.getGenom().size() - genomPartLength; i++) {
                childGenom.add(mother.getGene(i));
            }

            for (int i = mother.getGenom().size() - genomPartLength; i < father.getGenom().size(); i++) {
                childGenom.add(father.getGene(i));
            }
        }

        return new Genom(childGenom);
    }

    @Override
    public String toString() {
        return genom.toString();
    }

    public static Genom getRandomGenom(int genomLength) {
        List<Integer> randomList = new Random().ints(genomLength, 0, 8)
                .boxed()
                .collect(Collectors.toList());
        return new Genom(randomList);
    }

    public static int getRandomGene(int genomLength) {
        Random random = new Random();
        return random.nextInt(genomLength);
    }

    public static Genom genomMutation(Genom genom, int minMutateGene, int maxMutateGene) {
        int numGenesToMutate = new Random().nextInt(maxMutateGene - minMutateGene + 1) + minMutateGene;
        Set<Integer> mutatedIndices = new HashSet<>();
        Random random = new Random();

        while (mutatedIndices.size() < numGenesToMutate) {
            int randIndex = random.nextInt(genom.getGenom().size());
            mutatedIndices.add(randIndex);
        }

        List<Integer> mutatedGenom = new ArrayList<>(genom.getGenom());

        for (int index : mutatedIndices) {
            int pom = random.nextInt(maxGeneNum + 1);
            while (pom == genom.getGene(index)) {
                pom = random.nextInt(maxGeneNum + 1);
            }
            mutatedGenom.set(index, pom);
        }
        return new Genom(mutatedGenom);
    }

    @Override
    public int hashCode() {
        // Hash code oparty na liście genom
        return Objects.hash(genom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Sprawdzenie referencji
        if (o == null || getClass() != o.getClass()) return false; // Sprawdzenie klasy

        // Rzutowanie do Genom i porównanie list genom
        Genom otherGenom = (Genom) o;
        return Objects.equals(genom, otherGenom.genom);
    }
}