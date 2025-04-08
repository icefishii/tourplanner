package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.util.Map;
import java.util.function.Consumer;

public class TourCreateViewController {

    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private ComboBox<String> transportTypeBox;

    @FXML
    private Button createButton, cancelButton;

    private Consumer<Tour> tourCreatedListener;
    private final TourViewModel tourViewModel;

    public TourCreateViewController(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
    }

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTourCreatedListener(Consumer<Tour> listener) {
        this.tourCreatedListener = listener;
    }

    private void onCreateButtonClick(ActionEvent actionEvent) {
        try {
            String name = tourNameField.getText();
            String description = tourDescriptionField.getText();
            String fromLocation = fromLocationField.getText();
            String toLocation = toLocationField.getText();
            String transportType = transportTypeBox.getValue();

            Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourChecker.validateTour(
                    name, description, fromLocation, toLocation, transportType);

            if (!errors.isEmpty()) {
                showErrorAlert(errors);
                return;
            }

            Tour newTour = new Tour(UUIDv7Generator.generateUUIDv7(), name, description, fromLocation, toLocation, transportType);

            if (tourCreatedListener != null) {
                tourCreatedListener.accept(newTour); // Notify listener
            }

            WindowUtils.close(tourNameField);
        } catch (Exception e) {
            showErrorAlert(Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }

    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }

    private void showErrorAlert(Map<String, String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(String.join("\n", errors.values()));
        alert.showAndWait();
    }
}