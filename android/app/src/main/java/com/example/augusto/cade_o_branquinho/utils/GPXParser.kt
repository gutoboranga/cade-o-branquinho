package com.example.augusto.cade_o_branquinho.utils

import android.support.v4.view.ViewCompat.setElevation
import com.google.android.gms.maps.model.LatLng
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class GPXParserw {

    val TAG_POINT = "trkpt"
    val TAG_LAT = "lat"
    val TAG_LON = "lon"

    fun readPoint(parser: XmlPullParser): ArrayList<LatLng> {

        var array = arrayListOf<LatLng>()
        parser.require(XmlPullParser.START_TAG, "", TAG_POINT)

        while (parser.next() != XmlPullParser.END_TAG) {
            val lat = parser.getAttributeValue(null, TAG_LAT).toDouble()
            val lng = parser.getAttributeValue(null, TAG_LON).toDouble()

            array.add(LatLng(lat, lng))
        }

        parser.require(XmlPullParser.END_TAG, "", TAG_POINT)

        return array
    }


}