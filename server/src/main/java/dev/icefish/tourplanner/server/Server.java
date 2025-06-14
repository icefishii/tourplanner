package dev.icefish.tourplanner.server;

import dev.icefish.tourplanner.models.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;

@SpringBootApplication
@EntityScan("dev.icefish.tourplanner.models")
public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("Starting TourPlanner Server");
        SpringApplication.run(Server.class, args);
    }
}

//Todo Add logging (B)

//Todo Add tests (B)