package com.example.filesharekt

import android.Manifest
import android.app.Activity
import android.content.*
import android.net.Uri
import android.net.wifi.p2p.*
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.filesharekt.databinding.ActivityMainBinding
import com.example.filesharekt.domain.NetworkInfo
import com.example.filesharekt.ui.fragments.DiscoveryFragment
import com.example.filesharekt.ui.fragments.HistoryFragment
import com.example.filesharekt.ui.fragments.SettingsFragment
import com.example.filesharekt.ui.fragments.TransfersFragment
import com.example.filesharekt.ui.viewmodel.FileShareViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FileShareViewModel

    private lateinit var manager: WifiP2pManager
    private var channel: Channel? = null
    private var receiver: BroadcastReceiver? = null

    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { sendFile(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FileShareViewModel::class.java)

        requestPermissions()
        initializeWifiP2p()
        setupNavigation()
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        ActivityCompat.requestPermissions(this, permissions, 100)
    }

    private fun initializeWifiP2p() {
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        receiver = WiFiP2pBroadcastReceiver(manager, channel!!, this::onPeersAvailable, this::onConnectionInfo)
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_discovery -> loadFragment(DiscoveryFragment())
                R.id.nav_transfers -> loadFragment(TransfersFragment())
                R.id.nav_history -> loadFragment(HistoryFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
                else -> false
            }
            true
        }

        // Load initial fragment
        loadFragment(DiscoveryFragment())
        binding.bottomNavigation.selectedItemId = R.id.nav_discovery
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }, Context.RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() { Log.i(TAG, "Discovery started") }
            override fun onFailure(reason: Int) { Log.e(TAG, "Discovery failed: $reason") }
        })
    }

    private fun onPeersAvailable(peerList: Collection<WifiP2pDevice>) {
        val peers = peerList.map { WifiP2pManager.WifiP2pDeviceList().apply { deviceList.add(it) } }
        Log.d(TAG, "Peers available: ${peerList.size}")
    }

    fun connectToPeer(deviceAddress: String) {
        val config = WifiP2pConfig().apply { deviceAddress = deviceAddress }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() { Log.i(TAG, "Connecting to $deviceAddress") }
            override fun onFailure(reason: Int) { Log.e(TAG, "Connect failed: $reason") }
        })
    }

    private fun onConnectionInfo(info: WifiP2pInfo) {
        val networkInfo = NetworkInfo(
            isP2pEnabled = true,
            isConnected = info.groupFormed,
            isGroupOwner = info.isGroupOwner,
            groupOwnerAddress = info.groupOwnerAddress?.hostAddress
        )
        viewModel.updateNetworkInfo(networkInfo)

        if (info.groupFormed) {
            if (info.isGroupOwner) {
                FileTransferService.startServer(this)
            }
        }
    }

    private fun sendFile(uri: Uri) {
        thread {
            try {
                val fileName = uri.lastPathSegment ?: "file"
                val fileSize = contentResolver.query(uri, null, null, null, null)?.use {
                    it.moveToFirst()
                    it.getLong(0)
                } ?: 0L

                viewModel.createTransfer(fileName, fileSize, "send", "")
            } catch (e: Exception) {
                Log.e(TAG, "Error preparing file", e)
            }
        }
    }
}

