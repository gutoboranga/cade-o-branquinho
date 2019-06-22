package com.example.android_gps_tracker

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

interface WSListener {
    fun onOpen(webSocket: WebSocket, response: Response)
    fun onMessage(webSocket: WebSocket?, text: String?)
    fun onMessage(webSocket: WebSocket?, bytes: ByteString?)
    fun onClosing(webSocket: WebSocket?, code: Int, reason: String?)
    fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?)
}

class MySocketListener : WebSocketListener() {

    private lateinit var manager: WSListener

    fun setManager(m: WSListener) {
        manager = m
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        manager.onOpen(webSocket, response)
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        manager.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        manager.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        manager.onClosing(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        manager.onFailure(webSocket, t, response)
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private fun output(txt: String) {
        Log.v("WSS", txt)
    }
}