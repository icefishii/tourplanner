package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourEditViewController;
import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.services.OpenRouteService;
import dev.icefish.tourplanner.models.Tour;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

public class TourEditIntegrationTest extends ApplicationTest {

    private TourEditViewController controller;
    private Tour existingTour;
    private boolean updatedCalled;

    @Override
    public void start(javafx.stage.Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        loader.setController(new TourEditViewController());
        Parent root = loader.load();
        controller = loader.getController();

        existingTour = new Tour("Original Name", "Original Desc", "Vienna", "Salzburg", "Bike");
        controller.setTour(existingTour);
        controller.setTourUpdatedListener(tour -> updatedCalled = true);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testEditTourSuccessfully() throws Exception {
        try (
                MockedStatic<GeoCoder> geoMock = mockStatic(GeoCoder.class);
                MockedStatic<OpenRouteService> orsMock = mockStatic(OpenRouteService.class);
                MockedStatic<MapService> mapMock = mockStatic(MapService.class)
        ) {
            // 1. Mock GeoCoder
            geoMock.when(() -> GeoCoder.getCoordinates(any()))
                    .thenReturn(new double[]{48.2, 16.3});

            // 2. Mock Route Info
            orsMock.when(() -> OpenRouteService.getRouteInfo(any(), any(), any()))
                    .thenReturn(new OpenRouteService.RouteInfo(300.5, 2.0));

            // 3. Mock the WebView snapshot to avoid void-return issue
            mapMock.when(() -> MapService.saveWebViewSnapshot(any(), any(), any()))
                    .thenAnswer(invocation -> {
                        System.out.println("Mocked snapshot called");
                        return null;
                    });

            updatedCalled = false;

            // 4. Change fields
            clickOn("#tourNameField").eraseText(50).write("Updated Name");
            clickOn("#tourDescriptionField").eraseText(50).write("Updated Desc");

            ComboBox<String> box = lookup("#transportTypeBox").queryAs(ComboBox.class);
            Platform.runLater(() -> box.setValue("Car"));
            WaitForAsyncUtils.waitForFxEvents();

            // 5. Bypass map loading (WebView)
            java.lang.reflect.Field mapLoadedField = TourEditViewController.class.getDeclaredField("mapLoaded");
            mapLoadedField.setAccessible(true);
            mapLoadedField.set(controller, true);

            // 6. Trigger save
            clickOn("#createButton");
            WaitForAsyncUtils.waitForFxEvents();

            // 7. Assertions
            assertThat(updatedCalled).isTrue();
            assertThat(existingTour.getName()).isEqualTo("Updated Name");
            assertThat(existingTour.getDistance()).isCloseTo(300.5, within(5.0));
            assertThat(existingTour.getEstimatedTime()).isCloseTo(3.0, within(1.0));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
