package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.server.repository.TourLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourLogServiceTest {

    private TourLogRepository tourLogRepository;
    private TourLogService tourLogService;

    @BeforeEach
    void setUp() {
        tourLogRepository = mock(TourLogRepository.class);
        tourLogService = new TourLogService(tourLogRepository);
    }

    @Test
    void getAllTourLogs_returnsList() {
        List<TourLog> logs = List.of(new TourLog());
        when(tourLogRepository.findAll()).thenReturn(logs);

        List<TourLog> result = tourLogService.getAllTourLogs();

        assertEquals(1, result.size());
        verify(tourLogRepository).findAll();
    }

    @Test
    void getTourLogById_returnsTourLog() {
        UUID id = UUID.randomUUID();
        TourLog log = new TourLog();
        log.setId(id);
        when(tourLogRepository.findById(id)).thenReturn(Optional.of(log));

        Optional<TourLog> result = tourLogService.getTourLogById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void saveTourLog_savesAndReturnsTourLog() {
        TourLog log = new TourLog();
        when(tourLogRepository.save(log)).thenReturn(log);

        TourLog result = tourLogService.saveTourLog(log);

        assertEquals(log, result);
        verify(tourLogRepository).save(log);
    }

    @Test
    void deleteTourLog_deletesTourLog() {
        UUID id = UUID.randomUUID();

        tourLogService.deleteTourLog(id);

        verify(tourLogRepository).deleteById(id);
    }

    @Test
    void getTourLogsByTourId_returnsList() {
        UUID tourId = UUID.randomUUID();
        List<TourLog> logs = List.of(new TourLog());
        when(tourLogRepository.findByTourId(tourId)).thenReturn(logs);

        List<TourLog> result = tourLogService.getTourLogsByTourId(tourId);

        assertEquals(1, result.size());
        verify(tourLogRepository).findByTourId(tourId);
    }

    @Test
    void importTourLogs_importsNewLogs() {
        TourLog log = new TourLog();
        log.setId(UUID.randomUUID());
        List<TourLog> logs = List.of(log);

        when(tourLogRepository.existsById(log.getId())).thenReturn(false);
        when(tourLogRepository.save(log)).thenReturn(log);

        List<TourLog> result = tourLogService.importTourLogs(logs);

        assertEquals(1, result.size());
        verify(tourLogRepository).save(log);
    }
}