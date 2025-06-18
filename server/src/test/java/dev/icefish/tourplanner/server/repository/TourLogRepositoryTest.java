package dev.icefish.tourplanner.server.repository;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@DataJpaTest
@EntityScan("dev.icefish.tourplanner.models")
public class TourLogRepositoryTest {

    @Autowired
    private TourLogRepository tourLogRepository;

    @Autowired
    private TourRepository tourRepository;

    @Test
    void saveAndFindTourLog() {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        tour.setName("Test Tour");
        tourRepository.save(tour);

        TourLog log = new TourLog();
        log.setId(UUID.randomUUID());
        log.setTour(tour); // Make sure to set the tour!
        tourLogRepository.save(log);

        assertTrue(tourLogRepository.findById(log.getId()).isPresent());
        List<TourLog> logs = tourLogRepository.findByTourId(tour.getId());
        assertFalse(logs.isEmpty());
    }
}