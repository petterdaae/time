package dev.daae.time;

import dev.daae.time.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByOrderByTimestampDesc();
}
