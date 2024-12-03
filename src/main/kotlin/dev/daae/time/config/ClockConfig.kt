package dev.daae.time.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class ClockConfig {
    @Bean
    open fun clock() = Clock.systemDefaultZone()
}
