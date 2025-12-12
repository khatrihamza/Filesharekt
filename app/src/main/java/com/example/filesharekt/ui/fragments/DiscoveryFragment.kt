package com.example.filesharekt.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filesharekt.databinding.FragmentDiscoveryBinding
import com.example.filesharekt.ui.adapter.PeerAdapter
import com.example.filesharekt.ui.viewmodel.FileShareViewModel

class DiscoveryFragment : Fragment() {
    private lateinit var binding: FragmentDiscoveryBinding
    private lateinit var viewModel: FileShareViewModel
    private lateinit var peerAdapter: PeerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(FileShareViewModel::class.java)

        peerAdapter = PeerAdapter { peer ->
            viewModel.selectPeer(peer)
        }

        binding.peersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = peerAdapter
        }

        viewModel.peers.observe(viewLifecycleOwner) { peers ->
            peerAdapter.submitList(peers)
            binding.statusText.text = "Found ${peers.size} peer(s)"
        }

        binding.discoverBtn.setOnClickListener {
            binding.statusText.text = "Discovering peers..."
        }

        binding.stopDiscoverBtn.setOnClickListener {
            binding.statusText.text = "Discovery stopped"
        }
    }
}
