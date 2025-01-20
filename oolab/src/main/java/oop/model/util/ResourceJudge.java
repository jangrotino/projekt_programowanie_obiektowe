package oop.model.util;

import oop.model.Animal;

import java.util.List;
import java.util.Random;

public class ResourceJudge {
    private static final int DEFAULT_START_INDEX = 1;

    public static List<Animal> judge(List<Animal> elements, boolean pairOfElement) {
        if (elements.isEmpty() || elements.size() == 1) {
            return elements;
        }

        int startIndex = calculateStartIndex(elements);
        int equalCount = calculateEqualCount(elements, startIndex);
        Random random = new Random();

        if (!pairOfElement) {
            return handleSingleElementSelection(elements, equalCount, random);
        }

        return handlePairSelection(elements, equalCount, startIndex, random);
    }

    private static int calculateStartIndex(List<Animal> elements) {
        return elements.get(0).equalsCustom(elements.get(1)) ? DEFAULT_START_INDEX : 2;
    }

    private static int calculateEqualCount(List<Animal> elements, int startIndex) {
        int count = (startIndex == 2) ? 1 : 0;
        for (int i = startIndex; i < elements.size(); i++) {
            if (elements.get(i - 1).equalsCustom(elements.get(i))) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private static List<Animal> handleSingleElementSelection(List<Animal> elements, int equalCount, Random random) {
        if (equalCount > 1) {
            int randomIndex = random.nextInt(equalCount + 1);
            return List.of(elements.get(randomIndex));
        }
        return List.of(elements.get(0));
    }

    private static List<Animal> handlePairSelection(List<Animal> elements, int equalCount, int startIndex, Random random) {
        if (equalCount > 2) {
            if (startIndex == 1) {
                return generateUniqueRandomIndices(elements, equalCount, random);
            } else {
                return generateUniquePairIncludingFirst(elements, equalCount, random);
            }
        }
        return List.of(elements.get(0), elements.get(1));
    }

    private static List<Animal> generateUniqueRandomIndices(List<Animal> elements, int equalCount, Random random) {
        int index1 = random.nextInt(equalCount + 1);
        int index2;
        do {
            index2 = random.nextInt(equalCount + 1);
        } while (index1 == index2);
        return List.of(elements.get(index1), elements.get(index2));
    }

    private static List<Animal> generateUniquePairIncludingFirst(List<Animal> elements, int equalCount, Random random) {
        int index;
        do {
            index = random.nextInt(equalCount + 1);
        } while (index == 0);
        return List.of(elements.get(0), elements.get(index));
    }
}