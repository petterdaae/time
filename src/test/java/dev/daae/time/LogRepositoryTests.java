package dev.daae.time;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class LogRepositoryTests {

    @Autowired
    private LogRepository repository;

    @Test
    void testThatLogIsReturnedFromRepositoryAfterItIsSaved() {
        var log = repository.save(new Log("description", LocalDateTime.now().atOffset(ZoneOffset.UTC)));
        repository.findById(log.getId()).orElseThrow();
    }

    @Test
    void testThatTimestampIsEqualBeforeAndAfterStoringIt() {
        var timestamp = LocalDateTime.now().atOffset(ZoneOffset.UTC);
        var id = repository.save(new Log("description", timestamp)).getId();
        var log = repository.findById(id).orElseThrow();
        assertThat(log.getTimestamp()).isEqualTo(timestamp);
    }
}
