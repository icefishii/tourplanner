package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.services.OpenRouteService;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.models.Tour;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebView;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;
import java.util.Objects;
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

    private final MapViewModel mapViewModel;
    private Consumer<Tour> tourCreatedListener;

    private boolean mapLoaded = false;

    public TourCreateViewController() {
        this.mapViewModel = new MapViewModel();
    }

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
        loadMapButton.setOnAction(this::onLoadMapButtonClick);
        logger.info("TourCreateViewController initialized.");
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

    // Wird aufgerufen, wenn der "Load Map"-Button gedrückt wird
    private void onLoadMapButtonClick(ActionEvent event) {
        try {
            ControllerUtils.resetFieldStyles(
                    tourNameField, tourDescriptionField, fromLocationField, toLocationField, transportTypeBox
            );

            String name = tourNameField.getText();
            String description = tourDescriptionField.getText();
            String fromLocation = fromLocationField.getText();
            String toLocation = toLocationField.getText();
            String transportType = transportTypeBox.getValue();

            Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourChecker.validateTour(
                    name, description, fromLocation, toLocation, transportType);

            if (!errors.isEmpty()) {
                ControllerUtils.highlightErrorFields(errors, Map.of(
                        "name", tourNameField,
                        "description", tourDescriptionField,
                        "fromLocation", fromLocationField,
                        "toLocation", toLocationField,
                        "transportType", transportTypeBox
                ));
                ControllerUtils.showErrorAlert(errors);
                logger.error("Validation errors: {}", errors);
                return;
            }

            // Koordinaten
            double[] fromCoords = GeoCoder.getCoordinates(fromLocation);
            double[] toCoords = GeoCoder.getCoordinates(toLocation);

            // Transportmittel in OpenRouteService-Format umwandeln
            String orsTransport = switch (transportType.toLowerCase()) {
                case "walk" -> "foot-walking";
                case "bike" -> "cycling-regular";
                case "car" -> "driving-car";
                default -> throw new IllegalArgumentException("Unsupported transport type: " + transportType);
            };

            // HTML-Vorlage für die Karte laden
            String htmlTemplate;
            try {
                htmlTemplate = new String(Objects.requireNonNull(getClass().getResourceAsStream("/MapTemplate.html")).readAllBytes());
            } catch (Exception e) {
                logger.error("Error loading HTML template: {}", e.getMessage());
                ControllerUtils.showErrorAlert(Map.of("error", "Couldn't load map template: " + e.getMessage()));
                return;
            }

            // API-Key laden
            String apiKey = ConfigLoader.get("openrouteservice.api.key");

            // HTML-Inhalt mit Koordinaten und Parametern ersetzen
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
                    logger.info("Map fully loaded.");
                }
            });

        } catch (Exception e) {
            ControllerUtils.showErrorAlert(Map.of("error", "Couldn't load map: " + e.getMessage()));
            logger.error("Error loading map: {}", e.getMessage());
        }
    }

    public void setTourCreatedListener(Consumer<Tour> listener) {
        this.tourCreatedListener = listener;
    }

    // Wird aufgerufen, wenn der "Create"-Button gedrückt wird
    private void onCreateButtonClick(ActionEvent actionEvent) {
        try {
            ControllerUtils.resetFieldStyles(
                    tourNameField, tourDescriptionField, fromLocationField, toLocationField, transportTypeBox
            );

            String name = tourNameField.getText();
            String description = tourDescriptionField.getText();
            String fromLocation = fromLocationField.getText();
            String toLocation = toLocationField.getText();
            String transportType = transportTypeBox.getValue();

            Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourChecker.validateTour(
                    name, description, fromLocation, toLocation, transportType);

            if (!errors.isEmpty()) {
                ControllerUtils.highlightErrorFields(errors, Map.of(
                        "name", tourNameField,
                        "description", tourDescriptionField,
                        "fromLocation", fromLocationField,
                        "toLocation", toLocationField,
                        "transportType", transportTypeBox
                ));
                ControllerUtils.showErrorAlert(errors);
                logger.error("Validation errors: {}", errors);
                return;
            }

            if (!mapLoaded) {
                ControllerUtils.showErrorAlert(Map.of("error", "Please load the map before creating the tour."));
                logger.error("Map not loaded before creating tour.");
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
                ControllerUtils.showErrorAlert(Map.of("error", "Route info couldn't be retrieved: " + e.getMessage()));
                return;
            }

            if (tourCreatedListener != null) {
                tourCreatedListener.accept(newTour);
            }

            // Karten-Snapshot speichern
            MapService.saveWebViewSnapshot(mapWebView, newTour, mapViewModel);

            WindowUtils.close(tourNameField);
        } catch (Exception e) {
            ControllerUtils.showErrorAlert(Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }

    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(tourNameField);
    }
}