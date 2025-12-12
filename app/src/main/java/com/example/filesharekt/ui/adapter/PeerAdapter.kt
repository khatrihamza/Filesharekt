package com.example.filesharekt.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filesharekt.databinding.ItemPeerBinding
import com.example.filesharekt.domain.PeerInfo

class PeerAdapter(
    private val onConnect: (PeerInfo) -> Unit
) : RecyclerView.Adapter<PeerAdapter.ViewHolder>() {

    private var peers = listOf<PeerInfo>()

    fun submitList(list: List<PeerInfo>) {
        peers = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(peers[position])
    }

    override fun getItemCount() = peers.size

    inner class ViewHolder(private val binding: ItemPeerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(peer: PeerInfo) {
            binding.apply {
                peerName.text = peer.deviceName
                peerAddress.text = peer.deviceAddress
                connectBtn.text = if (peer.isConnected) "Connected" else "Connect"
                connectBtn.isEnabled = !peer.isConnected
                connectBtn.setOnClickListener {
                    onConnect(peer)
                }
            }
        }
    }
}
