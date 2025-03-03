package dev.icefish.tourplanner.mvvm;

import dev.icefish.tourplanner.models.Student;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class StudentViewModel {
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObjectProperty<Student> selectedStudent = new SimpleObjectProperty<>();

    // Properties für die Eingabefelder (Binding zur UI)
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final IntegerProperty semesterProperty = new SimpleIntegerProperty(1);

    public StudentViewModel() {
        // Optional: Testdaten
        students.add(new Student(UUID.randomUUID(),"Max Mustermann", 1));
        students.add(new Student(UUID.randomUUID(),"Lisa Beispiel", 3));
    }

    public ObservableList<Student> getStudents() {
        return students;
    }

    public ObjectProperty<Student> selectedStudentProperty() {
        return selectedStudent;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public IntegerProperty semesterProperty() {
        return semesterProperty;
    }

    public void addStudent() {
        if (!nameProperty.get().isEmpty()) {
            students.add(new Student(UUID.randomUUID(), nameProperty.get(), semesterProperty.get()));
            clearFields();
        }
    }

    public void editStudent() {
        Student student = selectedStudent.get();
        if (student != null) {
            student.setName(nameProperty.get());
            student.setSemester(semesterProperty.get());
        }
    }

    public void loadSelectedStudent() {
        if (selectedStudent.get() != null) {
            nameProperty.set(selectedStudent.get().getName());
            semesterProperty.set(selectedStudent.get().getSemester());
        }
    }

    private void clearFields() {
        nameProperty.set("");
        semesterProperty.set(1);
    }

    public void deleteStudent() {
        ObservableList<Student> selectedStudents = FXCollections.observableArrayList(selectedStudent.get());
        students.removeAll(selectedStudents);
    }




}
