package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Consumer;

public class TourLogEditViewController {
    private static final Logger logger = LogManager.getLogger(TourLogEditViewController.class);

    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField timeField, difficultyField, distanceField, durationField, ratingField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea commentField;

    @FXML
    private ComboBox<Tour> tourComboBox;

    @FXML
    private Label createTourLogLabel;

    private Consumer<TourLog> tourLogUpdatedListener;
    private TourLog tourLog;
    private TourLogViewModel tourLogViewModel;
    private TourViewModel tourViewModel;

    public TourLogEditViewController(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = new TourLogViewModel(new TourLogService());
    }

    public void setTourViewModel(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
        tourComboBox.setItems(tourViewModel.getAllTours());
    }

    @FXML
    private void initialize() {
        createTourLogLabel.setText("Edit Tour Log");
        tourComboBox.setItems(tourViewModel.getAllTours());
        ControllerUtils.setupTourComboBox(tourComboBox);

        createButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);

        tourComboBox.setDisable(true);

        logger.info("TourLogEditViewController initialized.");

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

    public void setTourLog(TourLog tourLog) {
        this.tourLog = tourLog;
        datePicker.setValue(tourLog.getDate().toLocalDateTime().toLocalDate());
        timeField.setText(tourLog.getDate().toLocalDateTime().toLocalTime().toString());
        commentField.setText(tourLog.getComment());
        difficultyField.setText(String.valueOf(tourLog.getDifficulty()));
        distanceField.setText(String.valueOf(tourLog.getDistance()));
        durationField.setText(tourLog.getDurationText());
        ratingField.setText(String.valueOf(tourLog.getRating()));

        tourComboBox.getSelectionModel().select(
                tourViewModel.getAllTours().stream()
                        .filter(t -> t.getId().equals(tourLog.getTour().getId()))
                        .findFirst()
                        .orElse(null)
        );
        logger.info("TourLog set for editing: {}", tourLog);
    }

    public void setTourLogUpdatedListener(Consumer<TourLog> listener) {
        this.tourLogUpdatedListener = listener;
    }

    private void onSaveButtonClick(ActionEvent actionEvent) {
        ControllerUtils.resetFieldStyles(
                timeField, difficultyField, distanceField, durationField, ratingField, commentField, tourComboBox, datePicker
        );
        try {
            Tour selectedTour = tourComboBox.getValue();
            var date = datePicker.getValue();
            var timeText = timeField.getText();
            var comment = commentField.getText();
            var difficultyText = difficultyField.getText();
            var distanceText = distanceField.getText();
            var durationText = durationField.getText();
            var ratingText = ratingField.getText();

            Map<String, String> errors = dev.icefish.tourplanner.client.utils.TourLogChecker.validateTourLogRaw(
                    selectedTour, date, timeText, comment, difficultyText, distanceText, durationText, ratingText
            );

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

            tourLog.setDate(Timestamp.valueOf(LocalDateTime.of(date, LocalTime.parse(timeText))));
            tourLog.setComment(comment);
            tourLog.setDifficulty(Integer.parseInt(difficultyText));
            tourLog.setDistance(Double.parseDouble(distanceText));
            tourLog.setDurationText(durationText);
            tourLog.setRating(Integer.parseInt(ratingText));

            logger.info("Saving TourLog: {}", tourLog);

            if (tourLogUpdatedListener != null) {
                tourLogUpdatedListener.accept(tourLog);
                logger.info("TourLog updated listener notified.");
            }

            WindowUtils.close(commentField);
            logger.info("Edit window closed after saving.");
        } catch (Exception e) {
            logger.error("Error saving TourLog: {}", e.getMessage());
            ControllerUtils.showErrorAlert(Map.of("error", "Unexpected Error: " + e.getMessage()));
        }
    }


    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(commentField);
        logger.info("Edit window closed without saving.");
    }
}