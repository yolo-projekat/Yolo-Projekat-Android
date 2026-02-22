package com.yolo.vozilo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.webrtc.*
import java.io.ByteArrayOutputStream
import kotlin.math.min

class WebRtcController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onConnectionChange: (Boolean) -> Unit,
    private val onFrameReceived: (Bitmap) -> Unit
) {
    private val httpClient = OkHttpClient()
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var lastFrameProcessTime = 0L

    fun start() {
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions())
        val options = PeerConnectionFactory.Options()
        peerConnectionFactory = PeerConnectionFactory.builder().setOptions(options).createPeerConnectionFactory()

        val iceServers = listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())

        peerConnection = peerConnectionFactory?.createPeerConnection(iceServers, object : CustomPeerConnectionObserver() {
            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                onConnectionChange(state == PeerConnection.IceConnectionState.CONNECTED)
            }
            override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {
                val track = receiver?.track() as? VideoTrack ?: return
                track.addSink { frame -> processNativeFrame(frame) }
            }
        })

        peerConnection?.addTransceiver(MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO, RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY))

        peerConnection?.createOffer(object : CustomSdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                desc?.let {
                    peerConnection?.setLocalDescription(CustomSdpObserver(), it)
                    postOfferToServer(it)
                }
            }
        }, MediaConstraints())
    }

    private fun postOfferToServer(sdp: SessionDescription) {
        scope.launch(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("sdp", sdp.description)
                    put("type", sdp.type.canonicalForm())
                }.toString()

                val body = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url("http://192.168.4.1:1607/offer").post(body).build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val respJson = JSONObject(response.body.string())
                        val remoteSdp = SessionDescription(SessionDescription.Type.fromCanonicalForm(respJson.getString("type")), respJson.getString("sdp"))
                        peerConnection?.setRemoteDescription(CustomSdpObserver(), remoteSdp)
                    }
                }
            } catch (e: Exception) { Log.e("WebRTC", "Signaling failed", e) }
        }
    }

    private fun processNativeFrame(frame: VideoFrame) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFrameProcessTime < 66) return
        lastFrameProcessTime = currentTime

        frame.retain()
        val buffer = frame.buffer
        val i420Buffer = buffer.toI420()

        if (i420Buffer == null) {
            frame.release()
            return
        }

        val width = i420Buffer.width
        val height = i420Buffer.height
        val yuvBytes = ByteArray(width * height * 3 / 2)

        val yBuffer = i420Buffer.dataY
        val uBuffer = i420Buffer.dataU
        val vBuffer = i420Buffer.dataV

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        yBuffer.get(yuvBytes, 0, ySize)

        var uvIndex = ySize
        for (i in 0 until min(uSize, vSize)) {
            yuvBytes[uvIndex++] = vBuffer.get(i)
            yuvBytes[uvIndex++] = uBuffer.get(i)
        }

        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 80, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        i420Buffer.release()
        frame.release()

        onFrameReceived(bitmap)
    }

    fun stop() {
        peerConnection?.close()
        peerConnection = null
        onConnectionChange(false)
    }
}