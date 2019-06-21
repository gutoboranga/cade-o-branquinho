package com.example.android_gps_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.TextView


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 42
            )
        } else {
            requestLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            42 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()
                } else {
                    findViewById<TextView>(R.id.title_gps).text = ":'("
                }
                return
            }
        }
    }

    private fun uploadLocation() {

    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            findViewById<TextView>(R.id.latitude_value_gps).text = p0.latitude.toString()
            findViewById<TextView>(R.id.longitude_value_gps).text = p0.longitude.toString()
            lastLocation = p0
        }

        println("onLocationChanged: ${p0}")
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        println("onStatusChanged: ${p0}")
    }

    override fun onProviderEnabled(p0: String?) {
        println("onProviderEnabled: ${p0}")
    }

    override fun onProviderDisabled(p0: String?) {
        println("onProviderDisabled: ${p0}")
    }
}
