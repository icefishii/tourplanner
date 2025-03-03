package dev.icefish.tourplanner.mvvm;

import dev.icefish.tourplanner.models.Student;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class StudentTableView extends VBox {
    private final TableView<Student> tableView = new TableView<>();
    private final StudentViewModel viewModel;

    public StudentTableView(StudentViewModel viewModel) {
        this.viewModel = viewModel;

        // Spalten der Tabelle
        TableColumn<Student, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Student, Number> semesterColumn = new TableColumn<>("Semester");
        semesterColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSemester()));

        // Spalten zur Tabelle hinzufügen
        tableView.getColumns().addAll(idColumn, nameColumn, semesterColumn);
        tableView.setItems(viewModel.getStudents());

        // Buttons
        Button newButton = new Button("New");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        newButton.setOnAction(e -> viewModel.addStudent());
        editButton.setOnAction(e -> viewModel.editStudent());
        deleteButton.setOnAction(e -> viewModel.deleteStudent());

        // Auswahl-Listener für Tabelle
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewModel.selectedStudentProperty().set(newSelection);
            viewModel.loadSelectedStudent();
        });


        // Layout
        getChildren().addAll(tableView, newButton, editButton, deleteButton);
    }
}
