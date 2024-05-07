package dev.daae.time;

import static org.assertj.core.api.Assertions.*;

import dev.daae.time.models.Log;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class LogRepositoryTests {

  @Autowired private LogRepository repository;

  @Test
  void testThatLogIsReturnedFromRepositoryAfterItIsSaved() {
    var log =
        repository.save(
            Log.builder()
                .kind(Log.Kind.STOP)
                .timestamp(LocalDateTime.now().atOffset(ZoneOffset.UTC))
                .build());
    repository.findById(log.getId()).orElseThrow();
  }

  @Test
  void testThatTimestampIsEqualBeforeAndAfterStoringIt() {
    var timestamp = LocalDateTime.now().atOffset(ZoneOffset.UTC);
    var id =
        repository.save(Log.builder().kind(Log.Kind.START).timestamp(timestamp).build()).getId();
    var log = repository.findById(id).orElseThrow();
    assertThat(log.getTimestamp()).isEqualTo(timestamp);
  }
}
