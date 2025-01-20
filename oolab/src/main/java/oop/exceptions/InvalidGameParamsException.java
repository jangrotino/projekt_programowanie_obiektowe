package oop.exceptions;

public class InvalidGameParamsException extends Exception {
    public InvalidGameParamsException() {
        super("Invalid game parameters.");
    }
}