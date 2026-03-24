package com.anidra.areyouok.data.work

import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime


object CheckInReminderSchedule {

    // Option A
    private val slots = listOf(
        LocalTime.of(8, 0),
        LocalTime.of(12, 0),
        LocalTime.of(16, 0),
        LocalTime.of(19, 30),
    )

    fun now(): ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())

    /** Window is 08:00 (inclusive) to 20:00 (exclusive). */
    fun isWithinWindow(now: ZonedDateTime): Boolean {
        val t = now.toLocalTime()
        return !t.isBefore(LocalTime.of(8, 0)) && t.isBefore(LocalTime.of(20, 0))
    }

    /** Strictly after `now` (prevents "schedule same time again" loops). */
    fun nextSlotAfter(now: ZonedDateTime): ZonedDateTime {
        val today = now.toLocalDate()
        val zone = now.zone

        val nextToday = slots
            .map { time -> ZonedDateTime.of(today, time, zone) }
            .firstOrNull { it.isAfter(now) }

        return nextToday ?: ZonedDateTime.of(today.plusDays(1), slots.first(), zone)
    }

    fun tomorrowMorning(now: ZonedDateTime): ZonedDateTime =
        ZonedDateTime.of(now.toLocalDate().plusDays(1), slots.first(), now.zone)


}