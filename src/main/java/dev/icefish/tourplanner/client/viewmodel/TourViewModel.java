package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.client.model.Tour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class TourViewModel {

    private ObservableList<Tour> toursList;

    public TourViewModel() {
        this.toursList = FXCollections.observableArrayList();
    }
    public ObservableList<Tour> getAllTours() {
        return this.toursList;
    }

    public void createNewTour(String name, String description, String fromLocation, String toLocation, String transportType) {
        Tour newTour = new Tour(name, description, fromLocation, toLocation, transportType);
        this.toursList.add(newTour);
    }

}
