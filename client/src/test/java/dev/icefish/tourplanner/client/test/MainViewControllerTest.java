package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.MainViewController;
import dev.icefish.tourplanner.client.model.Tour;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class MainViewControllerTest extends TestFXBase {

    private MainViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourPlannerWindow.fxml")); // Ensure this path is correct
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testDeleteTour() {
        // Add a test tour
        Tour testTour = new Tour("Test Tour", "Description", "Vienna", "Salzburg", "Car");
        controller.addTourToViewModel(testTour);

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
}
