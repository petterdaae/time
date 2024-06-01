package dev.daae.time.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class StatusResponse {

    @NonNull
    String status;

    @NonNull
    StatusResponseStats stats;
}
