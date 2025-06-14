package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.services.OpenRouteService;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import dev.icefish.tourplanner.models.Tour;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebView;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;

public class TourCreateViewController {
    private final static Logger logger = LogManager.getLogger(TourCreateViewController.class);
    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private ComboBox<String> transportTypeBox;

    @FXML
    private WebView mapWebView;

    @FXML
    private Button createButton, cancelButton, loadMapButton;

    private final TourViewModel tourViewModel;
    private final MapViewModel mapViewModel;
    private Consumer<Tour> tourCreatedListener;

    public TourCreateViewController(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
        this.mapViewModel = new MapViewModel();
    }

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
        loadMapButton.setOnAction(this::onLoadMapButtonClick);

        Platform.runLater(() -> {
            Scene scene = createButton.getScene();
            if (scene != null) {
                ShortcutUtils.addShortcuts(scene, Map.of(
                        ShortcutUtils.ctrl(KeyCode.S), () -> onCreateButtonClick(null),
                        ShortcutUtils.esc(), () -> onCancelButtonClick(null),
                        ShortcutUtils.ctrl(KeyCode.L), () -> onLoadMapButtonClick(null)
                ));
            }
        });
    }



    private void onLoadMapButtonClick(ActionEvent event) {
        try {
            double[] fromCoords = GeoCoder.getCoordinates(fromLocationField.getText());
            double[] toCoords = GeoCoder.getCoordinates(toLocationField.getText());
            String transportType = transportTypeBox.getValue().toLowerCase();

            String orsTransport = switch (transportType) {
                case "walk" -> "foot-walking";
                case "bike" -> "cycling-regular";
                case "car" -> "driving-car";
                default -> throw new IllegalArgumentException("Unsupported transport type: " + transportType);
            };

            String htmlTemplate = new String(getClass().getResourceAsStream("/MapTemplate.html").readAllBytes());
            String apiKey = ConfigLoader.get("openrouteservice.api.key");

            String htmlContent = htmlTemplate
                    .replace("LAT_FROM", String.valueOf(fromCoords[0]))
                    .replace("LON_FROM", String.valueOf(fromCoords[1]))
                    .replace("LAT_TO", String.valueOf(toCoords[0]))
                    .replace("LON_TO", String.valueOf(toCoords[1]))
                    .replace("TRANSPORT_MODE", orsTransport)
                    .replace("API_KEY", apiKey);

            mapWebView.getEngine().loadContent(htmlContent);

            mapWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    // Karte ist fertig geladen
                    System.out.println("Map fully loaded.");
                }
            });

        } catch (Exception e) {
            showErrorAlert(Map.of("error", "Couldn't load map: " + e.getMessage()));
        }
    }

    public void setTourCreatedListener(Consumer<Tour> listener) {
        this.tourCreatedListener = listener;
    }

    private void onCreateButtonClick(ActionEvent actionEvent) {
        try {
            resetFieldStyles();

            String name = tourNameField.getText();
            String description = tourDescriptionField.getText();
            String fromLocation = fromLocationField.getText();
            String toLocation = toLocationField.getText();
            String transportType = transportTypeBox.getValue();

            Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourChecker.validateTour(
                    name, description, fromLocation, toLocation, transportType);

            if (!errors.isEmpty()) {
                highlightErrorFields(errors);
                showErrorAlert(errors);
                return;
            }

            Tour newTour = new Tour(name, description, fromLocation, toLocation, transportType);
            newTour.setId(UUIDv7Generator.generateUUIDv7());

            try {
                OpenRouteService.RouteInfo info = OpenRouteService.getRouteInfo(fromLocation, toLocation, transportType);
                logger.info(info.toString());
                newTour.setDistance(info.distanceInKm());
                newTour.setEstimatedTime(info.durationInHours());
            } catch (Exception e) {
                logger.warn("Error retrieving route info: {}", e.getMessage());
                showErrorAlert(Map.of("error", "Route info couldn't be retrieved: " + e.getMessage()));
                return;
            }

            if (tourCreatedListener != null) {
                tourCreatedListener.accept(newTour);
            }

            // Hier neuer Aufruf MapService
            MapService.saveWebViewSnapshot(mapWebView, newTour, mapViewModel);

            WindowUtils.close(tourNameField);
        } catch (Exception e) {
            showErrorAlert(Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }

    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }

    private void resetFieldStyles() {
        tourNameField.setStyle(null);
        tourDescriptionField.setStyle(null);
        fromLocationField.setStyle(null);
        toLocationField.setStyle(null);
        transportTypeBox.setStyle(null);
    }

    private void highlightErrorFields(Map<String, String> errors) {

        if (errors.containsKey("name")) {
            logger.error(errors.get("name"));
            tourNameField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("description")) {
            logger.error(errors.get("description"));
            tourDescriptionField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("fromLocation")) {
            logger.error(errors.get("fromLocation"));
            fromLocationField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("toLocation")) {
            logger.error(errors.get("toLocation"));
            toLocationField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("transportType")) {
            logger.error(errors.get("transportType"));
            transportTypeBox.setStyle("-fx-border-color: red;");
        }
    }

    private void showErrorAlert(Map<String, String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(String.join("\n", errors.values()));
        alert.showAndWait();
    }
}
