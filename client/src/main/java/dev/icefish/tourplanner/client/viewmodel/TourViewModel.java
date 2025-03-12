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
        if (!toursList.contains(newTour)) {
            tourService.createNewTour(newTour);
        }
    }

    public void deleteTour(Tour tour) {
        tourService.deleteTour(tour);
    }

    public void updateTour(Tour tour) {
        for (int i = 0; i < toursList.size(); i++) {
            if (toursList.get(i).getId().equals(tour.getId())) {
                toursList.set(i, tour);
                break;
            }
        }
        tourService.updateTour(tour);
    }
}