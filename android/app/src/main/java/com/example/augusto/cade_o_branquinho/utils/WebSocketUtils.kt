package com.example.augusto.cade_o_branquinho.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebSocketUtils() {

//    val SERVER_URL = "ws://cade-o-branquinho-server.herokuapp.com/websocket"
    val SERVER_URL = "ws://192.168.25.3:5000/websocket"

    fun openSocket(wslistener: WSListener): WebSocket {
        val client = OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

        val request = Request.Builder()
                .url(SERVER_URL)
                .build()


        val listener = MySocketListener()
        listener.manager = wslistener
        return client.newWebSocket(request, listener)
    }

}