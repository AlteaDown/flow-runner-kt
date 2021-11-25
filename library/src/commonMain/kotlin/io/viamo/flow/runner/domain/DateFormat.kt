package io.viamo.flow.runner.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * TODO: Kill it with fire. Just use ISO 8601, and we won't have to deal with the billion possible problems this presents.
 * Extracted function encapsulating date String formatted like "2020-01-17 17:58:08.090Z".
 * @param date
 */
fun createFormattedDate(date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)) = date.toString()
