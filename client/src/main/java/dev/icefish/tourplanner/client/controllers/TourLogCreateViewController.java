package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.TourLogChecker;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Consumer;

public class TourLogCreateViewController {

    private final static Logger logger = LogManager.getLogger(TourLogCreateViewController.class);

    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField timeField, difficultyField, distanceField, durationField, ratingField;

    @FXML
    private TextArea commentField;

    @FXML
    private ComboBox<Tour> tourComboBox;

    @FXML
    private DatePicker datePicker;

    private Consumer<TourLog> tourLogCreatedListener;
    private final TourLogViewModel tourLogViewModel;
    private final TourViewModel tourViewModel;

    public TourLogCreateViewController(TourViewModel tourViewModel, TourLogViewModel tourLogViewModel) {
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = tourLogViewModel;
    }

    public void setTourLogCreatedListener(Consumer<TourLog> listener) {
        this.tourLogCreatedListener = listener;
    }

    private void onTourLogCreated(TourLog tourLog) {
        if (tourLogCreatedListener != null) {
            tourLogCreatedListener.accept(tourLog);
            logger.info("TourLog created listener notified.");
        }
    }

    @FXML
    private void initialize() {
        tourComboBox.setItems(tourViewModel.getAllTours());
        ControllerUtils.setupTourComboBox(tourComboBox);

        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);

        logger.info("TourLogCreateViewController initialized.");

        Platform.runLater(() -> {
            Scene scene = createButton.getScene();
            if (scene != null) {
                ShortcutUtils.addShortcuts(scene, Map.of(
                        ShortcutUtils.ctrl(KeyCode.S), () -> onCreateButtonClick(null),
                        ShortcutUtils.esc(), () -> onCancelButtonClick(null)
                ));
            }
        });
    }

    private void onCreateButtonClick(ActionEvent actionEvent) {
        ControllerUtils.resetFieldStyles(
                timeField, difficultyField, distanceField, durationField, ratingField, commentField, tourComboBox, datePicker
        );
        try {
            Tour selectedTour = tourComboBox.getValue();
            LocalDate date = datePicker.getValue();
            String timeText = timeField.getText();
            String comment = commentField.getText();
            String difficultyText = difficultyField.getText();
            String distanceText = distanceField.getText();
            String durationText = durationField.getText();
            String ratingText = ratingField.getText();

            Map<String, String> errors = TourLogChecker.validateTourLogRaw(
                    selectedTour, date, timeText, comment, difficultyText, distanceText, durationText, ratingText);

            if (!errors.isEmpty()) {
                ControllerUtils.highlightErrorFields(errors, Map.of(
                        "tour", tourComboBox,
                        "date", datePicker,
                        "comment", commentField,
                        "difficulty", difficultyField,
                        "distance", distanceField,
                        "duration", durationField,
                        "rating", ratingField,
                        "time", timeField
                ));
                ControllerUtils.showErrorAlert(errors);
                logger.error("Validation errors: {}", errors);
                return;
            }

            LocalTime time = LocalTime.parse(timeText);
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(date, time));
            int difficulty = Integer.parseInt(difficultyText);
            double distance = Double.parseDouble(distanceText);
            int rating = Integer.parseInt(ratingText);

            TourLog newTourLog = new TourLog();
            newTourLog.setId(UUIDv7Generator.generateUUIDv7());
            newTourLog.setTour(selectedTour);
            newTourLog.setDate(timestamp);
            newTourLog.setComment(comment);
            newTourLog.setDifficulty(difficulty);
            newTourLog.setDistance(distance);
            newTourLog.setDurationText(durationText);
            newTourLog.setRating(rating);

            logger.info("Creating new TourLog: {}", newTourLog);

            if (tourLogCreatedListener != null) {
                tourLogCreatedListener.accept(newTourLog);
                logger.info("TourLog created listener notified.");
            }

            WindowUtils.close(commentField);
            logger.info("Create window closed after saving.");
        } catch (Exception e) {
            logger.error("Error creating TourLog: {}", e.getMessage());
            ControllerUtils.showErrorAlert(Map.of("error", "Unexpected Error: " + e.getMessage()));
        }
    }

    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(commentField);
        logger.info("Create window closed without saving.");
    }
}