package com.example.filesharekt.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transfers")
data class TransferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val fileSize: Long,
    val direction: String, // "send" or "receive"
    val remotePeer: String,
    val status: String, // "pending", "in_progress", "completed", "failed", "cancelled"
    val progress: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val errorMessage: String? = null
)
