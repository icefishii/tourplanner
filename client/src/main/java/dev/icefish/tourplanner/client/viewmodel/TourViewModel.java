package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.client.services.TourServiceTemp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TourViewModel {

    private final ObservableList<Tour> toursList;
    private final TourServiceTemp tourService;

    public TourViewModel(TourServiceTemp tourService) {
        this.tourService = tourService;
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
}