package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.function.Consumer;

public class TourEditViewController {
    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private ComboBox<String> transportTypeBox;

    private Consumer<Tour> tourUpdatedListener;
    private Tour tour;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setText("Save");
        createButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTour(Tour tour) {
        this.tour = tour;
        tourNameField.setText(tour.getName());
        tourDescriptionField.setText(tour.getDescription());
        fromLocationField.setText(tour.getFromLocation());
        toLocationField.setText(tour.getToLocation());
        transportTypeBox.setValue(tour.getTransportType());
    }

    public void setTourUpdatedListener(Consumer<Tour> listener) {
        this.tourUpdatedListener = listener;
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        tour.setName(tourNameField.getText());
        tour.setDescription(tourDescriptionField.getText());
        tour.setFromLocation(fromLocationField.getText());
        tour.setToLocation(toLocationField.getText());
        tour.setTransportType(transportTypeBox.getValue());

        if (tourUpdatedListener != null) {
            tourUpdatedListener.accept(tour);
        }

        WindowUtils.close(tourNameField);
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }
}