package com.esgi.yummy.tesla_app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import com.esgi.yummy.tesla_app.models.repositories.BluetoothEmbeddedStateError
import com.esgi.yummy.tesla_app.models.repositories.BluetoothEmbeddedStateLoading
import com.esgi.yummy.tesla_app.models.repositories.BluetoothEmbeddedStateSuccess

class MainActivity : AppCompatActivity() {

    private val deviceName = "HC-06"
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        bluetoothConnection()

    }

    private fun checkPermissionBluetoothDevice(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission", "ClickableViewAccessibility")
    private fun bluetoothConnection() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        this.bluetoothAdapter = bluetoothManager.getAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if (!checkPermissionBluetoothDevice(context = this)) {
                return
            }
        }
        val deviceEmbedded = pairedDevices?.find { device -> device.name == deviceName }
        if (deviceEmbedded != null) {
            val vm: MainViewModel by viewModels { MainViewModel.Factory(deviceEmbedded) }
            viewModel = vm
            viewModel.bluetooth.observe(this) {
                when (it) {
                    is BluetoothEmbeddedStateError -> {
                        it.ex.message?.let { it1 -> Log.e("error", it1) }
                    }
                    BluetoothEmbeddedStateLoading -> {
                        // TODO:
                    }
                    is BluetoothEmbeddedStateSuccess -> {

                    }
                }
            }
        }
    }


}