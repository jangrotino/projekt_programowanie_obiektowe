package oop.model.util;

import oop.model.Boundary;
import oop.model.RectangularMap;
import oop.model.Vector2d;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GrassGenerator {
    private List<Vector2d> nearEquator = new ArrayList<>();
    private List<Vector2d> farFromEquator = new ArrayList<>();

    public List<Vector2d> generateGrass(int width, int height, int grassNum, List<Vector2d> excludedPositions, RectangularMap map) {
        if (grassNum > excludedPositions.size()) {
            throw new IllegalArgumentException("n cannot be greater than the size of the range");
        }

        List<Vector2d> possiblePositions = this.generatePossiblePositions(width, height, excludedPositions);
        this.splitPositions(possiblePositions, map);
        List<Double> uniformSample = generateRandomUniformValues(grassNum);
        int increasedProbCnt = 0;

        for(Double elem : uniformSample) {
            if(elem <= 0.8) {
                increasedProbCnt++;
            }
        }

        List<Vector2d> increasedPositions = generateRandomSample(increasedProbCnt, nearEquator);
        List<Vector2d> decreasedPositions = generateRandomSample(grassNum - increasedProbCnt, farFromEquator);

        return increasedPositions.addAll(decreasedPositions);
    }

    private  List<Vector2d> generateRandomSample(int n, List<Vector2d> positions) {
        Set<Vector2d> sample = new HashSet<>();
        Random random = new Random();

        int rangeSize = positions.size();
        for (int i = rangeSize - n; i < rangeSize; i++) {
            int t = random.nextInt(i + 1);

            Vector2d selectedValue = positions.get(t);

            if (sample.contains(selectedValue)) {
                sample.add(positions.get(i));
            } else {
                sample.add(selectedValue);
            }
        }

        return sample.stream().toList();
    }

    private List<Double> generateRandomUniformValues(int n) {
        Random random = new Random();
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            values.add(random.nextDouble()); // Generates a value between 0.0 (inclusive) and 1.0 (exclusive)
        }

        return values;
    }

    private void splitPositions(List<Vector2d> possiblePositions, RectangularMap map) {
        Boundary boundary = map.getEquatorBoundary();

        for (Vector2d possiblePosition : possiblePositions) {
            if(possiblePosition.precedes(boundary.lowerBound()) && possiblePosition.follows(boundary.upperBound())) {
                nearEquator.add(possiblePosition);
            }
            else {
                farFromEquator.add(possiblePosition);
            }
        }
    }

    private List<Vector2d> generatePossiblePositions(int maxWidth, int maxHeight, List<Vector2d> excludedPositions) {
        HashSet<Vector2d> possiblePositions = IntStream.range(0, maxWidth)
                .boxed()
                .flatMap(x -> IntStream.range(0, maxHeight)
                        .mapToObj(y -> new Vector2d(x, y)))
                .collect(Collectors.toCollection(HashSet::new));

        // Remove excluded positions from the set
        for(Vector2d elem : excludedPositions) {
           possiblePositions.remove(elem);
        }

        // Return the remaining positions as a List
        return possiblePositions.stream().collect(Collectors.toList());
    }

}
