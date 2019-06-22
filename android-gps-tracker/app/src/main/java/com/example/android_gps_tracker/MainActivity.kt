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
import android.widget.Button
import android.widget.TextView
import javax.xml.datatype.DatatypeConstants.SECONDS
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class MainActivity : AppCompatActivity(), LocationListener  {

    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null

    private lateinit var sendButton: Button
    private var isSending = false
    private lateinit var webSocket: WebSocket

    private val SERVER_URL = "ws://192.168.0.106:3000/websocket-provider"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendButton = findViewById<Button>(R.id.send_button)
        setIsSending(false)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 42
            )
        } else {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this)
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
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

    private fun createConnection() {

        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(SERVER_URL)
            .build()


        val listener = MySocketListener()
        webSocket = client.newWebSocket(request, listener)

    }

    private fun makeMessage(lat: Double, long: Double): String {
        return "{ \"latitude\" : ${lat}, \"longitude\" : ${long}}"
    }

    private fun setIsSending(s: Boolean) {
        isSending = s

        if (isSending) {
            sendButton.text = "Desconectar"
            sendButton.setOnClickListener {
                setIsSending(!isSending)

                if (::webSocket.isInitialized) {
                    webSocket.close(1000, null)
                }
            }
        } else {
            sendButton.text = "Conectar"
            sendButton.setOnClickListener {
                setIsSending(!isSending)
                createConnection()
            }
        }
    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            findViewById<TextView>(R.id.latitude_value_gps).text = p0.latitude.toString()
            findViewById<TextView>(R.id.longitude_value_gps).text = p0.longitude.toString()
            lastLocation = p0

            if (::webSocket.isInitialized) {
                webSocket.send(makeMessage(p0.latitude, p0.longitude))
            }
        }
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
