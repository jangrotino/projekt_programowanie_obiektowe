package oop.model;


public interface MoveValidator {

        /**
         * Indicate if any object can move to the given position.
         *
         * @param position
         *            The position checked for the movement possibility.
         * @return True if the object can move to that position.
         */
        Vector2d positionCorrector(Vector2d position);
        MapDirection directionCorrector(MapDirection direction, Vector2d position);
}

