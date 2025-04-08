package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.services.TourLogServiceTemp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class TourLogViewModel {

    private final ObservableList<TourLog> tourLogsList;
    private final TourLogServiceTemp tourLogService;

    public TourLogViewModel(TourLogServiceTemp tourLogService) {
        this.tourLogService = tourLogService;
        this.tourLogsList = FXCollections.observableArrayList(tourLogService.getAllTourLogs());
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
}