package com.example.augusto.cade_o_branquinho.utils

class DepartureTimes {

    companion object {
        lateinit var weekdays: ArrayList<DepartureTime>
        lateinit var saturdays: ArrayList<DepartureTime>
    }

}

class DepartureTime(val hour: Int, val minute: Int)