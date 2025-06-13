package dev.icefish.tourplanner.client.viewmodel;

import dev.icefish.tourplanner.client.services.ReportService;
import dev.icefish.tourplanner.client.services.TourService;
import dev.icefish.tourplanner.client.utils.TourAttributeHelper;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TourViewModel {
    public final static Logger logger = LogManager.getLogger(TourViewModel.class);
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

    public void generateSummaryReport(Path filePath) throws IOException {
        List<Tour> tours = toursList.stream().toList(); // alle Touren aus aktueller Liste
        // Zu jeder Tour die zugehörigen Logs holen
        Map<Tour, List<TourLog>> logsByTour = new HashMap<>();
        for (Tour tour : tours) {
            List<TourLog> logs = tourLogViewModel.getTourLogsByTourId(tour.getId());
            logsByTour.put(tour, logs);
        }
        // Jetzt ReportService rufen mit allen Daten
        reportService.generateSummaryReport(logsByTour, filePath);
    }

    public ObservableList<Tour> searchTours(String searchText, Integer selectedRating, boolean isChildFriendly) {
        ObservableList<Tour> filteredTours = FXCollections.observableArrayList();
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredTours = FXCollections.observableArrayList(toursList);
        } else {
            searchText = searchText.toLowerCase().trim(); // Normalize search text

            for (Tour tour : toursList) {
                if (searchText.startsWith("name:")) {
                    String nameFilter = searchText.substring(5).trim();
                    if (tour.getName().toLowerCase().contains(nameFilter)) {
                        filteredTours.add(tour);
                    }
                } else if (searchText.startsWith("description:")) {
                    String descriptionFilter = searchText.substring(12).trim();
                    if (tour.getDescription().toLowerCase().contains(descriptionFilter)) {
                        filteredTours.add(tour);
                    }
                } else if (searchText.startsWith("from:")) {
                    String fromFilter = searchText.substring(5).trim();
                    if (tour.getFromLocation().toLowerCase().contains(fromFilter)) {
                        filteredTours.add(tour);
                    }
                } else if (searchText.startsWith("to:")) {
                    String toFilter = searchText.substring(3).trim();
                    if (tour.getToLocation().toLowerCase().contains(toFilter)) {
                        filteredTours.add(tour);
                    }
                } else if (searchText.startsWith("transport:")) {
                    String transportFilter = searchText.substring(10).trim();
                    if (tour.getTransportType().toLowerCase().contains(transportFilter)) {
                        filteredTours.add(tour);
                    }
                } else {
                    int popularity = getPopularity(tour);
                    String childFriendliness = getChildFriendliness(tour);
                    if (tour.getName().toLowerCase().contains(searchText) ||
                            tour.getDescription().toLowerCase().contains(searchText) ||
                            tour.getFromLocation().toLowerCase().contains(searchText) ||
                            tour.getToLocation().toLowerCase().contains(searchText) ||
                            tour.getTransportType().toLowerCase().contains(searchText) ||
                            String.valueOf(popularity).contains(searchText) ||
                            childFriendliness.toLowerCase().contains(searchText)) {
                        filteredTours.add(tour);
                    }
                }
            }
        }
        filteredTours.removeIf(tour -> {
            int popularity = getPopularity(tour);
            String childFriendliness = getChildFriendliness(tour);

            if (selectedRating != null && popularity < selectedRating) {
                return true;
            }

            return isChildFriendly && !"Child-Friendly".equalsIgnoreCase(childFriendliness);
        });

        return filteredTours;
    }

    public int getPopularity(Tour tour) {
        ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(tour.getId());
        return TourAttributeHelper.computePopularity(tourLogs);
    }

    public String getChildFriendliness(Tour tour) {
        ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(tour.getId());
        return TourAttributeHelper.computeChildFriendliness(tourLogs, tour);
    }



}