package dev.icefish.tourplanner;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import dev.icefish.tourplanner.models.Student;
import dev.icefish.tourplanner.mvvm.StudentViewModel;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentManagementController {
    private final StudentViewModel viewModel = new StudentViewModel();

    @FXML
    private TableView<Student> tableView;
    @FXML
    private TableColumn<Student, String> idColumn;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, Integer> semesterColumn;
    @FXML
    private ComboBox<String> myComboBox;

    @FXML
    private Button newButton, editButton, deleteButton, okButton, cancelButton;

    public void initialize() {
        tableView.setItems(viewModel.getStudents());

        // Zellwerte setzen
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        semesterColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSemester()).asObject());

        // Button-Aktionen
        newButton.setOnAction(e -> viewModel.addStudent());
        //editButton.setOnAction(e -> openEditDialog());
        deleteButton.setOnAction(e -> viewModel.deleteStudent());

        // Buttons standardmäßig deaktivieren
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Auswahl-Listener für Tabelle
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewModel.selectedStudentProperty().set(newSelection);
            viewModel.loadSelectedStudent();

            boolean selectionNotEmpty = newSelection != null;
            editButton.setDisable(!selectionNotEmpty);
            deleteButton.setDisable(!selectionNotEmpty);
        });
    }

}