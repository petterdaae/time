package dev.daae.time

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TimeApplication

fun main(args: Array<String>) {
    runApplication<TimeApplication>(*args)
}
