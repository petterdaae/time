package dev.daae.time;

import dev.daae.time.models.Log;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
  List<Log> findAllByOrderByTimestampDesc();

  Optional<Log> findFirstByOrderByTimestampDesc();

  List<Log> findFirst2ByOrderByTimestampDesc();
}
