package dev.daae.time;

import static org.assertj.core.api.Assertions.assertThat;

import dev.daae.time.models.Session;
import dev.daae.time.repository.SessionRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class SessionRepositoryTests {

  @Autowired private SessionRepository repository;

  @Test
  void testThatLogIsReturnedFromRepositoryAfterItIsSaved() {
    var now = LocalDateTime.now().atOffset(ZoneOffset.UTC);
    var session = repository.save(Session.builder().start(now).end(now).build());
    repository.findById(session.getId()).orElseThrow();
  }

  @Test
  void testThatTimestampIsEqualBeforeAndAfterStoringIt() {
    var timestamp = LocalDateTime.now().atOffset(ZoneOffset.UTC);
    var id = repository.save(Session.builder().start(timestamp).build()).getId();
    var session = repository.findById(id).orElseThrow();
    assertThat(session.getStart()).isEqualTo(timestamp);
  }
}
