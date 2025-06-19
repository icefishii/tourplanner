package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.ImportViewController;
import dev.icefish.tourplanner.client.services.ImportService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class ImportViewControllerTest extends TestFXBase {

    private ImportViewController controller;
    private ImportService mockImportService;

    @Start
    public void start(Stage stage) throws Exception {
        mockImportService = Mockito.mock(ImportService.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ImportWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setImportService(mockImportService);  // inject mock


        stage.setScene(new Scene(root, 400, 200));
        stage.show();
    }

    @Test
    public void testImportSuccess() {
        // Arrange: mock loadFromFile returns dummy data
        Map<String, Object> fakeData = Map.of(
                "tours", List.of(/* mock Tour objects */),
                "tourLogs", List.of(/* mock TourLog objects */)
        );
        when(mockImportService.loadFromFile(any(File.class))).thenReturn(fakeData);

        // Act: simulate UI input and click import
        clickOn("#filePathField").write("fakepath.json");
        clickOn("#importButton");

        // Assert: loadFromFile called once
        verify(mockImportService, times(1)).loadFromFile(any(File.class));
        verify(mockImportService, never()).importToursAndLogs(any(File.class));
    }


    @Test
    public void testImportNoFileSelected() {
        // Act: click import with empty file path
        clickOn("#filePathField").eraseText(10);
        clickOn("#importButton");

        // Assert: importService not called
        verify(mockImportService, never()).importToursAndLogs(any(File.class));
    }

    @Test
    public void testImportErrorResponse() {
        // Arrange: mock loadFromFile returns error data or null tours
        Map<String, Object> errorData = Map.of(
                "tours", List.of(),
                "error", "Error importing tours"
        );

        when(mockImportService.loadFromFile(any(File.class))).thenReturn(errorData);

        // Act: simulate UI input and click import
        clickOn("#filePathField").write("fakepath.json");
        clickOn("#importButton");

        // Assert: loadFromFile called once
        verify(mockImportService, times(1)).loadFromFile(any(File.class));
        // Verify importToursAndLogs NOT called
        verify(mockImportService, never()).importToursAndLogs(any(File.class));

        // Optional: verify UI shows error alert (if applicable)
    }

}
