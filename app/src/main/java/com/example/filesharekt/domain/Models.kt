package com.example.filesharekt.domain

data class PeerInfo(
    val deviceName: String,
    val deviceAddress: String,
    val isConnected: Boolean = false,
    val status: String = "discovered"
)

data class TransferInfo(
    val id: Long = 0,
    val fileName: String,
    val fileSize: Long,
    val direction: String,
    val remotePeer: String,
    val status: String,
    val progress: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val errorMessage: String? = null,
    val speed: String = "0 KB/s",
    val eta: String = "calculating..."
)

data class NetworkInfo(
    val isP2pEnabled: Boolean,
    val isConnected: Boolean,
    val connectedDevice: String? = null,
    val localAddress: String? = null,
    val groupOwnerAddress: String? = null,
    val isGroupOwner: Boolean = false
)

data class ConnectionState(
    val isPeering: Boolean = false,
    val isGroupFormed: Boolean = false,
    val isGroupOwner: Boolean = false,
    val localIp: String? = null,
    val groupOwnerIp: String? = null
)
