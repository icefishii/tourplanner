package dev.icefish.tourplanner.client.services;

import dev.icefish.tourplanner.client.model.Tour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//This class is a temporary service to store the tours in memory

public class TourServiceTemp {

    private final ObservableList<Tour> tours = FXCollections.observableArrayList();

    public ObservableList<Tour> getAllTours() {
        return tours;
    }

    public void createNewTour(Tour tour) {
        tours.add(tour);
    }

    public void deleteTour(Tour tour) {
        tours.remove(tour);
    }

    public void updateTour(Tour tour) {
        for (int i = 0; i < tours.size(); i++) {
            if (tours.get(i).getId().equals(tour.getId())) {
                tours.set(i, tour);
                break;
            }
        }
    }
}