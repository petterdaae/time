package dev.daae.time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.OffsetDateTime;

@Entity
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private OffsetDateTime timestamp;

    public Log() {}

    public Log(String description, OffsetDateTime timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
