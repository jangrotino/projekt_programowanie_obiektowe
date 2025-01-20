package oop.model.util;
import oop.model.Boundary;
import oop.model.RectangularMap;
import oop.model.Vector2d;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GrassGenerator {
    private static final double INCREASED_PROBABILITY_THRESHOLD = 0.8;

    public static List<Vector2d> generateGrass(int width, int height, int grassNum, List<Vector2d> excludedPositions, RectangularMap map) {
        grassNum = Math.min(grassNum, width * height - excludedPositions.size());
        if (grassNum <= 0) {
            return Collections.emptyList();
        }

        List<Vector2d> possiblePositions = generatePossiblePositions(width, height, excludedPositions);
        List<Vector2d> nearEquator = new ArrayList<>();
        List<Vector2d> farFromEquator = new ArrayList<>();
        categorizePositions(possiblePositions, map, nearEquator, farFromEquator);

        int increasedProbCount = calculateIncreasedProbCount(grassNum);
        List<Vector2d> increasedPositions = generateRandomSample(Math.min(increasedProbCount, nearEquator.size()), nearEquator);
        List<Vector2d> decreasedPositions = generateRandomSample(grassNum - Math.min(increasedProbCount, nearEquator.size()), farFromEquator);
        List<Vector2d> grassPositions = new ArrayList<>(increasedPositions);
        grassPositions.addAll(decreasedPositions);
        return grassPositions;
    }

    private static int calculateIncreasedProbCount(int grassNum) {
        return (int) generateRandomUniformValues(grassNum).stream()
                .filter(prob -> prob <= INCREASED_PROBABILITY_THRESHOLD)
                .count();
    }

    private static List<Vector2d> generateRandomSample(int n, List<Vector2d> positions) {
        n = Math.min(n, positions.size());
        Collections.shuffle(positions, new Random());
        return new ArrayList<>(positions.subList(0, n));
    }

    private static void categorizePositions(List<Vector2d> possiblePositions, RectangularMap map,
                                            List<Vector2d> nearEquatorList, List<Vector2d> farFromEquatorList) {
        Boundary equatorBoundary = map.getEquatorBoundary();
        for (Vector2d position : possiblePositions) {
            if (position.follows(equatorBoundary.lowerBound()) && position.precedes(equatorBoundary.upperBound())) {
                nearEquatorList.add(position);
            } else {
                farFromEquatorList.add(position);
            }
        }
    }

    private static List<Double> generateRandomUniformValues(int n) {
        Random random = new Random();
        return random.doubles(n, 0.0, 1.0)
                .boxed()
                .collect(Collectors.toList());
    }

    private static List<Vector2d> generatePossiblePositions(int maxWidth, int maxHeight, List<Vector2d> excludedPositions) {
        Set<Vector2d> possiblePositions = IntStream.range(0, maxWidth)
                .boxed()
                .flatMap(x -> IntStream.range(0, maxHeight)
                        .mapToObj(y -> new Vector2d(x, y)))
                .collect(Collectors.toSet());
        possiblePositions.removeAll(new HashSet<>(excludedPositions));
        return new ArrayList<>(possiblePositions);
    }
}