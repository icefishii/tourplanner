package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.TourChecker;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.model.Tour;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    public void onCreateButtonClick(ActionEvent actionEvent) {
        //ToDo Checker einfügen
        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();
        String fromLocation = fromLocationField.getText();
        String toLocation = toLocationField.getText();
        String transportType = transportTypeBox.getValue();

        resetFieldStyles();

        Map<String, String> errors = TourChecker.validateTour(name, description, fromLocation, toLocation, transportType);

        if (!errors.isEmpty()) {
            highlightErrors(errors);
            return; // Methode beenden, wenn ein Fehler gefunden wurde
        }

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
    private void resetFieldStyles() {
        tourNameField.setStyle("");
        tourDescriptionField.setStyle("");
        fromLocationField.setStyle("");
        toLocationField.setStyle("");
        transportTypeBox.setStyle("");
    }

    private void highlightErrors(Map<String, String> errors) {
        if (errors.containsKey("name")) {
            tourNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            tourNameField.setStyle(""); // Falls kein Fehler mehr -> Normalisieren
        }

        // Beschreibung prüfen
        if (errors.containsKey("description")) {
            tourDescriptionField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            tourDescriptionField.setStyle("");
        }

        // From-Location prüfen
        if (errors.containsKey("fromLocation")) {
            fromLocationField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            fromLocationField.setStyle("");
        }

        // To-Location prüfen
        if (errors.containsKey("toLocation")) {
            toLocationField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            toLocationField.setStyle("");
        }

        // Transport-Typ prüfen
        if (errors.containsKey("transportType")) {
            transportTypeBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            transportTypeBox.setStyle("");
        }
    }

}