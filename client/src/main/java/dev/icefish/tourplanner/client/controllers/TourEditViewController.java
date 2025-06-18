package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.function.Consumer;

public class TourEditViewController {
    private static final Logger logger = LogManager.getLogger(TourEditViewController.class);

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

        logger.info("TourEditViewController initialized.");

        Platform.runLater(() -> {
            Scene scene = createButton.getScene();
            if (scene != null) {
                ShortcutUtils.addShortcuts(scene, Map.of(
                        ShortcutUtils.ctrl(javafx.scene.input.KeyCode.S), () -> onSaveButtonClick(null),
                        ShortcutUtils.esc(), () -> onCancelButtonClick(null)
                ));
            }
        });
    }

    public void setTour(Tour tour) {
        this.tour = tour;
        tourNameField.setText(tour.getName());
        tourDescriptionField.setText(tour.getDescription());
        fromLocationField.setText(tour.getFromLocation());
        toLocationField.setText(tour.getToLocation());
        transportTypeBox.setValue(tour.getTransportType());
        logger.info("Tour set for editing: {}", tour);
    }

    public void setTourUpdatedListener(Consumer<Tour> listener) {
        this.tourUpdatedListener = listener;
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        ControllerUtils.resetFieldStyles(
                tourNameField, tourDescriptionField, fromLocationField, toLocationField, transportTypeBox
        );

        tour.setName(tourNameField.getText());
        tour.setDescription(tourDescriptionField.getText());
        tour.setFromLocation(fromLocationField.getText());
        tour.setToLocation(toLocationField.getText());
        tour.setTransportType(transportTypeBox.getValue());

        logger.info("Saving tour: {}", tour);

        if (tourUpdatedListener != null) {
            tourUpdatedListener.accept(tour);
            logger.info("Tour updated listener notified.");
        }

        WindowUtils.close(tourNameField);
        logger.info("Edit window closed after saving.");
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
        logger.info("Edit window closed without saving.");
    }
}