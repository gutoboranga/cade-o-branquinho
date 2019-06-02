package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import com.example.augusto.cade_o_branquinho.model.BusStop
import org.json.JSONArray
import org.json.JSONObject

class BusStopLocationsUtils {

    private var context: Context? = null
    private var jsonUtils: JsonUtils? = null

    private val FILE_NAME = "bus_stops.json"
    private val NAME_TAG = "name"
    private val LATITUDE_TAG = "latitude"
    private val LONGITUDE_TAG = "longitude"

    constructor(context: Context) {
        this.context = context
        this.jsonUtils = JsonUtils(context)
    }

    fun getLocations() : ArrayList<BusStop> {
        val data: String = jsonUtils!!.readFile(FILE_NAME) ?: return ArrayList<BusStop>()

        val jsonArray = JSONArray(data)

        val arrayList = ArrayList<BusStop>()
        for (i in 0..(jsonArray .length() - 1)) {

            val jsonItem = jsonArray.getJSONObject(i)

            val name = jsonItem.getString(NAME_TAG)
            val latitude = jsonItem.getString(LATITUDE_TAG).toDouble()
            val longitude = jsonItem.getString(LONGITUDE_TAG).toDouble()

            val busStop = BusStop(name, latitude, longitude)

            arrayList.add(busStop)
        }

        return arrayList
    }

}