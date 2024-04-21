package dev.daae.time;

import dev.daae.time.models.Log;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LogRepository extends CrudRepository<Log, Long> {
    List<Log> findAllByOrderByTimestampDesc();
}
