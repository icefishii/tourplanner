package dev.icefish.tourplanner.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Tour {

    @Id
    private UUID id;

    @Version
    private Long version = 0L;

    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private double estimatedTime;
    private String routeImagePath;

    // Constructor - No need to include 'version' here, JPA manages it.
    public Tour(String name, String description, String fromLocation, String toLocation, String transportType) {
        this.name = name;
        this.description = description;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.transportType = transportType;
    }

    // Lombok's @Data will generate getVersion() and setVersion()
    // but you generally shouldn't manually set the version.
}