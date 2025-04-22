package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class TourDetailViewController {

    @FXML
    private Button closeButton;

    @FXML
    private Label nameLabel, descriptionLabel, fromLocationLabel, toLocationLabel, transportTypeLabel, distanceLabel, timeLabel;

    //Ausgabe der Details
    public void setTourDetails(Tour tour) {
        nameLabel.setText(tour.getName());
        descriptionLabel.setText(tour.getDescription());
        fromLocationLabel.setText(tour.getFromLocation());
        toLocationLabel.setText(tour.getToLocation());
        transportTypeLabel.setText( tour.getTransportType());
        distanceLabel.setText(String.format("%.2f km", tour.getDistance()));
        timeLabel.setText(formatDuration(tour.getEstimatedTime()));
    }

    //schließen des Fensters
    public void handleClose() {
        WindowUtils.close(nameLabel); //Unsere eigene Klasse in utils
    }

    private String formatDuration(double durationInHours) {
        int hours = (int) durationInHours;
        int minutes = (int) Math.round((durationInHours - hours) * 60);
        return String.format("%dh %02dmin", hours, minutes);
    }

}