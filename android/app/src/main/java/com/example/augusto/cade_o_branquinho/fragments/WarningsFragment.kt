package com.example.augusto.cade_o_branquinho.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class WarningsFragment : Fragment() {
    val SERVER_URL = "http://192.168.0.4:3000/status"
    val clientBuilder = OkHttpClient.Builder()

    private fun fetchWarnings(callback : Callback) {
        val client = clientBuilder
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

        val request = Request.Builder()
                .url(SERVER_URL)
                .build()

        client.newCall(request).enqueue(callback)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("BRINKS", "WarningsFragment::onCreateView")

        fetchWarnings(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.e("BRINKS", "Deu ruim na chamada")}
            override fun onResponse(call: Call, response: Response) {
                Log.d("BRINKS", response.body()?.string())
            }
        })


        return inflater.inflate(R.layout.warnings_fragment_layout, container, false)
    }

}
