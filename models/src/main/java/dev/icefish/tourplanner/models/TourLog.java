package dev.icefish.tourplanner.models;

import java.sql.Timestamp;
import java.util.UUID;

public class TourLog {
    private UUID id;
    private UUID tourId;
    private Timestamp date;
    private String comment;
    private int difficulty;

    public TourLog(UUID id, UUID tourId, Timestamp date, String comment, int difficulty, double distance, String durationText, int rating) {
        this.id = id;
        this.tourId = tourId;
        this.date = date;
        this.comment = comment;
        this.difficulty = difficulty;
        this.distance = distance;
        this.durationText = durationText;
        this.rating = rating;
    }

    public UUID getTourId() {
        return tourId;
    }

    public void setTourId(UUID tourId) {
        this.tourId = tourId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    private double distance;
    private String durationText;
    private int rating;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
