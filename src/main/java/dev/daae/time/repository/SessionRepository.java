package dev.daae.time.repository;

import dev.daae.time.models.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {

  Optional<Session> findFirstByOrderByStartDesc();
}
