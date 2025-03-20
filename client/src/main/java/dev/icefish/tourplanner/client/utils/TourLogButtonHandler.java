package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.client.model.TourLog;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;

public class TourLogButtonHandler {

    private Button deleteTourLogButton;
    private Button editTourLogButton;
    private TableView<TourLog> tourLogTableView;

    public TourLogButtonHandler(Button deleteTourLogButton, Button editTourLogButton, TableView<TourLog> tourLogTableView) {
        this.deleteTourLogButton = deleteTourLogButton;
        this.editTourLogButton = editTourLogButton;
        this.tourLogTableView = tourLogTableView;
        initializeButtonState();
    }

    private void initializeButtonState() {
        tourLogTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourLogTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        updateButtonState();
    }

    //Aus und einblenden
    private void updateButtonState() {
        int selectedCount = tourLogTableView.getSelectionModel().getSelectedItems().size();

        if (selectedCount == 0) {
            deleteTourLogButton.setDisable(true);
            editTourLogButton.setDisable(true);
        } else if (selectedCount == 1) {
            deleteTourLogButton.setDisable(false);
            editTourLogButton.setDisable(false);
        } else {
            deleteTourLogButton.setDisable(false);
            editTourLogButton.setDisable(true);
        }
    }

    public void resetButtons() {
        deleteTourLogButton.setDisable(true);
        editTourLogButton.setDisable(true);
    }
}