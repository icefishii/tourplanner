module dev.icefish.tourplanner.server {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.data.jpa;
    requires dev.icefish.tourplanner.models;
    requires jakarta.persistence;
    requires spring.webmvc;
    requires spring.core;
    requires spring.beans;
    requires spring.orm;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens dev.icefish.tourplanner.server to spring.core, spring.beans, spring.context;
    opens dev.icefish.tourplanner.server.config to spring.core, spring.beans, spring.context;
    opens dev.icefish.tourplanner.server.controller to spring.core, spring.beans, spring.context;
    opens dev.icefish.tourplanner.server.repository;

    // Exports for Spring components
    exports dev.icefish.tourplanner.server;
    exports dev.icefish.tourplanner.server.repository;
    exports dev.icefish.tourplanner.server.config;
    exports dev.icefish.tourplanner.server.controller;
}