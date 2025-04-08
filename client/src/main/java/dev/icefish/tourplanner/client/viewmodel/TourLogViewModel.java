package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.services.TourLogServiceTemp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class TourLogViewModel {

    private ObservableList<TourLog> tourLogsList;
    private TourLogServiceTemp tourLogService;
    private TourViewModel tourViewModel;

    public TourLogViewModel() {
        this.tourLogService = new TourLogServiceTemp();
        this.tourLogsList = tourLogService.getAllTourLogs();
        this.tourViewModel = new TourViewModel(); // Default constructor still creates one if needed
    }

    public TourLogViewModel(TourViewModel tourViewModel) {
        this.tourLogService = new TourLogServiceTemp();
        this.tourLogsList = tourLogService.getAllTourLogs();
        this.tourViewModel = tourViewModel;
    }

    public ObservableList<TourLog> getAllTourLogs() {
        return this.tourLogsList;
    }

    public ObservableList<Tour> getAllTours() {
        return tourViewModel.getAllTours();
    }

    public void createNewTourLog(TourLog tourLog) {
        tourLogService.createNewTourLog(tourLog);
    }

    public void deleteTourLog(TourLog tourLog) {
        tourLogService.deleteTourLog(tourLog);
    }

    public void updateTourLog(TourLog tourLog) {
        tourLogService.updateTourLog(tourLog);
    }

    public ObservableList<TourLog> getTourLogsByTourId(UUID tourId) {
        ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();
        tourLogs = tourLogService.getTourLogsfromTour(tourId);
        return tourLogs;
    }
}