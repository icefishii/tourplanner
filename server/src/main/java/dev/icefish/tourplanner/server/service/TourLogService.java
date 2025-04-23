package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.server.repository.TourLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TourLogService {

    private final TourLogRepository tourLogRepository;

    @Autowired
    public TourLogService(TourLogRepository tourLogRepository) {
        this.tourLogRepository = tourLogRepository;
    }

    public List<TourLog> getAllTourLogs() {
        return tourLogRepository.findAll();
    }

    public Optional<TourLog> getTourLogById(UUID id) {
        return tourLogRepository.findById(id);
    }

    public List<TourLog> getTourLogsByTourId(UUID tourId) {
        return tourLogRepository.findByTourId(tourId);
    }

    public TourLog saveTourLog(TourLog tourLog) {
        return tourLogRepository.save(tourLog);
    }

    public void deleteTourLog(UUID id) {
        tourLogRepository.deleteById(id);
    }
}