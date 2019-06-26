package com.example.augusto.cade_o_branquinho.utils

import com.example.augusto.cade_o_branquinho.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class BusMarkerUtils {

    private var position: LatLng
    private val directions: Array<Double> = arrayOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)

    constructor(pos: LatLng) {
        position = pos
    }

    fun make(): MarkerOptions? {
        return MarkerOptions()
                .position(position)
                .title(name)
    }

    fun updateTexture(marker: Marker) {

//
//        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));
    }

    private fun getTextureIndex(degree: Double) {
        for (d in directions) {

        }
    }


    companion object {
        const val name: String = "Branquinho"

        fun getDrawableId(bearing: Float): Int {
            when (bearing) {
                in 0..89 -> return R.drawable.bus5
                in 90..179 -> return R.drawable.bus7
                in 180..269 -> return R.drawable.bus1
                in 270..359-> return R.drawable.bus3
                else -> return R.drawable.bus1
            }
        }
    }

}