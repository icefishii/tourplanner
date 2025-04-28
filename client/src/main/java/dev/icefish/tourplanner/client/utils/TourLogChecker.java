package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/*
 * This was created  with the help of ChatGPT Model-4o
 */

public class TourLogChecker {
    public static Map<String, String> validateTourLogRaw(
            Tour tour, LocalDate date, String time, String comment,
            String difficulty, String distance, String duration, String rating) {

        Map<String, String> errors = new HashMap<>();

        if (tour == null) {
            errors.put("tour", "Tour is not selected!");
        }
        if (date == null) {
            errors.put("date", "Date is not selected!");
        }
        if (time == null || time.isBlank()) {
            errors.put("time", "Time is empty!");
        } else {
            try {
                LocalTime.parse(time);
            } catch (Exception e) {
                errors.put("time", "Invalid time format! Use HH:mm.");
            }
        }

        if (comment == null || comment.trim().isEmpty()) {
            errors.put("comment", "Comment is empty!");
        }

        if (difficulty == null || difficulty.isBlank()) {
            errors.put("difficulty", "Difficulty is empty!");
        } else {
            try {
                int diff = Integer.parseInt(difficulty);
                if (diff < 1 || diff > 5)
                    errors.put("difficulty", "Difficulty must be between 1 and 5!");
            } catch (NumberFormatException e) {
                errors.put("difficulty", "Difficulty must be a number!");
            }
        }

        if (distance == null || distance.isBlank()) {
            errors.put("distance", "Distance is empty!");
        } else {
            try {
                double dist = Double.parseDouble(distance);
                if (dist <= 0)
                    errors.put("distance", "Distance must be greater than 0!");
            } catch (NumberFormatException e) {
                errors.put("distance", "Distance must be a number!");
            }
        }

        if (duration == null || duration.trim().isEmpty()) {
            errors.put("duration", "Duration is empty!");
        }

        if (rating == null || rating.isBlank()) {
            errors.put("rating", "Rating is empty!");
        } else {
            try {
                int r = Integer.parseInt(rating);
                if (r < 1 || r > 5)
                    errors.put("rating", "Rating must be between 1 and 5!");
            } catch (NumberFormatException e) {
                errors.put("rating", "Rating must be a number!");
            }
        }

        return errors;
    }
}