package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.models.TourLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class TourLogViewModel {

    private final ObservableList<TourLog> tourLogsList;
    private final TourLogService tourLogService;

    public TourLogViewModel(TourLogService tourLogService) {
        this.tourLogService = tourLogService;
        this.tourLogsList = FXCollections.observableArrayList(tourLogService.getAllTourLogs());
    }

    private final BooleanProperty deleteTourLogButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty editTourLogButtonDisabled = new SimpleBooleanProperty(true);

    public BooleanProperty deleteTourLogButtonDisabledProperty() { return deleteTourLogButtonDisabled; }
    public BooleanProperty editTourLogButtonDisabledProperty() { return editTourLogButtonDisabled; }

    public void updateTourLogButtonStates(ObservableList<TourLog> selectedTourLogs) {
        int count = selectedTourLogs.size();
        deleteTourLogButtonDisabled.set(count == 0);
        editTourLogButtonDisabled.set(count != 1);
    }

    public void fetchTourLogsFromServer() {
        this.tourLogsList.setAll(tourLogService.getAllTourLogs());
    }

    public ObservableList<TourLog> getAllTourLogs() {
        return this.tourLogsList;
    }

    public ObservableList<TourLog> getTourLogsByTourId(UUID tourId) {
        if (tourId == null) {
            System.out.println("Tour ID is null, returning empty list.");
            return FXCollections.observableArrayList();
        }
        System.out.println("Fetching TourLogs for Tour ID: " + tourId);
        return FXCollections.observableArrayList(tourLogService.getTourLogsfromTour(tourId));
    }

    public void createNewTourLog(TourLog tourLog) {
        System.out.println("TourLog Created: " + tourLog);
        tourLogService.createNewTourLog(tourLog);
        tourLogsList.add(tourLog);
    }

    public void deleteTourLog(TourLog tourLog) {
        tourLogService.deleteTourLog(tourLog);
        tourLogsList.remove(tourLog);
    }

    public void updateTourLog(TourLog tourLog) {
        tourLogService.updateTourLog(tourLog);
    }

    public ObservableList<TourLog> searchTourLogs(String searchText, ObservableList<TourLog> currentTourLogs) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return currentTourLogs; // Return all logs if search text is empty
        }

        ObservableList<TourLog> filteredTourLogs = FXCollections.observableArrayList();
        searchText = searchText.toLowerCase().trim(); // Normalize search text

        for (TourLog tourLog : currentTourLogs) {
            if (searchText.startsWith("comment:")) {
                String commentFilter = searchText.substring(8).trim(); // Extract and trim after "comment:"
                if (tourLog.getComment().toLowerCase().contains(commentFilter)) {
                    filteredTourLogs.add(tourLog);
                }
            } else if (searchText.startsWith("difficulty:")) {
                String difficultyFilter = searchText.substring(10).trim();
                if (String.valueOf(tourLog.getDifficulty()).contains(difficultyFilter)) {
                    filteredTourLogs.add(tourLog);
                }
            } else if (searchText.startsWith("distance:")) {
                String distanceFilter = searchText.substring(9).trim();
                if (String.valueOf(tourLog.getDistance()).contains(distanceFilter)) {
                    filteredTourLogs.add(tourLog);
                }
            } else if (searchText.startsWith("duration:")) {
                String durationFilter = searchText.substring(9).trim();
                if (tourLog.getDurationText().toLowerCase().contains(durationFilter)) {
                    filteredTourLogs.add(tourLog);
                }
            } else if (searchText.startsWith("rating:")) {
                String ratingFilter = searchText.substring(7).trim();
                if (String.valueOf(tourLog.getRating()).contains(ratingFilter)) {
                    filteredTourLogs.add(tourLog);
                }
            } else {
                // Default full-text search
                if (tourLog.getComment().toLowerCase().contains(searchText) ||
                        String.valueOf(tourLog.getDifficulty()).contains(searchText) ||
                        String.valueOf(tourLog.getDistance()).contains(searchText) ||
                        tourLog.getDurationText().toLowerCase().contains(searchText) ||
                        String.valueOf(tourLog.getRating()).contains(searchText)) {
                    filteredTourLogs.add(tourLog);
                }
            }
        }
        return filteredTourLogs;
    }
}