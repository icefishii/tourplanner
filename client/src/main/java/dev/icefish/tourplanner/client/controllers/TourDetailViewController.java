package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class TourDetailViewController {

    @FXML
    private Button closeButton;

    @FXML
    private Label nameLabel, descriptionLabel, fromLocationLabel, toLocationLabel, transportTypeLabel;

    public void setTourDetails(Tour tour) {
        nameLabel.setText(tour.getName());
        descriptionLabel.setText(tour.getDescription());
        fromLocationLabel.setText(tour.getFromLocation());
        toLocationLabel.setText(tour.getToLocation());
        transportTypeLabel.setText( tour.getTransportType());
    }

    public void handleClose() {
        WindowUtils.close(nameLabel);
    }

}