package com.example.augusto.cade_o_branquinho.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.model.BusStop
import com.example.augusto.cade_o_branquinho.utils.DepartureTime
import com.example.augusto.cade_o_branquinho.utils.JsonUtils
import com.example.augusto.cade_o_branquinho.utils.TimeManager
import com.example.augusto.cade_o_branquinho.utils.TimeMeasurent

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.bus_stop_detail.view.*

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var busStops: ArrayList<BusStop>
    private lateinit var departuresUpdater: Runnable
    private var lastDeparture: DepartureTime? = null
    private var nextDeparture: DepartureTime? = null
    private lateinit var lastDepartureLabel: TextView

    private lateinit var timeMeasurement: TimeMeasurent
    private val timeManager = TimeManager()
    private val mapCenter = LatLng(-30.071224, -51.119861)
    private val mapInitialZoom = 15.0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_fragment_layout, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps_fragment_support_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        timeMeasurement = TimeMeasurent(JsonUtils(context!!))
        busStops = BusStop.all()

        configureDeparturesUpdater()
        departuresUpdater.run()
        return view
    }

    // --- Map methods

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!

        for (item in busStops) {
            val pos = item.getLocation()
            map.addMarker(MarkerOptions()
                    .position(pos)
                    .title(item.name)
                    .snippet(item.getId().toString())
            )
        }
        map.setOnMarkerClickListener(this)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, mapInitialZoom))
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val id = p0?.snippet

        if (id != null) {
            for (b in busStops) {
                if (b.getId() == id) {
                    showBusStopDetail(b)
                    break
                }
            }
        }

        return true
    }

    // --- Other stuff

    private fun configureDeparturesUpdater() {
        departuresUpdater = object: Runnable {
            override fun run() {
                updateDepartures()
                Handler().postDelayed(this, 30000)
            }
        }
    }

    private fun updateDepartures() {
        val last = timeManager.getLastDeparture()
        val next = timeManager.getNextDepartureWeek()

        updateBusStopsNextTime(last!!, next!!)

        lastDeparture = last
        nextDeparture = next

        println("Atualizei os trem")
        Toast.makeText(context, "Atualizei os trem", Toast.LENGTH_LONG).show()
    }

    private fun updateBusStopsNextTime(last: DepartureTime, next: DepartureTime) {

        for (b in busStops) {
            val minutesToAdd = timeMeasurement.getNextTime(b.getId())

            val currentTrip = DepartureTime(last.hour, last.minute)
            val nextTrip = DepartureTime(next.hour, next.minute)

            currentTrip .sum(minutesToAdd)
            nextTrip.sum(minutesToAdd)

            // se já passou do horário da viagem atual, mostra o horário da próxima
            if (timeManager.hasPassed(currentTrip )) {
                b.nextTime = DepartureTime(nextTrip.hour, nextTrip.minute)
            } else {
                b.nextTime = DepartureTime(currentTrip .hour, currentTrip .minute)
            }

        }
    }

    private fun showBusStopDetail(b: BusStop) {

        val view= layoutInflater.inflate(R.layout.bus_stop_detail, null)
        view.bus_stop_detail_header_title.text = b.getName()
        view.bus_stop_detail_next_time_value_label.text = b.nextTime!!.getFormatted()

        val dialog = AlertDialog.Builder(this.context)
                .setView(view)
                .create()

        dialog.show()

    }

}
