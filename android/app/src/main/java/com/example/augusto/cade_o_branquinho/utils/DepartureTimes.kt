package com.example.augusto.cade_o_branquinho.utils

class DepartureTimes {

    companion object {
        lateinit var weekdays: ArrayList<DepartureTime>
        lateinit var saturdays: ArrayList<DepartureTime>
    }

}

class DepartureTime(var hour: Int, var minute: Int) {

    val MAX_HOUR = 24
    val MAX_MIN = 60

    fun sum(minutes: Int) {
        var newMinutes = this.minute + minutes
        var newHours = this.hour

        while (newMinutes >= MAX_MIN) {
            newMinutes -= MAX_MIN
            newHours += 1
        }
        this.minute = newMinutes

        while (newHours >= MAX_HOUR) {
            newHours -= MAX_HOUR
        }
        this.hour = newHours
    }

    fun getFormatted(): String {
        var min= this.minute
        var minStr = this.minute.toString()

        if (min < 10) {
            minStr = "0" + minStr
        }

        return this.hour.toString() + ":" + minStr
    }

}