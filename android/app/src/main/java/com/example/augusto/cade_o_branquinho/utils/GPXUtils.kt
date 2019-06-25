package com.example.augusto.cade_o_branquinho.utils

import android.app.Activity
import com.google.android.gms.maps.model.LatLng
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import java.lang.Exception

class GPXUtils {

    private val FILENAME = "bus_route.gpx"

    fun getPoints(activity: Activity): ArrayList<LatLng> {

        val gpxParser = GPXParser()
        var parsedGpx: Gpx? = null

        // try to parse local gpx file with bus route
        try {
            val input = activity.assets.open(FILENAME)
            parsedGpx = gpxParser.parse(input)
        } catch (e: Exception) { e.printStackTrace() }

        var coordinates = arrayListOf<LatLng>()

        // will get all the points
        if (parsedGpx != null) {
            for (t in parsedGpx.tracks) {
                for (s in t.trackSegments) {
                    for (tp in s.trackPoints) {
                        coordinates.add(LatLng(tp.latitude, tp.longitude))
                    }
                }
            }
        }

        return coordinates

    }

}