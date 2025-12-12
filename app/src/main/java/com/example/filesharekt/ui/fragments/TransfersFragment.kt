package com.example.filesharekt.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filesharekt.databinding.FragmentTransfersBinding
import com.example.filesharekt.ui.adapter.TransferAdapter
import com.example.filesharekt.ui.viewmodel.FileShareViewModel

class TransfersFragment : Fragment() {
    private lateinit var binding: FragmentTransfersBinding
    private lateinit var viewModel: FileShareViewModel
    private lateinit var transferAdapter: TransferAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTransfersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(FileShareViewModel::class.java)

        transferAdapter = TransferAdapter { transferId ->
            viewModel.deleteTransfer(transferId)
        }

        binding.transfersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transferAdapter
        }

        binding.activeTransfersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TransferAdapter { transferId ->
                viewModel.deleteTransfer(transferId)
            }
        }

        viewModel.transfers.observe(viewLifecycleOwner) { transfers ->
            transferAdapter.submitList(transfers)
        }

        binding.pickFileBtn.setOnClickListener {
            // TODO: Launch file picker
        }

        binding.clearHistoryBtn.setOnClickListener {
            viewModel.clearAllTransfers()
        }
    }
}
