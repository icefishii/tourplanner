package dev.icefish.tourplanner.client.services;

import dev.icefish.tourplanner.client.model.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TourLogServiceTemp {

    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public ObservableList<TourLog> getAllTourLogs() {
        return tourLogs;
    }

    public void createNewTourLog(TourLog tourLog) {
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