package com.example.filesharekt.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.net.InetAddress
import java.net.NetworkInterface

object NetworkMonitor {
    private val TAG = "NetworkMonitor"

    data class NetworkStats(
        val isConnected: Boolean,
        val ipAddress: String?,
        val ssid: String?,
        val signalStrength: Int,
        val linkSpeed: Int
    )

    fun getNetworkStats(context: Context): NetworkStats {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo

        return NetworkStats(
            isConnected = connectionInfo != null && connectionInfo.ipAddress != 0,
            ipAddress = getIpAddress(),
            ssid = connectionInfo?.ssid?.removeSurrounding("\""),
            signalStrength = connectionInfo?.rssi ?: 0,
            linkSpeed = connectionInfo?.linkSpeed ?: 0
        )
    }

    fun getIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .filter { it.isUp && !it.isLoopback }
                .flatMap { it.inetAddresses.toList() }
                .filterIsInstance<InetAddress>()
                .filter { !it.isLoopbackAddress && it.hostAddress.contains(".") }
                .map { it.hostAddress }
                .firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting IP address", e)
            null
        }
    }

    fun getMacAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .filter { it.hardwareAddress != null }
                .firstOrNull()
                ?.hardwareAddress
                ?.joinToString(":") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting MAC address", e)
            null
        }
    }
}
