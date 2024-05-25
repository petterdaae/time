package dev.daae.time.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull private OffsetDateTime start;

  @Column(name = "_end")
  private OffsetDateTime end;

  public Optional<OffsetDateTime> getEnd() {
    return Optional.ofNullable(end);
  }

  public static Session newWithStartTime(OffsetDateTime start) {
    return Session.builder().start(start).build();
  }
}
