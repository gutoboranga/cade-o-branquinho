package com.example.augusto.cade_o_branquinho.utils

import com.example.augusto.cade_o_branquinho.model.BusStop
import com.example.augusto.cade_o_branquinho.model.DayType
import com.example.augusto.cade_o_branquinho.model.DepartureTime
import java.util.*
import kotlin.collections.ArrayList


class TimeManager() {

    val SUN = 0
    val SAT = 6
    val ONE_MINUTE_IN_MILLIS: Long = 60000

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

    fun getNextDeparture(dayType: DayType): DepartureTime {
        var times: ArrayList<DepartureTime>

        if (dayType == DayType.WEEKDAY) {
            times = DepartureTimes.weekdays
        } else if (dayType == DayType.SATURDAY) {
            times = DepartureTimes.saturdays
        } else {
            return DepartureTime.INVALID()
        }

        var next: DepartureTime = DepartureTime.INVALID()
        val now= now()
        println("CU")

        for (i in 0..(times.size - 1)) {
            val time= now()

            time.hours = times[i].hour
            time.minutes = times[i].minute

            if (now.compareTo(time) < 0) {
                println("FOUND ONE => ${times[i].hour}:${times[i].minute}")
                next = times[i]
                break
            }
        }

        return next
    }

    fun getNextDepartureSaturday(): DepartureTime? {
        val times = DepartureTimes.saturdays

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

    fun getTodayType(): DayType {
        val now = now()

        when(now.day) {
            in 1..(SAT-1) -> return DayType.WEEKDAY
            SAT -> return DayType.SATURDAY
            else -> return DayType.SUNDAY
        }
    }

    fun getNextTimeBusStop(delayFromStart: Int, dayType: DayType): DepartureTime {

        lateinit var times: ArrayList<DepartureTime>

        if (dayType == DayType.WEEKDAY) {
            times = DepartureTimes.weekdays
        } else if (dayType == DayType.SATURDAY) {
            times = DepartureTimes.saturdays
        } else {
            return DepartureTime.INVALID()
        }

        val now= now()

        // procura na lista o proximo horário para aquela parada específica
        for (i in 0..(times.size - 1)) {
            val departureTime= now()

            departureTime.hours = times[i].hour
            departureTime.minutes = times[i].minute

            val timeInMS = departureTime.getTime()
            val time = Date(timeInMS + delayFromStart * ONE_MINUTE_IN_MILLIS)

            if (now.compareTo(time) < 0) {
                return DepartureTime(time.hours, time.minutes)
            }
        }

        return DepartureTime.INVALID()

    }

    fun getDiffInMinutes(date1: Date, date2: Date): Int {
        /* this function get the difference between date1 and date2
     * represented in minutes
     * assume that the formate is HH:MM */
        val diff = date1.time - date2.time
        return ((diff / 1000) / 60).toInt()
    }

    companion object {
        fun nowDate(): Date {
            return Calendar.getInstance().time
        }
    }

}