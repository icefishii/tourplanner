package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.client.model.Tour;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TourLogChecker {
    public static Map<String, String> validateTourLog(Tour tour, Timestamp date, String comment, int difficulty, double distance, Duration duration, int rating) {
        Map<String, String> errors = new HashMap<>();

        if (tour == null) {
            errors.put("tour", "Tour is not selected!");
        }
        if (date == null) {
            errors.put("date", "Date is empty!");
        }
        if (comment == null || comment.trim().isEmpty()) {
            errors.put("comment", "Comment is empty!");
        }
        if (difficulty < 1 || difficulty > 5) {
            errors.put("difficulty", "Difficulty must be between 1 and 5!");
        }
        if (distance <= 0) {
            errors.put("distance", "Distance must be greater than 0!");
        }
        if (duration == null || duration.isNegative() || duration.isZero()) {
            errors.put("duration", "Duration must be positive!");
        }
        if (rating < 1 || rating > 5) {
            errors.put("rating", "Rating must be between 1 and 5!");
        }

        return errors;
    }
    public static Map<String, String> validateTourLogWithStringDuration(
            Tour tour, Timestamp date, String comment, int difficulty,
            double distance, String duration, int rating) {

        Map<String, String> errors = new HashMap<>();

        if (tour == null) {
            errors.put("tour", "Tour is not selected!");
        }
        if (date == null) {
            errors.put("date", "Date is empty!");
        }
        if (comment == null || comment.trim().isEmpty()) {
            errors.put("comment", "Comment is empty!");
        }
        if (difficulty < 1 || difficulty > 5) {
            errors.put("difficulty", "Difficulty must be between 1 and 5!");
        }
        if (distance <= 0) {
            errors.put("distance", "Distance must be greater than 0!");
        }
        if (duration == null || duration.trim().isEmpty()) {
            errors.put("duration", "Duration is empty!");
        }
        if (rating < 1 || rating > 5) {
            errors.put("rating", "Rating must be between 1 and 5!");
        }

        return errors;
    }
}