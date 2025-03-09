package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourCreateViewController;
import dev.icefish.tourplanner.client.model.Tour;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class TourCreateViewControllerTest extends TestFXBase {

    private TourCreateViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testCreateTour() {
        // Set up a listener to capture the created tour
        AtomicReference<Tour> createdTourRef = new AtomicReference<>();
        controller.setTourCreatedListener(createdTourRef::set);

        // Fill in the form fields
        clickOn("#tourNameField").write("Test Tour");
        clickOn("#tourDescriptionField").write("Test Description");
        clickOn("#fromLocationField").write("Vienna");
        clickOn("#toLocationField").write("Salzburg");
        clickOn("#transportTypeBox").clickOn("Car");

        // Click the create button
        clickOn("#createButton");

        // Verify the tour was created with correct values
        Tour createdTour = createdTourRef.get();
        assertNotNull(createdTour, "Tour should have been created");
        assertEquals("Test Tour", createdTour.getName());
        assertEquals("Test Description", createdTour.getDescription());
        assertEquals("Vienna", createdTour.getFromLocation());
        assertEquals("Salzburg", createdTour.getToLocation());
        assertEquals("Car", createdTour.getTransportType());
    }
}
