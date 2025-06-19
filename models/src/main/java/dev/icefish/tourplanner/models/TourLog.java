package dev.icefish.tourplanner.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
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

    public TourLog(UUID uuid, Tour newTour, String log, LocalDate now, int i, double v, int i1, int i2) {
        this.id = uuid;
        this.tour = newTour;
        this.comment = log;
        this.date = Timestamp.valueOf(now.atStartOfDay());
        this.difficulty = i;
        this.distance = v;
        this.durationText = String.valueOf(i1);
        this.rating = i2;
    }
}