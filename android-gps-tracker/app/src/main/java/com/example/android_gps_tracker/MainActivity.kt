package com.example.android_gps_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.TextView
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), LocationListener, WSListener  {

    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null
    private var directionInDegrees = 0.0
    private var directionInDegreesTest = 0.0

    private lateinit var sendButton: Button
    private var isSending = false
    private lateinit var webSocket: WebSocket

//    private val SERVER_URL = "ws://192.168.0.106:3000/websocket-provider"
    private val SERVER_URL = "ws://cade-o-branquinho-server.herokuapp.com/websocket-provider"

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
        listener.setManager(this)
        webSocket = client.newWebSocket(request, listener)

    }

    private fun makeMessage(lat: Double, long: Double, direction: Double, bearing: Float): String {
        return "{ \"latitude\" : $lat, \"longitude\" : $long, \"direction\": $direction, \"test\" : $directionInDegreesTest, \"bearing\" : ${bearing} }"
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

            calculateDirectionInDegrees(p0)

            lastLocation = p0

            if (::webSocket.isInitialized) {
                webSocket.send(makeMessage(p0.latitude, p0.longitude, directionInDegrees, p0.bearing))
            }

//            val view = findViewById<TextView>(R.id.content)
//            view.text = "${view.text}\n${makeMessage(p0.latitude, p0.longitude, directionInDegrees)}"
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

    // === WSListener implementation ===

    override fun onOpen(webSocket: WebSocket, response: Response) {}
    override fun onMessage(webSocket: WebSocket?, text: String?) {}
    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {}
    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        showWarning("Fim da conexão", "A conexão ao servidor foi encerrada.", false)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        showWarning("Erro", "Falha ao conectar ao servidor. Tente novamente em seguida.", false)
    }

    private fun showWarning(title: String, message: String, isSending: Boolean) {
        val mainHandler = Handler(this.mainLooper)
        val activity = this

        val runnable = object : Runnable {
            override fun run() {
                val builder = AlertDialog.Builder(activity)
                val dialog = builder.
                    setTitle(title).
                    setMessage(message).
                    setNeutralButton("Ok", null).create()

                dialog.show()

            }
        }

        mainHandler.post(runnable)
    }


    fun calculateDirectionInDegrees(newLocation: Location) {
        if (lastLocation != null) {
            val dLon = newLocation.longitude - lastLocation!!.longitude
            val y = Math.sin(dLon)
            val x = Math.cos(lastLocation!!.latitude) * Math.sin(newLocation.latitude) - Math.sin(lastLocation!!.latitude) + Math.cos(newLocation.latitude) * Math.cos(dLon)

            directionInDegrees = Math.atan2(y, x) * 180 / Math.PI

            if (directionInDegrees < 0) directionInDegrees += 360

            directionInDegreesTest += 180.0 - directionInDegrees
        }
    }
}
