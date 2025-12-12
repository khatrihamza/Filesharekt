package com.example.filesharekt.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filesharekt.databinding.FragmentHistoryBinding
import com.example.filesharekt.ui.adapter.TransferAdapter
import com.example.filesharekt.ui.viewmodel.FileShareViewModel

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: FileShareViewModel
    private lateinit var historyAdapter: TransferAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(FileShareViewModel::class.java)

        historyAdapter = TransferAdapter { transferId ->
            viewModel.deleteTransfer(transferId)
        }

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        viewModel.transfers.observe(viewLifecycleOwner) { transfers ->
            historyAdapter.submitList(transfers.sortedByDescending { it.timestamp })
        }
    }
}
