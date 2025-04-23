package dev.icefish.tourplanner.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("dev.icefish.tourplanner.models")
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}