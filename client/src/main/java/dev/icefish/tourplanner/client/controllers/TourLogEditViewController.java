package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.TourLogService;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Consumer;

public class TourLogEditViewController {
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
        this.tourLogViewModel = new TourLogViewModel(new TourLogService()); // Pass the correct dependency
    }

    public void setTourViewModel(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
        tourComboBox.setItems(tourViewModel.getAllTours());
    }

    @FXML
    private void initialize() {
        createTourLogLabel.setText("Edit Tour Log");
        tourComboBox.setItems(tourViewModel.getAllTours());

        // Set the cell factory to display the tour name
        tourComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        tourComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        createButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);

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
        // Populate fields with the existing TourLog data
        datePicker.setValue(tourLog.getDate().toLocalDateTime().toLocalDate());
        timeField.setText(tourLog.getDate().toLocalDateTime().toLocalTime().toString());
        commentField.setText(tourLog.getComment());
        difficultyField.setText(String.valueOf(tourLog.getDifficulty()));
        distanceField.setText(String.valueOf(tourLog.getDistance()));
        durationField.setText(tourLog.getDurationText());
        ratingField.setText(String.valueOf(tourLog.getRating()));

        // Set the selected tour in the ComboBox
        tourComboBox.getSelectionModel().select(
                tourViewModel.getAllTours().stream()
                        .filter(t -> t.getId().equals(tourLog.getTour().getId()))
                        .findFirst()
                        .orElse(null)
        );
    }

    public void setTourLogUpdatedListener(Consumer<TourLog> listener) {
        this.tourLogUpdatedListener = listener;
    }

    private void onSaveButtonClick(ActionEvent actionEvent) {
        try {
            tourLog.setDate(Timestamp.valueOf(LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeField.getText()))));
            tourLog.setComment(commentField.getText());
            tourLog.setDifficulty(Integer.parseInt(difficultyField.getText()));
            tourLog.setDistance(Double.parseDouble(distanceField.getText()));
            tourLog.setDurationText(durationField.getText());
            tourLog.setRating(Integer.parseInt(ratingField.getText()));

            if (tourLogUpdatedListener != null) {
                tourLogUpdatedListener.accept(tourLog);
            }

            WindowUtils.close(commentField);
        } catch (Exception e) {
            showErrorAlert("Invalid input: " + e.getMessage());
        }
    }

    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(commentField);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}