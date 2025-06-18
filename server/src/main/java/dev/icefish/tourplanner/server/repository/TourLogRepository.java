package dev.icefish.tourplanner.server.repository;

import dev.icefish.tourplanner.models.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TourLogRepository extends JpaRepository<TourLog, UUID> {
    List<TourLog> findByTourId(UUID tourId);
}