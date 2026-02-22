package com.yolo.vozilo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpController {
    private var udpSocket: DatagramSocket? = null
    private var robotAddress: InetAddress? = null
    private val port = 1606

    init {
        try {
            udpSocket = DatagramSocket()
            robotAddress = InetAddress.getByName("192.168.4.1")
        } catch (e: Exception) {
            Log.e("UdpController", "Failed to initialize UDP socket", e)
        }
    }

    suspend fun sendCommand(cmd: String) = withContext(Dispatchers.IO) {
        if (robotAddress == null || udpSocket == null) return@withContext
        try {
            val data = cmd.toByteArray()
            val packet = DatagramPacket(data, data.size, robotAddress, port)
            udpSocket?.send(packet)
        } catch (e: Exception) {
            Log.e("UdpController", "Send failed: ${e.message}")
        }
    }

    fun close() {
        udpSocket?.close()
    }
}