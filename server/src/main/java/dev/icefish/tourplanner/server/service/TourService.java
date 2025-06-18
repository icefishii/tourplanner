package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.exceptions.RepositoryException;
import dev.icefish.tourplanner.models.exceptions.ServiceException;
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
        try {
            logger.info("Fetching all tours");
            return tourRepository.findAll();
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to fetch all tours");
        }
    }

    public Optional<Tour> getTourById(UUID id) {
        try {
            logger.info("Fetching tour with id: {}", id);
            return tourRepository.findById(id);
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to fetch tour by id");
        }
    }

    public Tour saveTour(Tour tour) {
        try {
            logger.info("Saving tour: {}", tour);
            Tour t = tourRepository.save(tour);
            tourRepository.flush();
            return t;
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to save tour");
        }
    }

    public void deleteTour(UUID id) {
        try {
            logger.info("Deleting tour with id: {}", id);
            tourRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to delete tour");
        }
    }

    public List<Tour> importTours(List<Tour> tours) {
        List<Tour> importedTours = new ArrayList<>();
        for (Tour tour : tours) {
            try {
                if (!tourRepository.existsById(tour.getId())) {
                    importedTours.add(tourRepository.save(tour));
                } else {
                    logger.warn("Tour with ID {} already exists. Skipping import.", tour.getId());
                }
            } catch (Exception e) {
                logger.error("Repository error: {}", e.getMessage());
                throw new RepositoryException("Failed to import tours");
            }
        }
        return importedTours;
    }
}