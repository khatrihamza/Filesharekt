package com.example.filesharekt.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.filesharekt.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deviceNameText.text = "Device Name: ${Build.DEVICE}"
        binding.deviceAddressText.text = "Address: ${getMacAddress()}"
        binding.wifiP2pStatusText.text = "Wi-Fi P2P: Available"

        binding.autoAcceptSwitch.setOnCheckedChangeListener { _, isChecked ->
            // TODO: Save preference
        }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // TODO: Save preference
        }
    }

    private fun getMacAddress(): String {
        return try {
            val addr = java.net.NetworkInterface.getNetworkInterfaces()
                .toList()
                .filter { it.hardwareAddress != null }
                .firstOrNull()
                ?.hardwareAddress
                ?.joinToString(":") { "%02x".format(it) }
                ?: "Unknown"
            addr
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
