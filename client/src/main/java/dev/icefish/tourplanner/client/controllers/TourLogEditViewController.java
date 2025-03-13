package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.function.Consumer;
//TODO: Edit geht noch nicht
public class TourLogEditViewController {
    @FXML
    private Button saveButton, cancelButton;

    @FXML
    private TextField dateField, difficultyField, distanceField, durationField, ratingField;

    @FXML
    private TextArea commentField;

    private Consumer<TourLog> tourLogUpdatedListener;
    private TourLog tourLog;

    @FXML
    private void initialize() {
        saveButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTourLog(TourLog tourLog) {
        this.tourLog = tourLog;
        dateField.setText(tourLog.getDate().toString());
        commentField.setText(tourLog.getComment());
        difficultyField.setText(String.valueOf(tourLog.getDifficulty()));
        distanceField.setText(String.valueOf(tourLog.getDistance()));
        durationField.setText(tourLog.getDurationText());
        ratingField.setText(String.valueOf(tourLog.getRating()));
    }

    public void setTourLogUpdatedListener(Consumer<TourLog> listener) {
        this.tourLogUpdatedListener = listener;
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        tourLog.setDate(Timestamp.valueOf(dateField.getText()));
        tourLog.setComment(commentField.getText());
        tourLog.setDifficulty(Integer.parseInt(difficultyField.getText()));
        tourLog.setDistance(Double.parseDouble(distanceField.getText()));
        tourLog.setDurationText(durationField.getText());
        tourLog.setRating(Integer.parseInt(ratingField.getText()));

        if (tourLogUpdatedListener != null) {
            tourLogUpdatedListener.accept(tourLog);
        }

        System.out.println("TourLog updated: " + tourLog);
        WindowUtils.close(dateField);
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(dateField);
    }
}