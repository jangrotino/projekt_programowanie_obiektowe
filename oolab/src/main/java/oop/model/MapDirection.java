package oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    private static final List<MapDirection> directions = List.of(MapDirection.values());

    @Override
    public String toString() {
        return switch(this) {
            case NORTH -> "N";
            case NORTH_EAST -> "NE";
            case SOUTH_WEST -> "SW";
            case SOUTH_EAST -> "SE";
            case SOUTH -> "S";
            case WEST -> "W";
            case EAST -> "E";
            case NORTH_WEST -> "NW";
        };
    }

    public static MapDirection getDirection(int num) {
        if (num < 0 || num >= directions.size()) {
            throw new IllegalArgumentException("Invalid direction index");
        }
        return directions.get(num);
    }

    public static MapDirection getRandomDirection() {
        Random random = new Random();

        int min = 0;
        int max = directions.size() - 1;
        int randomNum = random.nextInt(max - min + 1) + min;

        return directions.get(randomNum);
    }

    public static MapDirection reverseDirection(MapDirection direction) {
        return switch(direction) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case NORTH_EAST -> SOUTH_WEST;
            case SOUTH_WEST -> NORTH_EAST;
            case NORTH_WEST -> SOUTH_EAST;
            case SOUTH_EAST -> NORTH_WEST;
        };
    }

    public Vector2d toUnitVector() {
        return switch(this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTH_WEST -> new Vector2d(-1, 1);
        };
    }
}

