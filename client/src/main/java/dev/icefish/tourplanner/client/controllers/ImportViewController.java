package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImportViewController {


    public TextField filePathField;
    public Button browseButton;
    public Button importButton;
    public Button cancelButton;

    public void onBrowse(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to import");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Fenster als Basis für den Dialog
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    public void onImport(ActionEvent actionEvent) {
        //???
    }

    public void onCancel(ActionEvent actionEvent) {
        WindowUtils.close(filePathField);
    }

}
