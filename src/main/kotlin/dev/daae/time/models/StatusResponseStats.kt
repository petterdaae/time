package dev.daae.time.models

import lombok.Builder
import lombok.Value

@Value
@Builder
class StatusResponseStats {
    var previous: String? = null
}
