package dev.icefish.tourplanner.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class TourLog {

    @Id
    private UUID id;

    @Version
    private Long version = 0L;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
    private Timestamp date;
    private String comment;
    private int difficulty;
    private double distance;
    private String durationText;
    private int rating;
}