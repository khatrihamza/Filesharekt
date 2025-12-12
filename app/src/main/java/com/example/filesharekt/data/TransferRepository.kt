package com.example.filesharekt.data

import com.example.filesharekt.domain.TransferInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransferRepository(private val transferDao: TransferDao) {

    fun getAllTransfers(): Flow<List<TransferInfo>> =
        transferDao.getAllTransfers().map { entities ->
            entities.map { it.toTransferInfo() }
        }

    fun getActiveTransfers(): Flow<List<TransferInfo>> =
        transferDao.getTransfersByStatus("in_progress").map { entities ->
            entities.map { it.toTransferInfo() }
        }

    fun getCompletedTransfers(): Flow<List<TransferInfo>> =
        transferDao.getTransfersByStatus("completed").map { entities ->
            entities.map { it.toTransferInfo() }
        }

    suspend fun createTransfer(
        fileName: String,
        fileSize: Long,
        direction: String,
        remotePeer: String
    ): Long {
        val entity = TransferEntity(
            fileName = fileName,
            fileSize = fileSize,
            direction = direction,
            remotePeer = remotePeer,
            status = "pending"
        )
        return transferDao.insertTransfer(entity)
    }

    suspend fun updateProgress(transferId: Long, progress: Int) {
        transferDao.updateProgress(transferId, progress)
    }

    suspend fun markAsInProgress(transferId: Long) {
        transferDao.updateStatus(transferId, "in_progress")
    }

    suspend fun markAsCompleted(transferId: Long) {
        transferDao.updateStatus(transferId, "completed", System.currentTimeMillis())
    }

    suspend fun markAsFailed(transferId: Long, errorMessage: String) {
        val transfer = TransferEntity(
            id = transferId,
            fileName = "",
            fileSize = 0,
            direction = "",
            remotePeer = "",
            status = "failed",
            errorMessage = errorMessage
        )
        transferDao.updateTransfer(transfer)
    }

    suspend fun deleteTransfer(transferId: Long) {
        transferDao.deleteTransfer(transferId)
    }

    suspend fun clearAllTransfers() {
        transferDao.clearAll()
    }

    private fun TransferEntity.toTransferInfo() = TransferInfo(
        id = id,
        fileName = fileName,
        fileSize = fileSize,
        direction = direction,
        remotePeer = remotePeer,
        status = status,
        progress = progress,
        timestamp = timestamp,
        completedAt = completedAt,
        errorMessage = errorMessage
    )
}
