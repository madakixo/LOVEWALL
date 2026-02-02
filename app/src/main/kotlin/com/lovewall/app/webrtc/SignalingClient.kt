package com.lovewall.app.webrtc

import org.json.JSONObject
import org.webrtc.SessionDescription
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Inject

class SignalingClient @Inject constructor() {
    private val socket: Socket = IO.socket("https://your-signaling-server-url.com")  // Deploy on Cloud Run

    init {
        socket.connect()
        socket.on("answer") { args ->
            val json = args[0] as JSONObject
            val sdp = SessionDescription(SessionDescription.Type.ANSWER, json.getString("sdp"))
            // Handle in ViewModel
        }
    }

    fun sendOffer(sdp: SessionDescription?) {
        socket.emit("offer", JSONObject().apply {
            put("type", "offer")
            put("sdp", sdp?.description)
        })
    }
}
