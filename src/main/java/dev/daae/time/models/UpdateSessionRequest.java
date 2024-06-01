package dev.daae.time.models;

import java.util.Optional;

public class UpdateSessionRequest {

    Integer plus;
    Integer minus;

    public Optional<Integer> getPlus() {
        return Optional.ofNullable(plus);
    }

    public Optional<Integer> getMinus() {
        return Optional.ofNullable(minus);
    }
}
