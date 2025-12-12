package com.example.filesharekt.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filesharekt.databinding.ItemTransferBinding
import com.example.filesharekt.domain.TransferInfo
import java.text.DecimalFormat

class TransferAdapter(
    private val onDelete: (Long) -> Unit
) : RecyclerView.Adapter<TransferAdapter.ViewHolder>() {

    private var transfers = listOf<TransferInfo>()

    fun submitList(list: List<TransferInfo>) {
        transfers = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transfers[position])
    }

    override fun getItemCount() = transfers.size

    inner class ViewHolder(private val binding: ItemTransferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transfer: TransferInfo) {
            binding.apply {
                fileName.text = transfer.fileName
                fileSize.text = formatBytes(transfer.fileSize)
                remotePeer.text = "Peer: ${transfer.remotePeer}"
                status.text = transfer.status.uppercase()
                progressBar.progress = transfer.progress
                progressText.text = "${transfer.progress}% - ${transfer.speed}"

                deleteBtn.setOnClickListener {
                    onDelete(transfer.id)
                }

                val statusColor = when (transfer.status) {
                    "completed" -> 0xFF4CAF50.toInt()
                    "in_progress" -> 0xFF2196F3.toInt()
                    "failed" -> 0xFFF44336.toInt()
                    else -> 0xFF757575.toInt()
                }
                status.setTextColor(statusColor)
            }
        }

        private fun formatBytes(bytes: Long): String {
            val df = DecimalFormat("0.00")
            val kilobytes = bytes / 1024.0
            val megabytes = kilobytes / 1024.0
            return when {
                megabytes >= 1 -> "${df.format(megabytes)} MB"
                kilobytes >= 1 -> "${df.format(kilobytes)} KB"
                else -> "$bytes B"
            }
        }
    }
}
