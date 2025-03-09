package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourEditViewController;
import dev.icefish.tourplanner.client.model.Tour;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class TourEditViewControllerTest extends TestFXBase {

    private TourEditViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // Use a custom controller factory to use TourEditViewController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        loader.setControllerFactory(param -> new TourEditViewController());
        Parent root = loader.load();

        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testEditTour() {
        // Set up a listener to capture the updated tour
        AtomicReference<Tour> updatedTourRef = new AtomicReference<>();
        Platform.runLater(() -> controller.setTourUpdatedListener(updatedTourRef::set));

        // Create a tour and set it in the controller
        Tour tour = new Tour("Original Name", "Original Description", "Original From", "Original To", "Walk");
        Platform.runLater(() -> controller.setTour(tour));

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();

        // Clear and fill the form fields with new values
        clickOn("#tourNameField").press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL).write("Updated Name");
        clickOn("#tourDescriptionField").press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL).write("Updated Description");
        clickOn("#fromLocationField").press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL).write("Updated From");
        clickOn("#toLocationField").press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL).write("Updated To");
        clickOn("#transportTypeBox").clickOn("Car");

        // Click the save button
        clickOn("#createButton");

        // Verify the tour was updated with correct values
        Tour updatedTour = updatedTourRef.get();
        assertNotNull(updatedTour, "Tour should have been updated");
        assertEquals("Updated Name", updatedTour.getName());
        assertEquals("Updated Description", updatedTour.getDescription());
        assertEquals("Updated From", updatedTour.getFromLocation());
        assertEquals("Updated To", updatedTour.getToLocation());
        assertEquals("Car", updatedTour.getTransportType());
    }
}