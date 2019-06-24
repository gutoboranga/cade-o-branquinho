package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.InputStream
import android.location.LocationManager
import com.example.augusto.cade_o_branquinho.model.BusLocation


class JsonUtils(private var mContext: Context) {

    fun readFile(filename: String) : String? {
        return try {
            val inputStream: InputStream = mContext.assets.open(filename)
            val inputString = inputStream.bufferedReader().use{it.readText()}
            inputString
        } catch (e:Exception){
            null
        }
    }

    fun getBusLocation(message: String): BusLocation {
        val jsonObj = JSONObject(message)

        val lat = jsonObj.getDouble("latitude")
        val long = jsonObj.getDouble("longitude")
        val bearing = jsonObj.getDouble("bearing").toFloat()

        return BusLocation(lat, long, bearing)
    }

}