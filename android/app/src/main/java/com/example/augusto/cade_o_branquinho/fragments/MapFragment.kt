package com.example.augusto.cade_o_branquinho.fragments

import android.app.AlertDialog
import android.graphics.Color
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
import com.example.augusto.cade_o_branquinho.model.DepartureTime
import com.example.augusto.cade_o_branquinho.utils.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.bus_stop_detail.view.*
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.util.*
import com.google.android.gms.maps.model.LatLng
import android.os.SystemClock
import android.view.animation.BounceInterpolator
import com.google.android.gms.maps.Projection



class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, WSListener {


    // map related vars
    private lateinit var map: GoogleMap
    private val mapCenter = LatLng(-30.071224, -51.119861)
//    private val mapCenter = LatLng(-29.75895163, -50.01396854)
    private val mapInitialZoom = 15.3f

    // bus stops vars
    private lateinit var busStops: ArrayList<BusStop>
    private lateinit var departuresUpdater: Runnable
    private var lastDeparture: DepartureTime? = null
    private var nextDeparture: DepartureTime? = null
    private lateinit var lastDepartureLabel: TextView
    private var busStopsMarkers: ArrayList<Marker> = arrayListOf()

    // bus location vars
    var currentBusLocation: BusLocation? = null
    private var currentLocationMarker: Marker? = null
    private val proximityThreshold: Float = 20f

    // web socket vars
    private var webSocket: WebSocket? = null
    private lateinit var serverConnectionUpdater: Runnable
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

        // inicializa a lista de paradas
        busStops = BusStop.all()
        for (b in busStops) {
            b.minutesToAdd = timeMeasurement.getNextTime(b.getId())
        }

        // configura timer para atualizar os tempos nas paradas a cada x segundos
        configureDeparturesUpdater()
        departuresUpdater.run()

        // cria um socket para receber a localização
        webSocket = WebSocketUtils().openSocket(this)

        return view
    }


    // --- Map methods


    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!
        map.uiSettings.isRotateGesturesEnabled = false

        // get the locations of the bus' route
        val busRoutePoints = GPXUtils().getPoints(activity!!)
        drawBusRoute(busRoutePoints)

        for (item in busStops) {
            val pos = item.getLocation()
            busStopsMarkers.add(
                    map.addMarker(MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus1))
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(item.getMarkerIcon()))
                            .snippet(item.getId()))
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
        departuresUpdater = object : Runnable {
            override fun run() {
                updateDepartures()
                Handler().postDelayed(this, 5000)
            }
        }
    }

    private fun updateDepartures() {

        val last = timeManager.getLastDeparture()
        val next = timeManager.getNextDeparture(timeManager.getTodayType())

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
        val text = b.nextTime.getFormatted()

        view.bus_stop_detail_header_title.text = b.getName()
        view.bus_stop_detail_next_time_value_label.text = text

        if (b == BusStop.TERMINAL) {
            view.bus_stop_detail_header.setBackgroundColor(resources.getColor(R.color.colorAccent))
            view.bus_stop_detail_header_title.setTextColor(resources.getColor(R.color.darkTextColor))
        }

        val dialog = AlertDialog.Builder(this.context)
                .setView(view)
                .create()

        dialog.show()

    }

    
    // --- Web Socket Listener methods


    override fun onOpen(webSocket: WebSocket, response: Response) {
//        showToast("Conectado ao servidor")
        shouldShowConnectionError = false
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {

        if (text != null) {
            // atualiza a posição do bus
            currentBusLocation = jsonUtils.getBusLocation(text)

            // manda animar o pin na thread principal (senão dá pau)
            val mainHandler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                showMarker(currentBusLocation!!)
                checkProximity(currentBusLocation!!)
            }
            mainHandler.post(runnable)
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {}

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        val message = "Falha ao conectar ao servidor"
        if (shouldShowConnectionError) {
            shouldShowConnectionError = false
            showWarning("Fim da conexão", message, false)
        }
        else {
//            showToast(message)
        }

        this.webSocket = WebSocketUtils().openSocket(this)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        val shortMessage = "Falha ao conectar ao servidor"

        if (shouldShowConnectionError) {
            shouldShowConnectionError = false
            showWarning("Erro de conexão", "${shortMessage}. Tente novamente em seguida.", false)
        } else {
//            showToast(shortMessage)
        }

        this.webSocket = WebSocketUtils().openSocket(this)
    }

    private fun showToast(message: String) {
        val mainHandler = Handler(Looper.getMainLooper())

        val runnable = Runnable { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }

        mainHandler.post(runnable)
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

//                Toast.makeText(context!!, )

                dialog.show()

            }
        }

        mainHandler.post(runnable)
    }


    // --- Bus Marker methods


    // move o pin do bus para a nova posição
    private fun showMarker(location: BusLocation) {
        val latLng = LatLng(location.getLatitude(), location.getLongitude())
        val rotation = location.getBearing()

        if (currentLocationMarker == null)
            currentLocationMarker = map.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus1))
                    .position(latLng))
//                    .rotation(rotation))
        else
            currentLocationMarker!!.position = latLng
//            currentLocationMarker!!.rotation = rotation

//        currentLocationMarker!!.setIcon(BitmapDescriptorFactory.fromResource(BusMarkerUtils.getDrawableId(rotation)))
        currentLocationMarker!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.busao))
    }

    private fun drawBusRoute(points: ArrayList<LatLng>) {

        val lineOptions = PolylineOptions()

        lineOptions.addAll(points)
        lineOptions.width(12f)
        lineOptions.color(Color.parseColor("#FF7583"))

        map.addPolyline(lineOptions)
    }

    private fun checkProximity(current: BusLocation) {

        // localização do bus
        val bus = current.getLocation()
//        var amountToCorrect: Int = 0

        // percorre a lista de paradas
        for (i in 0..(busStops.size - 1)) {
            val b = busStops[i]

            if (busIsCloseEnough(b.getLocation(), bus)) {
                if (!b.busIsHere) {
                    //                amountToCorrect = calculateAmountToCorrect(b)
//                busArrivedToBusStop(b, amountToCorrect)
                    println("\t> Tá na parada ${b.getName()}")
                    bounce(i)
                }
                b.busIsHere = true
                break

            } else {
                b.busIsHere = false
            }
        }

    }

    fun busIsCloseEnough(busStop: LatLng, bus: Location): Boolean {
        // calcula distância do bus pra parada
        val results = FloatArray(1)
        Location.distanceBetween(busStop.latitude, busStop.longitude, bus.latitude, bus.longitude, results)

        return results[0] < proximityThreshold
    }

    private fun busArrivedToBusStop(busStop: BusStop, m: Int) {

//        busStop.busIsHere = true
        println("\t> Tá na parada ${busStop.getName()}")

        // update this bus
        val nextTime = timeManager.getNextTimeBusStop(busStop.minutesToAdd, timeManager.getTodayType())
        busStop.nextTime = nextTime

//        var shouldCorrect = false
//        for (b in busStops) {
//            if (shouldCorrect) {
//                println("\t> Ajustei ${b.getName()}, m=${m}")
//                b.nextTime.sum(m)
//            } else if (b.getId() == busStop.getId()) {
//                shouldCorrect = true
//            }
//        }

    }

    // dá a diferença do horário esperado para o horário em que o ônibus passou de fato.
    // esse valor será usado para corrigir os horários previstos nas próximas paradas.
    fun calculateAmountToCorrect(b: BusStop): Int {

        val m = timeManager.getDiffInMinutes(TimeManager.nowDate(), b.nextTime.getDate())
        println("> ${m} minutes")
        return m
    }

    fun bounce(index: Int) {
        if (index >= busStopsMarkers.size) {
            return
        }

        val marker = busStopsMarkers[index]
        val handler = Handler()

        val startTime = SystemClock.uptimeMillis()
        val duration: Long = 1000

        val proj = map.getProjection()
        val markerLatLng = marker.getPosition()
        val startPoint = proj.toScreenLocation(markerLatLng)
        startPoint.offset(0, -50)
        val startLatLng = proj.fromScreenLocation(startPoint)

        val interpolator = BounceInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - startTime
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude
                val lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude
                marker.setPosition(LatLng(lat, lng))

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

}
