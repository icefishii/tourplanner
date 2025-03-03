package dev.icefish.tourplanner.models;

import java.util.UUID;

public class Student {
    //Benjamin Foto
    private UUID id;
    private String name;
    private int semester;

    public Student(UUID id,String name, int semester) {
        this.id = id; // Generiert eine eindeutige ID
        this.name = name;
        this.semester = semester;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return name + " (Semester " + semester + ")";
    }




}

