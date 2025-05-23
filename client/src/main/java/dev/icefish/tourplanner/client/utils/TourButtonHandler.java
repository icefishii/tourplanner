package dev.icefish.tourplanner.client.utils;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TourButtonHandler {
    private final static Logger logger = LogManager.getLogger(TourButtonHandler.class);
    private Button deleteTourButton;
    private Button editTourButton;
    private Button newTourButton;
    private ListView<Tour> tourListView;

    public TourButtonHandler(Button deleteTourButton, Button editTourButton, Button newTourButton, ListView<Tour> tourListView) {
        this.deleteTourButton = deleteTourButton;
        this.editTourButton = editTourButton;
        this.newTourButton = newTourButton;
        this.tourListView = tourListView;
        initializeButtonState();
    }

    private void initializeButtonState() {
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        updateButtonState();
    }

    private void updateButtonState() {
        logger.info("Updating tour button state");
        int selectedCount = tourListView.getSelectionModel().getSelectedItems().size();

        if (selectedCount == 0) {
            deleteTourButton.setDisable(true);
            editTourButton.setDisable(true);
        } else if (selectedCount == 1) {
            deleteTourButton.setDisable(false);
            editTourButton.setDisable(false);
        } else {
            deleteTourButton.setDisable(false);
            editTourButton.setDisable(true);
        }
    }

    public void resetButtons() {
        logger.info("Resetting tour button state");
        deleteTourButton.setDisable(true);
        editTourButton.setDisable(true);
    }
}