package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.MainViewController;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class MainViewControllerTest extends TestFXBase {

    private MainViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourPlannerWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testDeleteTour() {
        // Add a test tour
        Tour testTour = new Tour(UUIDv7Generator.generateUUIDv7(),"Test Tour", "Description", "Vienna", "Salzburg", "Car");
        controller.getTourViewModel().createNewTour(testTour);

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();

        // Select the tour in the list
        clickOn(testTour.getName());

        // Click delete and confirm
        clickOn("#deleteTourButton");
        clickOn("Yes");

        // Verify tour was deleted
        ListView<Tour> listView = find("#tourListView");
        assertEquals(0, listView.getItems().size(), "Tour should have been deleted");
    }

    @Test
    public void testDeleteTourLog() {
        // Add a test tour
        Tour testTour = new Tour(UUIDv7Generator.generateUUIDv7(),"Test Tour", "Description", "Vienna", "Salzburg", "Car");
        controller.getTourViewModel().createNewTour(testTour);

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();

        // Select the tour first
        clickOn("Test Tour");

        // Add a test tour log for this tour
        TourLog testTourLog = new TourLog(testTour.getId(), Timestamp.valueOf("2023-01-01 10:00:00"), "Test Comment", 3, 10, "60", 4);
        controller.getTourLogViewModel().createNewTourLog(testTourLog);

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();

        // Get the TableView and select the first row
        TableView<TourLog> tableView = find("#tourLogTableView");
        interact(() -> {
            tableView.getSelectionModel().select(0);
        });

        // Verify that the table has one item before deletion
        assertEquals(1, tableView.getItems().size(), "Table should have one tour log");

        // Click delete and confirm
        clickOn("#deleteTourLogButton");
        clickOn("Yes");

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();

        // Verify tour log was deleted
        assertEquals(0, tableView.getItems().size(), "Tour log should have been deleted");
    }
}