package dev.icefish.tourplanner.models.exceptions;

public class ControllerException extends RuntimeException {
    public ControllerException(String message) {
        super(message);
    }
}
