package oop.model.util;

import oop.model.Vector2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final List<Vector2d> allPositions;
    private final int count;

    public RandomPositionGenerator(int maxWidth, int maxHeight, int count) {
        if (count > maxWidth * maxHeight) {
            throw new IllegalArgumentException("Count cannot be greater than the total number of positions.");
        }
        this.count = count;
        this.allPositions = generateAllPositions(maxWidth, maxHeight);
        Collections.shuffle(allPositions);
    }

    private List<Vector2d> generateAllPositions(int maxWidth, int maxHeight) {
        return IntStream.range(0, maxWidth)
                .boxed()
                .flatMap(x -> IntStream.range(0, maxHeight)
                        .mapToObj(y -> new Vector2d(x, y)))
                .collect(Collectors.toList());
    }


    @Override
    public Iterator<Vector2d> iterator() {
        return new Iterator<Vector2d>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < count; // Only allow 'count' positions
            }

            @Override
            public Vector2d next() {
                if (!hasNext()) {
                    throw new UnsupportedOperationException("No more positions to generate");
                }
                return allPositions.get(currentIndex++); // Return the next position and increment the index
            }
        };
    }
}

