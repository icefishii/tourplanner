module dev.icefish.tourplanner.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires dev.icefish.tourplanner.models;
    requires com.google.gson;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires javafx.web;
    requires com.github.librepdf.openpdf;
    requires java.desktop;


    opens dev.icefish.tourplanner.client to javafx.fxml;
    exports dev.icefish.tourplanner.client;
    exports dev.icefish.tourplanner.client.controllers;
    opens dev.icefish.tourplanner.client.controllers to javafx.fxml;
}