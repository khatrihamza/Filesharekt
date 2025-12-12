package com.example.filesharekt.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Insert
    suspend fun insertTransfer(transfer: TransferEntity): Long

    @Update
    suspend fun updateTransfer(transfer: TransferEntity)

    @Query("SELECT * FROM transfers ORDER BY timestamp DESC")
    fun getAllTransfers(): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE status = :status ORDER BY timestamp DESC")
    fun getTransfersByStatus(status: String): Flow<List<TransferEntity>>

    @Query("UPDATE transfers SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int)

    @Query("UPDATE transfers SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, completedAt: Long? = null)

    @Query("DELETE FROM transfers WHERE id = :id")
    suspend fun deleteTransfer(id: Long)

    @Query("DELETE FROM transfers")
    suspend fun clearAll()
}
