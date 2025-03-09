package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.model.Tour;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class TourCreateViewController {
    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private Label nameLabel, descriptionLabel, fromLabel, toLabel, transportationModeLabel;

    @FXML
    private ComboBox<String> transportTypeBox;

    private Consumer<Tour> tourCreatedListener;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTourCreatedListener(Consumer<Tour> listener) {
        this.tourCreatedListener = listener;
    }

    public void onCreateButtonClick(ActionEvent actionEvent) {
        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();
        String fromLocation = fromLocationField.getText();
        String toLocation = toLocationField.getText();
        String transportType = transportTypeBox.getValue();

        Tour newTour = new Tour(name, description, fromLocation, toLocation, transportType);

        if (tourCreatedListener != null) {
            tourCreatedListener.accept(newTour);
        }

        System.out.println("Tour created: " + newTour.getString());
        WindowUtils.close(tourNameField);
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }
}