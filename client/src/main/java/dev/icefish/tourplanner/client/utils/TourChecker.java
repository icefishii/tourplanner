package dev.icefish.tourplanner.client.utils;

import java.util.HashMap;
import java.util.Map;

public class TourChecker {
      public static Map<String, String> validateTour(String name, String description, String fromLocation, String toLocation, String transportType) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Tourname is empty!");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Description is empty!");
        }
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            errors.put("fromLocation", "From-Location is empty!");
        }
        if (toLocation == null || toLocation.trim().isEmpty()) {
            errors.put("toLocation", "To-Location is empty!");
        }
        if (transportType == null || transportType.trim().isEmpty()) {
            errors.put("transportType", "Transportation type is empty!");
        }

        return errors; // Gibt eine Map mit allen Fehlern zurück
    }
}
