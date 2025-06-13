package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.server.Server;
import dev.icefish.tourplanner.server.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class TourService {
    private static final Logger logger = LogManager.getLogger(TourService.class);
    private final TourRepository tourRepository;

    @Autowired
    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> getAllTours() {
        logger.info("Fetching all tours");
        return tourRepository.findAll();
    }

    public Optional<Tour> getTourById(UUID id) {
        logger.info("Fetching tour with id: {}", id);
        return tourRepository.findById(id);
    }

    public Tour saveTour(Tour tour) {
        logger.info("Saving tour: {}", tour);
        Tour t = tourRepository.save(tour);
        tourRepository.flush();
        return t;

    }

    public void deleteTour(UUID id) {
        logger.info("Deleting tour with id: {}", id);
        tourRepository.deleteById(id);
    }

    public List<Tour> importTours(List<Tour> tours) {
        List<Tour> importedTours = new ArrayList<>();
        for (Tour tour : tours) {
            if (!tourRepository.existsById(tour.getId())) {
                importedTours.add(tourRepository.save(tour));
            } else {
                logger.warn("Tour with ID {} already exists. Skipping import.", tour.getId());
            }
        }
        return importedTours;
    }
}