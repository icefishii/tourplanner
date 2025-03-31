package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.TourChecker;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.model.Tour;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;
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

    //Beim erstellen einer Tour
    public void onCreateButtonClick(ActionEvent actionEvent) {
        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();
        String fromLocation = fromLocationField.getText();
        String toLocation = toLocationField.getText();
        String transportType = transportTypeBox.getValue();

        resetFieldStyles();

        Map<String, String> errors = TourChecker.validateTour(name, description, fromLocation, toLocation, transportType);

        if (!errors.isEmpty()) {
            highlightErrors(errors);
            return;
        }

        Tour newTour = new Tour(name, description, fromLocation, toLocation, transportType);

        if (tourCreatedListener != null) {
            tourCreatedListener.accept(newTour);
        }

        System.out.println("Tour created: " + newTour.getString());
        WindowUtils.close(tourNameField);
    }

    //Fenster schließen
    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }

    //Felder leeren
    private void resetFieldStyles() {
        tourNameField.setStyle("");
        tourDescriptionField.setStyle("");
        fromLocationField.setStyle("");
        toLocationField.setStyle("");
        transportTypeBox.setStyle("");
    }

    //Wenn etwas fehlt in der Eingabe
    private void highlightErrors(Map<String, String> errors) {
        if (errors.containsKey("name")) {
            tourNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            tourNameField.setStyle("");
        }

        if (errors.containsKey("description")) {
            tourDescriptionField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            tourDescriptionField.setStyle("");
        }

        if (errors.containsKey("fromLocation")) {
            fromLocationField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            fromLocationField.setStyle("");
        }

        if (errors.containsKey("toLocation")) {
            toLocationField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            toLocationField.setStyle("");
        }

        if (errors.containsKey("transportType")) {
            transportTypeBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            transportTypeBox.setStyle("");
        }
    }
}