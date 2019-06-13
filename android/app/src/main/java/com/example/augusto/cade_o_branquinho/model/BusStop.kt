package com.example.augusto.cade_o_branquinho.model

import com.example.augusto.cade_o_branquinho.utils.DepartureTime

class BusStop {

    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var nextTime: DepartureTime = DepartureTime(0,0)

    constructor(name: String, latitude: Double, longitude: Double) {
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }

}