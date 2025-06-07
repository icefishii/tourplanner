package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.client.services.ReportService;
import dev.icefish.tourplanner.client.services.TourService;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TourViewModel {

    private final ObservableList<Tour> toursList;
    private final TourService tourService;
    private final ReportService reportService;
    private final TourLogViewModel tourLogViewModel;

    public TourViewModel(TourService tourService, ReportService reportService, TourLogViewModel tourLogViewModel) {
        this.tourService = tourService;
        this.reportService = reportService;
        this.tourLogViewModel = tourLogViewModel;
        this.toursList = FXCollections.observableArrayList(tourService.getAllTours());
    }

    public ObservableList<Tour> getAllTours() {
        return this.toursList;
    }

    public void createNewTour(Tour tour) {
        tourService.createNewTour(tour);
        toursList.add(tour);
    }

    public void deleteTour(Tour tour) {
        tourService.deleteTour(tour);
        toursList.remove(tour);
    }

    public void updateTour(Tour tour) {
        tourService.updateTour(tour);
        int index = toursList.indexOf(tour);
        if (index >= 0) {
            toursList.set(index, tour);
        }
    }

    public void generateTourReport(Tour tour, Path filePath) {

        List<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(tour.getId()); // delegieren oder direkt laden
        reportService.generateTourReport(tour, tourLogs, filePath);

    }




}