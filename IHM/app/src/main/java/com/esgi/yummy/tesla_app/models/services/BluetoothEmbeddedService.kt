package com.esgi.yummy.tesla_app.models.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.esgi.yummy.tesla_app.models.entities.BluetoothResponse
import com.esgi.yummy.tesla_app.models.extensions.toBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.*


const val MESSAGE_READ: Int = 0

@SuppressLint("MissingPermission")
class BluetoothEmbeddedService(
    private val device: BluetoothDevice
) {
    private val socket: BluetoothSocket

    init {
        val list = device.uuids
        val uuid = list[0].uuid
        socket = device.createRfcommSocketToServiceRecord(uuid)
    }

    fun connect() {
        socket.connect()
    }

    suspend fun read(): BluetoothResponse {
        if(socket.isConnected) {
            val numBytes: Int
            val inStream: InputStream = socket.inputStream
            val buffer = ByteArray(10)
            numBytes = withContext(Dispatchers.IO) {
                inStream.read(buffer, 0, 10)
            }
            val responseStr = String(buffer, 0, numBytes)
            val responseArr = responseStr.split(';')
            val isLedBlinkingLeft = if (responseArr.isNotEmpty()) responseArr[0].toInt().toBoolean() else false
            val isLedBlinkingRight = if (responseArr.count() >= 2) responseArr[1].toInt().toBoolean() else false
            val distance = if (responseArr.count() >= 3) responseArr[2].toInt() else 0
            val isMotorRunning = if (responseArr.count() >= 4) responseArr[3].toInt().toBoolean() else false
            return BluetoothResponse(isLedBlinkingLeft, isLedBlinkingRight, distance, isMotorRunning)
        } else {
            throw Exception("Bluetooth non connecté")
        }
    }

}
