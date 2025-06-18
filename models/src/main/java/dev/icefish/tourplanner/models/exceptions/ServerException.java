package dev.icefish.tourplanner.models.exceptions;

public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
}
