package com.example.augusto.cade_o_branquinho.utils

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

    lateinit var manager: WSListener

    override fun onOpen(webSocket: WebSocket, response: Response) { manager.onOpen(webSocket, response) }
    override fun onMessage(webSocket: WebSocket?, text: String?) { manager.onMessage(webSocket, text) }
    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) { manager.onMessage(webSocket, bytes) }
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { manager.onFailure(webSocket, t, response) }
    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        manager.onClosing(webSocket, code, reason)
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

}