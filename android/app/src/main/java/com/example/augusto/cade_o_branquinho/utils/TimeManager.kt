package com.example.augusto.cade_o_branquinho.utils

import java.util.*
import kotlin.math.min

class TimeManager() {

    val SUN = 0
    val SAT = 6

    fun getLastDeparture(): DepartureTime? {

        val now = now()

        when(now.day) {
            in 1..(SAT-1) -> return getLastDepartureWeek()
            SAT -> return getLastDepartureSaturday()
            else -> return null
        }
    }

    private fun getLastDepartureWeek(): DepartureTime {
        val times = DepartureTimes.weekdays

        var last = DepartureTime(0,0)
        val now= now()

        for (i in 0..(times.size - 1)) {
            val time= now()

            time.hours = times[i].hour
            time.minutes = times[i].minute

            if (now.compareTo(time) >= 0) {
                last = times[i]
            } else {
                break
            }
        }

        return last
    }

    fun getNextDepartureWeek(): DepartureTime? {
        val times = DepartureTimes.weekdays

        var next: DepartureTime? = DepartureTime(0,0)
        val now= now()

        for (i in 0..(times.size - 1)) {
            val time= now()

            time.hours = times[i].hour
            time.minutes = times[i].minute

            if (now.compareTo(time) < 0) {
                next = times[i]
                break
            }
        }

        return next
    }

    fun hasPassed(time: DepartureTime): Boolean {
        val timeDate= now()

        timeDate.hours = time.hour
        timeDate.minutes = time.minute

        return (now() > timeDate)
    }

    private fun getLastDepartureSaturday(): DepartureTime {
        val times = DepartureTimes.saturdays

        var last = DepartureTime(0,0)
        val now= now()

        for (i in 0..(times.size - 1)) {
            val time= now()

            time.hours = times[i].hour
            time.minutes = times[i].minute

            if (now.compareTo(time) >= 0) {
                last = times[i]
            } else {
                break
            }
        }

        return last
    }

    private fun now(): Date {
        return Calendar.getInstance().time
    }

}