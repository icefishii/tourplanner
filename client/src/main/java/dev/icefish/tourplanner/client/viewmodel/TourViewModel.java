package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.services.TourServiceTemp;
import javafx.collections.ObservableList;

public class TourViewModel {

    private ObservableList<Tour> toursList;
    private TourServiceTemp tourService;

    public TourViewModel() {
        this.tourService = new TourServiceTemp();
        this.toursList = tourService.getAllTours();
    }

    public ObservableList<Tour> getAllTours() {
        return this.toursList;
    }

    public void createNewTour(String name, String description, String fromLocation, String toLocation, String transportType) {
        Tour newTour = new Tour(name, description, fromLocation, toLocation, transportType);
        tourService.createNewTour(newTour);
        this.toursList.add(newTour);
    }
}