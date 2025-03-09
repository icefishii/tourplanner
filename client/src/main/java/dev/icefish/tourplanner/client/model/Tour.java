package dev.icefish.tourplanner.client.model;


import dev.icefish.tourplanner.client.utils.UUIDv7Generator;

import java.util.UUID;

public class Tour {
    private UUID id;
    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private double estimatedTime;
    private String routeImagePath;


    //wenn noch nicht die time und distance mit API berechnet wird
    public Tour(String name, String description, String fromLocation, String toLocation, String transportType) {
        this.id = UUIDv7Generator.generateUUIDv7();        this.name = name;
        this.description = description;
        this.toLocation = toLocation;
        this.fromLocation = fromLocation;
        this.transportType = transportType;
    }

    public Tour(String name, String description, String fromLocation, String toLocation, String transportType, double distance, double estimatedTime, String routeImagePath) {
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.description = description;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.transportType = transportType;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.routeImagePath = routeImagePath;
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Tour Name: ").append(name).append(", ")
                .append("Description: ").append(description).append(", ")
                .append("From: ").append(fromLocation).append(", ")
                .append("To: ").append(toLocation).append(", ")
                .append("Transport Type: ").append(transportType).append(", ");

        if (routeImagePath != null && distance > 0) {
            sb.append("Distance: ").append(distance).append(" km, ");
        }

        if (routeImagePath != null && estimatedTime > 0) {
            sb.append("Estimated Time: ").append(estimatedTime).append(" hours, ");
        }

        if (routeImagePath != null && !routeImagePath.isEmpty()) {
            sb.append("Route Image: ").append(routeImagePath).append(", ");
        }

        return sb.toString();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getRouteImagePath() {
        return routeImagePath;
    }

    public void setRouteImagePath(String routeImagePath) {
        this.routeImagePath = routeImagePath;
    }

    //Vllt Tourlogs eine Liste aus Tours??

}

