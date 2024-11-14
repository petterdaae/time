package dev.daae.time.models

import lombok.Builder
import lombok.Value

@Value
@Builder
class StatusResponse {
    var status: String? = null

    var stats: StatusResponseStats? = null
}
