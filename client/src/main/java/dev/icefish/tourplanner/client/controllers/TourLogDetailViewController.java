package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TourLogDetailViewController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label commentLabel;

    @FXML
    private Label difficultyLabel;

    @FXML
    private Label distanceLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label ratingLabel;

    private TourLog tourLog;

    // Setter für das TourLog, das angezeigt werden soll
    public void setTourLog(TourLog tourLog) {
        this.tourLog = tourLog;
        // Fülle die Labels mit den TourLog-Daten
        dateLabel.setText(tourLog.getDate().toString());
        commentLabel.setText(tourLog.getComment());
        difficultyLabel.setText(String.valueOf(tourLog.getDifficulty()));
        distanceLabel.setText(String.format("%.2f km", tourLog.getDistance()));
        durationLabel.setText(String.format("%s min",tourLog.getDurationText()));
        setRatingStars(tourLog.getRating());
    }

    private void setRatingStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");  // Voller Stern
            } else {
                stars.append("☆");  // Leerer Stern
            }
        }
        ratingLabel.setText(stars.toString());
    }

    @FXML
    private void handleClose() {
        // Schließe das Fenster, wenn der OK-Button geklickt wird
        WindowUtils.close(dateLabel);
    }
}
