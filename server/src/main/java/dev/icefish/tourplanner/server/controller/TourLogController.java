package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.server.service.TourLogService;
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

    private final TourLogService tourLogService;

    @Autowired
    public TourLogController(TourLogService tourLogService) {
        this.tourLogService = tourLogService;
    }

    @GetMapping
    public ResponseEntity<List<TourLog>> getAllTourLogs() {
        return ResponseEntity.ok(tourLogService.getAllTourLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourLog> getTourLogById(@PathVariable UUID id) {
        return tourLogService.getTourLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourLog>> getTourLogsByTourId(@PathVariable UUID tourId) {
        return ResponseEntity.ok(tourLogService.getTourLogsByTourId(tourId));
    }

    @PostMapping
    public ResponseEntity<TourLog> createTourLog(@RequestBody TourLog tourLog) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourLogService.saveTourLog(tourLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourLog> updateTourLog(@PathVariable UUID id, @RequestBody TourLog tourLog) {
        return tourLogService.getTourLogById(id)
                .map(existingTourLog -> {
                    tourLog.setId(id);
                    return ResponseEntity.ok(tourLogService.saveTourLog(tourLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourLog(@PathVariable UUID id) {
        tourLogService.deleteTourLog(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<TourLog>> importTourLogs(@RequestBody List<TourLog> tourLogs) {
        List<TourLog> importedTourLogs = tourLogService.importTourLogs(tourLogs);
        return ResponseEntity.ok(importedTourLogs);
    }

    @GetMapping("/export")
    public ResponseEntity<List<TourLog>> exportTourLogs() {
        return ResponseEntity.ok(tourLogService.getAllTourLogs());
    }
}