package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.ObservableList;

public class TourAttributeHelper {

    public static int computePopularity(ObservableList<TourLog> tourLogs) {
        return tourLogs.size(); // Number of logs determines popularity
    }

    public static String computeChildFriendliness(ObservableList<TourLog> tourLogs, Tour tour) {
        double averageDifficulty = tourLogs.stream()
                .mapToInt(TourLog::getDifficulty)
                .average()
                .orElse(0);
        double totalDistance = tour.getDistance();
        double totalTime = tour.getEstimatedTime();

        if (averageDifficulty <= 2 && totalDistance <= 5 && totalTime <= 2) {
            return "Child-Friendly";
        } else {
            return "Not Child-Friendly";
        }
    }
}