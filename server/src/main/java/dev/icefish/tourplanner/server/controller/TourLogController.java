package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.server.service.TourLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/tourlogs")
public class TourLogController {

    private static final Logger logger = LogManager.getLogger(TourLogController.class);

    private final TourLogService tourLogService;

    @Autowired
    public TourLogController(TourLogService tourLogService) {
        logger.info("TourLogController initialized");
        this.tourLogService = tourLogService;
    }

    @GetMapping
    public ResponseEntity<List<TourLog>> getAllTourLogs() {
        logger.info("Fetching all tour logs");
        return ResponseEntity.ok(tourLogService.getAllTourLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourLog> getTourLogById(@PathVariable UUID id) {
        logger.info("Fetching tour log with id: {}", id);
        return tourLogService.getTourLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourLog>> getTourLogsByTourId(@PathVariable UUID tourId) {
        logger.info("Fetching tour logs for tour id: {}", tourId);
        return ResponseEntity.ok(tourLogService.getTourLogsByTourId(tourId));
    }

    @PostMapping
    public ResponseEntity<TourLog> createTourLog(@RequestBody TourLog tourLog) {
        logger.info("Creating new tour log: {}", tourLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(tourLogService.saveTourLog(tourLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourLog> updateTourLog(@PathVariable UUID id, @RequestBody TourLog tourLog) {
        logger.info("Updating tour log with id: {}", id);
        return tourLogService.getTourLogById(id)
                .map(existingTourLog -> {
                    tourLog.setId(id);
                    return ResponseEntity.ok(tourLogService.saveTourLog(tourLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourLog(@PathVariable UUID id) {
        logger.info("Deleting tour log with id: {}", id);
        tourLogService.deleteTourLog(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<TourLog>> importTourLogs(@RequestBody List<TourLog> tourLogs) {
        logger.info("Importing tour logs: {}", tourLogs);
        List<TourLog> importedTourLogs = tourLogService.importTourLogs(tourLogs);
        return ResponseEntity.ok(importedTourLogs);
    }

    @GetMapping("/export")
    public ResponseEntity<List<TourLog>> exportTourLogs() {
        return ResponseEntity.ok(tourLogService.getAllTourLogs());
    }
}