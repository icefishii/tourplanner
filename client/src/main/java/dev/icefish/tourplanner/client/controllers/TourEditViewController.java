package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.icefish.tourplanner.client.services.OpenRouteService;


import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TourEditViewController {
    private static final Logger logger = LogManager.getLogger(TourEditViewController.class);

    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private Label createTourLabel;

    @FXML
    private ComboBox<String> transportTypeBox;

    @FXML
    private WebView mapWebView;

    @FXML
    private Button loadMapButton;

    private Consumer<Tour> tourUpdatedListener;
    private Tour tour;
    private boolean mapLoaded = false;
    private final MapViewModel mapViewModel = new MapViewModel();

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setText("Save");
        createTourLabel.setText("Edit Tour");
        createButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
        loadMapButton.setOnAction(this::onLoadMapButtonClick);

        fromLocationField.setDisable(true);
        toLocationField.setDisable(true);

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

        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();
        String fromLocation = fromLocationField.getText();
        String toLocation = toLocationField.getText();
        String transportType = transportTypeBox.getValue();

        Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourChecker.validateTour(
                name, description, fromLocation, toLocation, transportType
        );

        if (!errors.isEmpty()) {
            ControllerUtils.highlightErrorFields(errors, Map.of(
                    "name", tourNameField,
                    "description", tourDescriptionField,
                    "fromLocation", fromLocationField,
                    "toLocation", toLocationField,
                    "transportType", transportTypeBox
            ));
            ControllerUtils.showErrorAlert(errors);
            logger.error("Validation errors in EditView: {}", errors);
            return;
        }

        if (!mapLoaded) {
            ControllerUtils.showErrorAlert(Map.of("error", "Please load the map before saving the tour."));
            logger.error("Map not loaded before saving.");
            return;
        }

        tour.setName(name);
        tour.setDescription(description);
        tour.setTransportType(transportType);

        try {
            OpenRouteService.RouteInfo info = OpenRouteService.getRouteInfo(fromLocation, toLocation, transportType);
            logger.info("Updated route info: {}", info);
            tour.setDistance(info.distanceInKm());
            tour.setEstimatedTime(info.durationInHours());
        } catch (Exception e) {
            logger.warn("Failed to update route info: {}", e.getMessage());
            ControllerUtils.showErrorAlert(Map.of("error", "Route info couldn't be updated: " + e.getMessage()));
            return;
        }

        logger.info("Saving tour: {}", tour);

        if (tourUpdatedListener != null) {
            tourUpdatedListener.accept(tour);
            logger.info("Tour updated listener notified.");
        }

        MapService.saveWebViewSnapshot(mapWebView, tour, mapViewModel);

        WindowUtils.close(tourNameField);
        logger.info("Edit window closed after saving.");
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
        logger.info("Edit window closed without saving.");
    }

    private void onLoadMapButtonClick(ActionEvent event) {
        try {
            ControllerUtils.resetFieldStyles(
                    tourNameField, tourDescriptionField, fromLocationField, toLocationField, transportTypeBox
            );

            String fromLocation = fromLocationField.getText();
            String toLocation = toLocationField.getText();
            String transportType = transportTypeBox.getValue();

            Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourChecker.validateTour(
                    tourNameField.getText(), tourDescriptionField.getText(),
                    fromLocation, toLocation, transportType
            );

            if (!errors.isEmpty()) {
                ControllerUtils.highlightErrorFields(errors, Map.of(
                        "name", tourNameField,
                        "description", tourDescriptionField,
                        "fromLocation", fromLocationField,
                        "toLocation", toLocationField,
                        "transportType", transportTypeBox
                ));
                ControllerUtils.showErrorAlert(errors);
                logger.error("Validation errors during map load: {}", errors);
                return;
            }

            double[] fromCoords = GeoCoder.getCoordinates(fromLocation);
            double[] toCoords = GeoCoder.getCoordinates(toLocation);
            String orsTransport = switch (transportType.toLowerCase()) {
                case "walk" -> "foot-walking";
                case "bike" -> "cycling-regular";
                case "car" -> "driving-car";
                default -> throw new IllegalArgumentException("Unsupported transport type: " + transportType);
            };

            String htmlTemplate = new String(Objects.requireNonNull(getClass().getResourceAsStream("/MapTemplate.html")).readAllBytes());
            String apiKey = ConfigLoader.get("openrouteservice.api.key");

            String htmlContent = htmlTemplate
                    .replace("LAT_FROM", String.valueOf(fromCoords[0]))
                    .replace("LON_FROM", String.valueOf(fromCoords[1]))
                    .replace("LAT_TO", String.valueOf(toCoords[0]))
                    .replace("LON_TO", String.valueOf(toCoords[1]))
                    .replace("TRANSPORT_MODE", orsTransport)
                    .replace("API_KEY", apiKey);

            mapLoaded = false;

            mapWebView.getEngine().loadContent(htmlContent);
            mapWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    mapLoaded = true;
                    logger.info("Map successfully loaded in edit view.");
                }
            });

        } catch (Exception e) {
            ControllerUtils.showErrorAlert(Map.of("error", "Could not load map: " + e.getMessage()));
            logger.error("Error loading map in edit view: {}", e.getMessage());
        }
    }

}