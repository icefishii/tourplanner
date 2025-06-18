package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.server.service.TourLogService;
import dev.icefish.tourplanner.server.service.TourService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory
        .annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private static final Logger logger = LogManager.getLogger(TourController.class);

    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        logger.info("Fetching all tours");
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable UUID id) {
        logger.info("Fetching tour with id: {}", id);
        return tourService.getTourById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        logger.info("Creating new tour: {}", tour);
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.saveTour(tour));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable UUID id, @RequestBody Tour tour) {
        logger.info("Updating tour with id: {}", id);
        return tourService.getTourById(id)
                .map(existingTour -> {
                    tour.setId(id);
                    return ResponseEntity.ok(tourService.saveTour(tour));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable UUID id) {
        logger.info("Deleting tour with id: {}", id);
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<Tour>> importTours(@RequestBody List<Tour> tours) {
        logger.info("Importing tours: {}", tours);
        List<Tour> importedTours = tourService.importTours(tours);
        return ResponseEntity.ok(importedTours);
    }

    @GetMapping("/export")
    public ResponseEntity<List<Tour>> exportTours() {
        logger.info("Exporting all tours");
        return ResponseEntity.ok(tourService.getAllTours());
    }
}