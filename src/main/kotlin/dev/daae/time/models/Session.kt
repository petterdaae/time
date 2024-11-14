package dev.daae.time.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.OffsetDateTime

@Entity
class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var start: OffsetDateTime? = null,
    @Column(name = "_end")
    var end: OffsetDateTime? = null,
) {
    companion object {
        @JvmStatic
        fun newWithStartTime(start: OffsetDateTime): Session? {
            return Session(null, start, null)
        }
    }
}
