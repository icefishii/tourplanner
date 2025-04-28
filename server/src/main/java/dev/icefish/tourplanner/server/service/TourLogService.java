package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.server.repository.TourLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TourLogService {


    private final TourLogRepository tourLogRepository;
    private static final Logger logger = LogManager.getLogger(TourLogService.class);

    @Autowired
    public TourLogService(TourLogRepository tourLogRepository) {
        this.tourLogRepository = tourLogRepository;
    }

    public List<TourLog> getAllTourLogs() {
        logger.info("Fetching all tour logs");
        return tourLogRepository.findAll();
    }

    public Optional<TourLog> getTourLogById(UUID id) {
        logger.info("Fetching tour log with id: {}", id);
        return tourLogRepository.findById(id);
    }

    public List<TourLog> getTourLogsByTourId(UUID tourId) {
        logger.info("Fetching tour logs for tour id: {}", tourId);
        return tourLogRepository.findByTourId(tourId);
    }

    public TourLog saveTourLog(TourLog tourLog) {
        logger.info("Saving tour log: {}", tourLog);
        return tourLogRepository.save(tourLog);
    }

    public void deleteTourLog(UUID id) {
        logger.info("Deleting tour log with id: {}", id);
        tourLogRepository.deleteById(id);
    }
}