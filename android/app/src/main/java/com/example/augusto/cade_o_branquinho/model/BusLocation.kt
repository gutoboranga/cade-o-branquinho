package com.example.augusto.cade_o_branquinho.model

import android.location.Location
import android.location.LocationManager

class BusLocation() {

    private var location: Location = Location(LocationManager.GPS_PROVIDER)
    private var bearing: Float = 0f

    constructor(latitude: Double, longitude: Double, bearing: Float) : this() {
        this.location.latitude = latitude
        this.location.longitude = longitude

        this.bearing = bearing
    }

    fun getLocation(): Location {
        return this.location
    }

    fun getLatitude(): Double {
        return location.latitude
    }

    fun getLongitude(): Double {
        return location.longitude
    }

    fun getBearing(): Float {
        return bearing
    }

}