package com.example.augusto.cade_o_branquinho.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.adapters.WarningsAdapter
import com.example.augusto.cade_o_branquinho.model.Warning
import kotlinx.android.synthetic.main.warnings_fragment_layout.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit




class WarningsFragment : Fragment() {

    val SERVER_URL = "http://cade-o-branquinho-server.herokuapp.com/status"
    val clientBuilder = OkHttpClient.Builder()
    private lateinit var adapter: WarningsAdapter
    private lateinit var swipeContainer: SwipeRefreshLayout


    private fun fetchWarnings(callback : Callback) {
        val client = clientBuilder
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

        val request = Request.Builder()
                .url(SERVER_URL)
                .build()

        client.newCall(request).enqueue(callback)

    }

    private fun updateAdapter(list: ArrayList<Warning>, status: String) {
        adapter.update(list, status)
    }

    fun refreshAdapter() {
        val real_data = ArrayList<Warning>()
        fetchWarnings(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.e("BRINKS", "Deu ruim na chamada")}
            override fun onResponse(call: Call, response: Response) {
                swipeContainer.isRefreshing = false

                val responseJSON = JSONObject(response.body()?.string())
                Log.d("BRINKS", responseJSON.toString())

                val real_statuses = responseJSON.getJSONArray("past_statuses")
                for (i in 0 until real_statuses.length()) {
                    val warningJSON = real_statuses.getJSONObject(i)
                    real_data.add(Warning(warningJSON.getString("text"), warningJSON.getString("date")))
                }

                // cria runnable pra poder atualizar a UI na thread principal
                val mainHandler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    updateAdapter(real_data, responseJSON.getString("current_status"))
                }
                mainHandler.post(runnable)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inicializa o adapter com tudo vazio
        adapter = WarningsAdapter(activity!!, arrayListOf(), "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        refreshAdapter()

        val view = inflater.inflate(R.layout.warnings_fragment_layout, container, false)

        view.warnings_fragment_recycler.adapter = adapter
        view.warnings_fragment_recycler.layoutManager = LinearLayoutManager(context)

        swipeContainer =  view.swipe_container as SwipeRefreshLayout
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                                                android.R.color.holo_green_light)

        swipeContainer.setOnRefreshListener {
            refreshAdapter()
        }

        return view
    }

}
