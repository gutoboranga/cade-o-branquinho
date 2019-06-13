package com.example.augusto.cade_o_branquinho.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.model.BusStop
import com.example.augusto.cade_o_branquinho.utils.BusStopLocationsUtils
import com.example.augusto.cade_o_branquinho.utils.DepartureTime
import com.example.augusto.cade_o_branquinho.utils.TimeManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.map_fragment_layout.view.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var busStops: ArrayList<BusStop>
    private lateinit var lastDepartureUpdater: Runnable
    private var lastDeparture: DepartureTime? = null
    private lateinit var lastDepartureLabel: TextView

    private val timeManager = TimeManager()
    private val mapCenter = LatLng(-30.071224, -51.119861)
    private val mapInitialZoom = 15.0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_fragment_layout, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps_fragment_support_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val busStopLocationsUtils = BusStopLocationsUtils(this.context!!)
        this.busStops = busStopLocationsUtils.getLocations()

        configureLastDepartureUpdater()
        lastDeparture = timeManager.getLastDeparture()
        lastDepartureLabel = view.maps_fragment_last_departure_textview
        lastDepartureUpdater.run()
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

    private fun configureLastDepartureUpdater() {
        lastDepartureUpdater = object: Runnable {
            override fun run() {
                updateLastDeparture()
                Handler().postDelayed(this, 30000)
            }
        }
    }

    private fun updateLastDeparture() {
        val newDeparture = timeManager.getLastDeparture()

        if (newDeparture!= null) {
            if (newDeparture != lastDeparture) {
                println("LAST DEPARTURE: " + newDeparture!!.getFormatted())
                lastDepartureLabel.text = newDeparture!!.getFormatted()

                updateBusStopsNextTime()
            }
        }

        lastDeparture = newDeparture
    }

    private fun updateBusStopsNextTime() {
        var minutesToAdd = 0
        for (b in busStops) {
            b.nextTime = DepartureTime(lastDeparture!!.hour, lastDeparture!!.minute)
            b.nextTime.sum(minutesToAdd)

            minutesToAdd += 2
            println(b.name + " => " + b.nextTime.getFormatted())
        }
    }

}
