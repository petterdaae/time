package dev.daae.time;

import dev.daae.time.models.Log;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
  List<Log> findAllByOrderByTimestampDesc();
}
