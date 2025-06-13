package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.server.service.TourService;
import org.springframework.beans.factory
        .annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable UUID id) {
        return tourService.getTourById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.saveTour(tour));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable UUID id, @RequestBody Tour tour) {
        return tourService.getTourById(id)
                .map(existingTour -> {
                    tour.setId(id);
                    return ResponseEntity.ok(tourService.saveTour(tour));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable UUID id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<Tour>> importTours(@RequestBody List<Tour> tours) {
        List<Tour> importedTours = tourService.importTours(tours);
        return ResponseEntity.ok(importedTours);
    }

    @GetMapping("/export")
    public ResponseEntity<List<Tour>> exportTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }
}