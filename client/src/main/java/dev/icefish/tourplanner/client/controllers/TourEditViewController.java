package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.Consumer;
//TODO: Geht nicht
public class TourEditViewController {
    @FXML
    private Button createButton, cancelButton;

    @FXML
    private Label createTourLabel;

    @FXML
    private void initialize() {
        createTourLabel.setText("Edit Tour");
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setText("Save");
        createButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private Label nameLabel, descriptionLabel, fromLabel, toLabel, transportationModeLabel;

    @FXML
    private ComboBox<String> transportTypeBox;

    private Consumer<Tour> tourUpdatedListener;
    private Tour tour;

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

        System.out.println("Tour updated: " + tour.getString());
        WindowUtils.close(tourNameField);
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }
}