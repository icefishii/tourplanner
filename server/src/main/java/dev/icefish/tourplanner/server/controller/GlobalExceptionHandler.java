package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.exceptions.RepositoryException;
import dev.icefish.tourplanner.models.exceptions.ServiceException;
import dev.icefish.tourplanner.models.exceptions.ControllerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<String> handleRepositoryException(RepositoryException ex) {
        logger.error("Repository error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<String> handleServiceException(ServiceException ex) {
        logger.error("Service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(ControllerException.class)
    public ResponseEntity<String> handleControllerException(ControllerException ex) {
        logger.error("Controller error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}