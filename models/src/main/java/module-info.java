module dev.icefish.tourplanner.models {
    requires jakarta.persistence;
    requires static lombok;

    // Allow Hibernate to access the models for proxying
    opens dev.icefish.tourplanner.models;

    exports dev.icefish.tourplanner.models;
}