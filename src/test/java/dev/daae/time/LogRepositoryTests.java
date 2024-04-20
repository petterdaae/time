package dev.daae.time;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogRepositoryTests {

    @Autowired
    private LogRepository repository;

    @Test
    void testThatLogIsReturnedFromRepositoryAfterItIsSaved() {
        var log = repository.save(new Log("description"));
        repository.findById(log.getId()).orElseThrow();
    }
}
