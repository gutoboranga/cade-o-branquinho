package com.example.augusto.cade_o_branquinho.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
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

    val SERVER_URL = "http://192.168.0.4:3000/status"
    val clientBuilder = OkHttpClient.Builder()
    private lateinit var adapter: WarningsAdapter

    private fun fetchWarnings(callback : Callback) {
        val client = clientBuilder
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

        val request = Request.Builder()
                .url(SERVER_URL)
                .build()

        client.newCall(request).enqueue(callback)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inicializa o adapter com tudo vazio
        adapter = WarningsAdapter(activity!!, arrayListOf(), "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("BRINKS", "WarningsFragment::onCreateView")


        val real_data = ArrayList<Warning>()
        fetchWarnings(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.e("BRINKS", "Deu ruim na chamada")}
            override fun onResponse(call: Call, response: Response) {
                val responseJSON = JSONObject(response.body()?.string())
                Log.d("BRINKS", responseJSON.toString())

                val real_statuses = responseJSON.getJSONArray("past_statuses")
                for (i in 0 until real_statuses.length()) {
                    val warningJSON = real_statuses.getJSONObject(i)
                    real_data.add(Warning(warningJSON.getString("text"), warningJSON.getString("date")))
                }

                adapter.update(real_data, responseJSON.getString("current_status"))
            }
        })

        val view = inflater.inflate(R.layout.warnings_fragment_layout, container, false)

        view.warnings_fragment_recycler.adapter = adapter
        view.warnings_fragment_recycler.layoutManager = LinearLayoutManager(context)

        return view
    }

    fun updateAdapter(list: ArrayList<Warning>, status: String) {
        adapter.update(list, status)
    }



}
