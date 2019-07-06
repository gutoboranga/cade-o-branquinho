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

        fetchWarnings(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.e("BRINKS", "Deu ruim na chamada")}
            override fun onResponse(call: Call, response: Response) {
                Log.d("BRINKS", response.body()?.string())

                // depois de parsear as coisas, chama:
                // updateAdapter(list, "on")
                // mas com os dados reais
            }
        })

        // apenas pra teste ======================================================================

        val list = arrayListOf<Warning>()

        val w1 = Warning("Bus explodiu", "23 DE MAIO DE 2019")
        val w2 = Warning("Tá tudo suave", "10 DE JULHO DE 2018")

        list.add(w1)
        list.add(w2)

        updateAdapter(list, "on")

        // fim do teste ======================================================================


        val view = inflater.inflate(R.layout.warnings_fragment_layout, container, false)

        view.warnings_fragment_recycler.adapter = adapter
        view.warnings_fragment_recycler.layoutManager = LinearLayoutManager(context)

        return view
    }

    fun updateAdapter(list: ArrayList<Warning>, status: String) {
        adapter.update(list, status)
    }



}
