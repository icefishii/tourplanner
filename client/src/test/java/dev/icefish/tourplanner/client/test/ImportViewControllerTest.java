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
        // Arrange: mock returns success message
        when(mockImportService.importToursAndLogs(any(File.class)))
                .thenReturn("Tours and tour logs imported successfully!");

        // Act: simulate UI input and click import
        clickOn("#filePathField").write("fakepath.json");
        clickOn("#importButton");

        // Assert: importService called once
        verify(mockImportService, times(1)).importToursAndLogs(any(File.class));
        // You can add assertions for alerts/windows if you mock ControllerUtils/WindowUtils
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
        // Arrange: mock returns error message
        when(mockImportService.importToursAndLogs(any(File.class)))
                .thenReturn("Error importing tours");

        // Act: simulate UI input and click import
        clickOn("#filePathField").write("fakepath.json");
        clickOn("#importButton");

        // Assert: importService called once
        verify(mockImportService, times(1)).importToursAndLogs(any(File.class));
        // You could verify error alert shown by mocking ControllerUtils.showErrorAlert
    }
}
