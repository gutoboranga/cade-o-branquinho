package com.example.augusto.cade_o_branquinho.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.model.BusLocation
import com.example.augusto.cade_o_branquinho.model.BusStop
import com.example.augusto.cade_o_branquinho.utils.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.bus_stop_detail.view.*
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, WSListener {


    // map related vars
    private lateinit var map: GoogleMap
    //    private val mapCenter = LatLng(-30.071224, -51.119861)
    private val mapCenter = LatLng(-29.75895163, -50.01396854)
    private val mapInitialZoom = 15.0f

    // bus stops vars
    private lateinit var busStops: ArrayList<BusStop>
    private lateinit var departuresUpdater: Runnable
    private var lastDeparture: DepartureTime? = null
    private var nextDeparture: DepartureTime? = null
    private lateinit var lastDepartureLabel: TextView

    // bus location vars
    var currentBusLocation: BusLocation? = null
    private var currentLocationMarker: Marker? = null

    // web socket vars
    private var webSocket: WebSocket? = null
    private var shouldRetryConnection = false
    private var shouldShowConnectionError = true

    // other vars
    private lateinit var timeMeasurement: TimeMeasurent
    private val timeManager = TimeManager()
    private lateinit var jsonUtils: JsonUtils


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_fragment_layout, container, false)

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps_fragment_support_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        jsonUtils = JsonUtils(context!!)
        timeMeasurement = TimeMeasurent(jsonUtils)
        busStops = BusStop.all()

        // configura timer para atualizar os tempos nas paradas a cada x segundos
        configureDeparturesUpdater()
        departuresUpdater.run()

        // cria um socket para receber a localização do bus
        webSocket = WebSocketUtils().openSocket(this)

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


    // --- Departure methods


    private fun configureDeparturesUpdater() {
        val listener = this
        departuresUpdater = object : Runnable {
            override fun run() {
                updateDepartures()
                Handler().postDelayed(this, 5000)
                if (shouldRetryConnection) {
                    webSocket = WebSocketUtils().openSocket(listener)
                }
            }
        }
    }

    private fun updateDepartures() {

        val last = timeManager.getLastDeparture()
        val next = timeManager.getNextDepartureWeek()

        if (last != null && next != null) {
            updateBusStopsNextTime(last, next)
        }

        lastDeparture = last
        nextDeparture = next
    }

    private fun updateBusStopsNextTime(last: DepartureTime, next: DepartureTime) {

        for (b in busStops) {
            val minutesToAdd = timeMeasurement.getNextTime(b.getId())

            val currentTrip = DepartureTime(last.hour, last.minute)
            val nextTrip = DepartureTime(next.hour, next.minute)

            currentTrip.sum(minutesToAdd)
            nextTrip.sum(minutesToAdd)

            // se já passou do horário da viagem atual, mostra o horário da próxima
            if (timeManager.hasPassed(currentTrip)) {
                b.nextTime = DepartureTime(nextTrip.hour, nextTrip.minute)
            } else {
                b.nextTime = DepartureTime(currentTrip.hour, currentTrip.minute)
            }

        }
    }

    private fun showBusStopDetail(b: BusStop) {

        val view = layoutInflater.inflate(R.layout.bus_stop_detail, null)
        view.bus_stop_detail_header_title.text = b.getName()
        view.bus_stop_detail_next_time_value_label.text = b.nextTime!!.getFormatted()

        val dialog = AlertDialog.Builder(this.context)
                .setView(view)
                .create()

        dialog.show()

    }

    
    // --- Web Socket Listener methods


    override fun onOpen(webSocket: WebSocket, response: Response) {
        val mainHandler = Handler(Looper.getMainLooper())

        val runnable = Runnable { Toast.makeText(context, "Conectado ao servidor", Toast.LENGTH_SHORT).show() }

        mainHandler.post(runnable)
        shouldRetryConnection = false
        shouldShowConnectionError = true
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {

        if (text != null) {
            // atualiza a posição do bus
            currentBusLocation = jsonUtils.getBusLocation(text)

            // manda animar o pin na thread principal (senão dá pau)
            val mainHandler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                showMarker(currentBusLocation!!)
            }
            mainHandler.post(runnable)
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {}

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        if (shouldShowConnectionError) {
            shouldShowConnectionError = false
            showWarning("Fim da conexão", "A conexão ao servidor foi encerrada.", false)
        }
        this.webSocket = null
        shouldRetryConnection = true
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        if (shouldShowConnectionError) {
            shouldShowConnectionError = false
            showWarning("Erro de conexão", "Falha ao conectar ao servidor. Tente novamente em seguida.", false)
        }
        this.webSocket = null
        shouldRetryConnection = true
    }

    private fun showWarning(title: String, message: String, isSending: Boolean) {
        val mainHandler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                val builder = android.support.v7.app.AlertDialog.Builder(context!!)
                val dialog = builder.
                        setTitle(title).
                        setMessage(message).
                        setNeutralButton("Ok", null).create()

                dialog.show()

            }
        }

        mainHandler.post(runnable)
    }


    // --- Bus Marker methods


    // move o pin do bus para a nova posição
    private fun showMarker(location: BusLocation) {
        val latLng = LatLng(location.getLatitude(), location.getLongitude())

        currentLocationMarker?.remove()
        currentLocationMarker = map.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus1))
                .position(latLng)
                .rotation(location.getBearing()))

//        if (currentLocationMarker == null)
//            currentLocationMarker = map.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng))
//        else
//            MarkerAnimation.animateMarkerToGB(currentLocationMarker!!, latLng, LatLngInterpolator.Spherical());
    }

}
