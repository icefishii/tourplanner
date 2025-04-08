package dev.icefish.tourplanner.client.services;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class TourLogServiceTemp {

    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public ObservableList<TourLog> getAllTourLogs() {
        return tourLogs;
    }

    public ObservableList<TourLog> getTourLogsfromTour(UUID tourId) {
        if (tourId == null) {
            return FXCollections.observableArrayList(); // Return an empty list if tourId is null
        }
        ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();
        for (TourLog tourLog : this.tourLogs) {
            if (tourId.equals(tourLog.getTourId())) {
                tourLogs.add(tourLog);
            }
        }
        return tourLogs;
    }

    public void createNewTourLog(TourLog tourLog) {
        System.out.println("Adding TourLog to ViewModel: " + tourLog + ", Tour ID: " + tourLog.getTourId());
        tourLogs.add(tourLog);
    }

    public void deleteTourLog(TourLog tourLog) {
        tourLogs.remove(tourLog);
    }

    public void updateTourLog(TourLog tourLog) {
        for (int i = 0; i < tourLogs.size(); i++) {
            if (tourLogs.get(i).getId().equals(tourLog.getId())) {
                tourLogs.set(i, tourLog);
                break;
            }
        }
    }
}