package dev.icefish.tourplanner.client.test;


import dev.icefish.tourplanner.client.controllers.TourCreateViewController;
import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.services.OpenRouteService;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.models.Tour;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import static org.testfx.api.FxToolkit.setupFixture;

@ExtendWith(ApplicationExtension.class)
public class TourCreateViewTest extends TestFXBase {

    private TourCreateViewController controller;
    private CountDownLatch tourCreatedLatch;
    private Tour createdTour;

    @Start
    public void start(Stage stage) throws IOException {
        // Set up the controller
        controller = new TourCreateViewController();
        tourCreatedLatch = new CountDownLatch(1);
        controller.setTourCreatedListener(new Consumer<Tour>() {
            @Override
            public void accept(Tour tour) {
                createdTour = tour;
                tourCreatedLatch.countDown();
            }
        });

        // Load the FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        // Set up the stage
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    @Test
    public void testCreateTour() {
        CountDownLatch tourCreatedLatch = new CountDownLatch(1);

        try (MockedStatic<GeoCoder> geocoderMock = Mockito.mockStatic(GeoCoder.class);
             MockedStatic<OpenRouteService> orsMock = Mockito.mockStatic(OpenRouteService.class);
             MockedStatic<ConfigLoader> configMock = Mockito.mockStatic(ConfigLoader.class);
             MockedStatic<MapService> mapServiceMock = Mockito.mockStatic(MapService.class)) {

            // Mocks...
            configMock.when(() -> ConfigLoader.get("openrouteservice.api.key")).thenReturn("mock-api-key");
            configMock.when(() -> ConfigLoader.get("image.basePath")).thenReturn("maps");

            double[] mockCoords = new double[]{48.2082, 16.3719};
            geocoderMock.when(() -> GeoCoder.getCoordinates(anyString())).thenReturn(mockCoords);

            OpenRouteService.RouteInfo mockRouteInfo = new OpenRouteService.RouteInfo(300.5, 3.5);
            orsMock.when(() -> OpenRouteService.getRouteInfo(anyString(), anyString(), anyString()))
                    .thenReturn(mockRouteInfo);

            mapServiceMock.when(() -> MapService.saveWebViewSnapshot(any(), any(), any()))
                    .then(invocation -> null);

            // Set field before triggering event
            Field mapLoadedField = TourCreateViewController.class.getDeclaredField("mapLoaded");
            mapLoadedField.setAccessible(true);
            mapLoadedField.set(controller, true);

            controller.setTourCreatedListener(tour -> {
                createdTour = tour;
                tourCreatedLatch.countDown();
            });

            // Perform UI interaction
            clickOn("#tourNameField").write("Test Tour");
            clickOn("#tourDescriptionField").write("Test Description");
            clickOn("#fromLocationField").write("Vienna");
            clickOn("#toLocationField").write("Salzburg");
            clickOn("#transportTypeBox").clickOn("Car");

            clickOn("#createButton");

            // Wait for async processing
            boolean success = tourCreatedLatch.await(10, TimeUnit.SECONDS);
            assertThat(success).isTrue();

            // Assertions...
            assertThat(createdTour).isNotNull();
            assertThat(createdTour.getDistance()).isCloseTo(300.5, within(5.0));
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage(), e);
        }
    }

}