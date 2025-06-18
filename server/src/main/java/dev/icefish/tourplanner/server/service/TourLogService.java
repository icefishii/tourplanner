package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.models.exceptions.RepositoryException;
import dev.icefish.tourplanner.server.repository.TourLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private static final Logger logger = LogManager.getLogger(TourLogService.class);

    @Autowired
    public TourLogService(TourLogRepository tourLogRepository) {
        this.tourLogRepository = tourLogRepository;
    }

    public List<TourLog> getAllTourLogs() {
        try {
            logger.info("Fetching all tour logs");
            return tourLogRepository.findAll();
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to fetch all tour logs");
        }
    }

    public Optional<TourLog> getTourLogById(UUID id) {
        try {
            logger.info("Fetching tour log with id: {}", id);
            return tourLogRepository.findById(id);
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to fetch tour log by id");
        }
    }

    public List<TourLog> getTourLogsByTourId(UUID tourId) {
        try {
            logger.info("Fetching tour logs for tour id: {}", tourId);
            return tourLogRepository.findByTourId(tourId);
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to fetch tour logs by tour id");
        }
    }

    public TourLog saveTourLog(TourLog tourLog) {
        try {
            logger.info("Saving tour log: {}", tourLog);
            return tourLogRepository.save(tourLog);
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to save tour log");
        }
    }

    public void deleteTourLog(UUID id) {
        try {
            logger.info("Deleting tour log with id: {}", id);
            tourLogRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Repository error: {}", e.getMessage());
            throw new RepositoryException("Failed to delete tour log");
        }
    }

    public List<TourLog> importTourLogs(List<TourLog> tourLogs) {
        List<TourLog> importedTourLogs = new ArrayList<>();
        for (TourLog tourLog : tourLogs) {
            try {
                if (!tourLogRepository.existsById(tourLog.getId())) {
                    importedTourLogs.add(tourLogRepository.save(tourLog));
                } else {
                    logger.warn("TourLog with ID {} already exists. Skipping import.", tourLog.getId());
                }
            } catch (Exception e) {
                logger.error("Repository error: {}", e.getMessage());
                throw new RepositoryException("Failed to import tour logs");
            }
        }
        return importedTourLogs;
    }
}