package com.example.android_gps_tracker

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class MySocketListener : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        output("Open!")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        output("Receiving : " + text!!)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        output("Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message + " - " + t.cause + " - " + t.stackTrace)
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private fun output(txt: String) {
        Log.v("WSS", txt)
    }
}