package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.ObservableList;

import java.util.Objects;

//zum Berechnen der Attribute, damit man es noch Easy ändern kann falls man es anders
public class TourAttributeHelper {

    public static int computePopularity(ObservableList<TourLog> tourLogs) {
        if (tourLogs.isEmpty()) {
            return 0;
        }
        double averageRating = tourLogs.stream()
                .mapToInt(TourLog::getRating)
                .average()
                .orElse(0);
        return (int) Math.round(averageRating);
    }

    public static String computeChildFriendliness(ObservableList<TourLog> tourLogs, Tour tour) {
        double averageDifficulty = tourLogs.stream()
                .mapToInt(TourLog::getDifficulty)
                .average()
                .orElse(0);
        double totalDistance = tour.getDistance();
        double totalTime = tour.getEstimatedTime();

        if (Objects.equals(tour.getTransportType(), "Car")) {
            return "Child-Friendly";
        }

        if (averageDifficulty <= 2 && totalDistance <= 20 && totalTime <= 2) {
            return "Child-Friendly";
        } else {
            return "Not Child-Friendly";
        }
    }
}