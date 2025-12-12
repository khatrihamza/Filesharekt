package com.example.filesharekt.ui.viewmodel

import android.app.Application
import android.net.wifi.p2p.WifiP2pDevice
import androidx.lifecycle.*
import com.example.filesharekt.data.AppDatabase
import com.example.filesharekt.data.TransferRepository
import com.example.filesharekt.domain.NetworkInfo
import com.example.filesharekt.domain.PeerInfo
import com.example.filesharekt.domain.TransferInfo
import kotlinx.coroutines.launch

class FileShareViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransferRepository
    private val db = AppDatabase.getInstance(application)

    private val _peers = MutableLiveData<List<PeerInfo>>()
    val peers: LiveData<List<PeerInfo>> = _peers

    private val _networkInfo = MutableLiveData<NetworkInfo>()
    val networkInfo: LiveData<NetworkInfo> = _networkInfo

    private val _transfers = MutableLiveData<List<TransferInfo>>()
    val transfers: LiveData<List<TransferInfo>> = _transfers

    private val _activeTransfers = MutableLiveData<List<TransferInfo>>()
    val activeTransfers: LiveData<List<TransferInfo>> = _activeTransfers

    private val _selectedPeer = MutableLiveData<PeerInfo?>()
    val selectedPeer: LiveData<PeerInfo?> = _selectedPeer

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    init {
        repository = TransferRepository(db.transferDao())
        observeTransfers()
    }

    fun setPeers(deviceList: List<WifiP2pDevice>) {
        _peers.value = deviceList.map { PeerInfo(it.deviceName, it.deviceAddress) }
    }

    fun selectPeer(peer: PeerInfo) {
        _selectedPeer.value = peer
    }

    fun updateNetworkInfo(networkInfo: NetworkInfo) {
        _networkInfo.value = networkInfo
    }

    fun createTransfer(
        fileName: String,
        fileSize: Long,
        direction: String,
        remotePeer: String
    ) {
        viewModelScope.launch {
            repository.createTransfer(fileName, fileSize, direction, remotePeer)
        }
    }

    fun updateTransferProgress(transferId: Long, progress: Int) {
        viewModelScope.launch {
            repository.updateProgress(transferId, progress)
        }
    }

    fun markTransferCompleted(transferId: Long) {
        viewModelScope.launch {
            repository.markAsCompleted(transferId)
        }
    }

    fun markTransferFailed(transferId: Long, error: String) {
        viewModelScope.launch {
            repository.markAsFailed(transferId, error)
        }
    }

    fun deleteTransfer(transferId: Long) {
        viewModelScope.launch {
            repository.deleteTransfer(transferId)
        }
    }

    fun clearAllTransfers() {
        viewModelScope.launch {
            repository.clearAllTransfers()
        }
    }

    private fun observeTransfers() {
        viewModelScope.launch {
            repository.getAllTransfers().collect { transfers ->
                _transfers.postValue(transfers)
            }
        }

        viewModelScope.launch {
            repository.getActiveTransfers().collect { active ->
                _activeTransfers.postValue(active)
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        _loadingState.value = isLoading
    }
}
