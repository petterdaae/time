package dev.daae.time;

import dev.daae.time.models.Log;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<Log, Long> { }
