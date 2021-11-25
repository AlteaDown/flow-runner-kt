package io.viamo.flow.runner.ext

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toUtcDate(): LocalDate = this.toLocalDateTime(TimeZone.UTC).date

@Deprecated("Just use the Instant", ReplaceWith("this"))
fun Instant.toUtcDateTime(): Instant = this
