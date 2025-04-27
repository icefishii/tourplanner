package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TourLogButtonHandler {
    private final static Logger logger = LogManager.getLogger(TourLogButtonHandler.class);
    @FXML
    private ListView<Tour> tourListView;

    private Button newTourLogButton;
    private Button deleteTourLogButton;
    private Button editTourLogButton;
    private TableView<TourLog> tourLogTableView;

    public TourLogButtonHandler(ListView<Tour> tourListView, Button newTourLogButton, Button deleteTourLogButton, Button editTourLogButton, TableView<TourLog> tourLogTableView) {
        this.tourListView = tourListView;
        this.newTourLogButton = newTourLogButton;
        this.deleteTourLogButton = deleteTourLogButton;
        this.editTourLogButton = editTourLogButton;
        this.tourLogTableView = tourLogTableView;
        initializeButtonState();
    }

    private void initializeButtonState() {
        tourLogTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourLogTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtonState());

        tourListView.getItems().addListener((javafx.collections.ListChangeListener<Tour>) change -> updateButtonState());

        updateButtonState();
        logger.info("Initializing Button State");
    }

    //Aus und einblenden
    private void updateButtonState() {
        logger.info("Updating tour log button state");
        int selectedCount = tourLogTableView.getSelectionModel().getSelectedItems().size();
        boolean isListEmpty = tourListView.getItems().isEmpty(); // Prüfen, ob die Liste leer ist

        newTourLogButton.setDisable(isListEmpty);

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
        logger.info("Reset tour log button state");
    }
}