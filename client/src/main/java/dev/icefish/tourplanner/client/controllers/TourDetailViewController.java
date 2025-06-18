package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.TourAttributeHelper;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TourDetailViewController {

    private static final Logger logger = LogManager.getLogger(TourDetailViewController.class);

    @FXML
    private Button closeButton;

    @FXML
    private Label nameLabel, descriptionLabel, fromLocationLabel, toLocationLabel, transportTypeLabel, distanceLabel, timeLabel, popularityLabel, childFriendlinessLabel;

    public void setTourDetails(Tour tour, ObservableList<TourLog> tourLogs) {
        nameLabel.setText(tour.getName());
        descriptionLabel.setText(tour.getDescription());
        fromLocationLabel.setText(tour.getFromLocation());
        toLocationLabel.setText(tour.getToLocation());
        transportTypeLabel.setText(tour.getTransportType());
        distanceLabel.setText(String.format("%.2f km", tour.getDistance()));
        timeLabel.setText(formatDuration(tour.getEstimatedTime()));

        int popularity = TourAttributeHelper.computePopularity(tourLogs);
        String childFriendliness = TourAttributeHelper.computeChildFriendliness(tourLogs, tour);

        popularityLabel.setText(String.valueOf(popularity));
        childFriendlinessLabel.setText(childFriendliness);

        logger.info("Tour details set: {} with {} logs", tour, tourLogs != null ? tourLogs.size() : 0);
    }

    public void handleClose() {
        WindowUtils.close(nameLabel);
        logger.info("Detail window closed.");
    }

    private String formatDuration(double durationInHours) {
        int hours = (int) durationInHours;
        int minutes = (int) Math.round((durationInHours - hours) * 60);
        return String.format("%dh %02dmin", hours, minutes);
    }
}