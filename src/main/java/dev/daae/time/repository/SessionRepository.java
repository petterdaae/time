package dev.daae.time.repository;

import dev.daae.time.models.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findFirstByOrderByStartDesc();

    @Query(value = "select * from session where start >= date_trunc('week', now())", nativeQuery = true)
    List<Session> findSessionsThisWeek();
}
