package dev.icefish.tourplanner.client.services;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class TourLogService {

    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public ObservableList<TourLog> getAllTourLogs() {
        return null;
    }

    public ObservableList<TourLog> getTourLogsfromTour(UUID tourId) {
        return null;
    }

    public void createNewTourLog(TourLog tourLog) {

    }

    public void deleteTourLog(TourLog tourLog) {

    }

    public void updateTourLog(TourLog tourLog) {

    }
}