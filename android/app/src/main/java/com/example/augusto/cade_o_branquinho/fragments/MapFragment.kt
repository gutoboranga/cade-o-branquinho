package com.example.augusto.cade_o_branquinho.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.model.BusStop
import com.example.augusto.cade_o_branquinho.utils.BusStopLocationsUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var busStops: ArrayList<BusStop>

    private val mapCenter = LatLng(-30.071224, -51.119861)
    private val mapInitialZoom = 15.0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_fragment_layout, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps_fragment_support_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val busStopLocationsUtils = BusStopLocationsUtils(this.context!!)
        this.busStops = busStopLocationsUtils.getLocations()

        return view
    }

    // --- Map methods

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!

        for (item in busStops) {
            val marker= LatLng(item.latitude, item.longitude)
            map.addMarker(MarkerOptions().position(marker).title(item.name))
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, mapInitialZoom))
    }

}
