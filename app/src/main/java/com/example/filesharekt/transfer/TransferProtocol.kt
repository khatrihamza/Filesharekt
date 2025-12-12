package com.example.filesharekt.transfer

import android.util.Log
import kotlinx.coroutines.*
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

/**
 * Enhanced transfer protocol with metadata support.
 * Format: [MAGIC(4)][VERSION(1)][TYPE(1)][METADATA_LEN(4)][METADATA][DATA]
 */
object TransferProtocol {
    private const val TAG = "TransferProtocol"
    const val MAGIC = 0x46534854L // "FSHT" in hex
    const val VERSION = 1
    const val TYPE_INIT = 1 // Init message with file metadata
    const val TYPE_DATA = 2 // File data chunk
    const val TYPE_ACK = 3 // Acknowledgment
    const val TYPE_ERROR = 4 // Error message
    const val TYPE_CANCEL = 5 // Cancel transfer
    const val CHUNK_SIZE = 64 * 1024 // 64KB chunks

    data class FileMetadata(
        val fileName: String,
        val fileSize: Long,
        val mimeType: String = "application/octet-stream",
        val transferId: Long,
        val checksum: Long = 0
    )

    fun sendFile(
        socket: Socket,
        filePath: String,
        fileName: String,
        fileSize: Long,
        transferId: Long,
        onProgress: (bytesTransferred: Long) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            socket.use { sock ->
                val out = DataOutputStream(sock.getOutputStream())

                // Send init message with metadata
                val metadata = FileMetadata(fileName, fileSize, transferId = transferId)
                sendInitMessage(out, metadata)

                // Send file data
                val file = File(filePath)
                file.inputStream().use { fileIn ->
                    var bytesRead: Long = 0
                    val buffer = ByteArray(CHUNK_SIZE)
                    var len: Int
                    while (fileIn.read(buffer).also { len = it } > 0) {
                        sendDataMessage(out, buffer, len)
                        bytesRead += len
                        onProgress(bytesRead)
                    }
                }

                Log.i(TAG, "File sent: $fileName ($fileSize bytes)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending file", e)
            onError(e.message ?: "Unknown error")
        }
    }

    fun receiveFile(
        socket: Socket,
        outputDir: String,
        onMetadata: (FileMetadata) -> Unit,
        onProgress: (bytesTransferred: Long) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            socket.use { sock ->
                val input = DataInputStream(sock.getInputStream())

                // Read init message
                val metadata = readInitMessage(input)
                onMetadata(metadata)

                // Receive file data
                val output = File(outputDir, metadata.fileName)
                output.parentFile?.mkdirs()
                output.outputStream().use { fileOut ->
                    var bytesRead: Long = 0
                    while (true) {
                        val msg = readMessage(input) ?: break
                        if (msg.type == TYPE_DATA) {
                            fileOut.write(msg.data)
                            bytesRead += msg.data.size
                            onProgress(bytesRead)
                        } else if (msg.type == TYPE_ERROR) {
                            throw Exception(String(msg.data))
                        }
                    }
                }

                Log.i(TAG, "File received: ${metadata.fileName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error receiving file", e)
            onError(e.message ?: "Unknown error")
        }
    }

    private fun sendInitMessage(out: DataOutputStream, metadata: FileMetadata) {
        val json = """{"fileName":"${metadata.fileName}","fileSize":${metadata.fileSize},"mimeType":"${metadata.mimeType}","transferId":${metadata.transferId}}"""
        val metadataBytes = json.toByteArray(Charsets.UTF_8)
        sendMessage(out, TYPE_INIT, metadataBytes)
    }

    private fun readInitMessage(input: DataInputStream): FileMetadata {
        val msg = readMessage(input) ?: throw IOException("No init message")
        val json = String(msg.data, Charsets.UTF_8)
        // Simple JSON parsing (in production, use proper JSON library)
        val fileNameMatch = Regex(""""fileName":"([^"]*)"""").find(json)
        val fileSizeMatch = Regex(""""fileSize":(\d+)""").find(json)
        val transferIdMatch = Regex(""""transferId":(\d+)""").find(json)

        val fileName = fileNameMatch?.groupValues?.get(1) ?: "unknown"
        val fileSize = fileSizeMatch?.groupValues?.get(1)?.toLong() ?: 0L
        val transferId = transferIdMatch?.groupValues?.get(1)?.toLong() ?: 0L

        return FileMetadata(fileName, fileSize, transferId = transferId)
    }

    private fun sendMessage(out: DataOutputStream, type: Int, data: ByteArray) {
        out.writeLong(MAGIC)
        out.writeByte(VERSION)
        out.writeByte(type)
        out.writeInt(data.size)
        out.write(data)
        out.flush()
    }

    private fun sendDataMessage(out: DataOutputStream, data: ByteArray, len: Int) {
        sendMessage(out, TYPE_DATA, data.copyOfRange(0, len))
    }

    private fun readMessage(input: DataInputStream): Message? {
        return try {
            val magic = input.readLong()
            if (magic != MAGIC) throw IOException("Invalid magic number")

            val version = input.readByte()
            val type = input.readByte()
            val dataLen = input.readInt()

            if (dataLen > 10 * 1024 * 1024) throw IOException("Message too large")

            val data = ByteArray(dataLen)
            input.readFully(data)

            Message(type.toInt(), data)
        } catch (e: EOFException) {
            null
        }
    }

    data class Message(val type: Int, val data: ByteArray)
}

/**
 * Manages concurrent file transfers with queue and state management.
 */
class TransferQueue {
    private val TAG = "TransferQueue"
    private val queue = mutableListOf<TransferTask>()
    private val activeTransfers = ConcurrentHashMap<Long, TransferTask>()
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    data class TransferTask(
        val id: Long,
        val fileName: String,
        val fileSize: Long,
        val direction: String,
        val remotePeer: String,
        val filePath: String?,
        val outputDir: String?,
        var status: String = "pending",
        var progress: Int = 0,
        var errorMessage: String? = null
    )

    fun enqueueTransfer(task: TransferTask) {
        synchronized(queue) {
            queue.add(task)
            Log.d(TAG, "Transfer enqueued: ${task.fileName} (total: ${queue.size})")
        }
        processQueue()
    }

    fun cancelTransfer(transferId: Long) {
        activeTransfers[transferId]?.status = "cancelled"
        activeTransfers.remove(transferId)
        synchronized(queue) {
            queue.removeAll { it.id == transferId }
        }
    }

    fun getActiveTransfers(): List<TransferTask> = activeTransfers.values.toList()

    fun getQueuedTransfers(): List<TransferTask> {
        synchronized(queue) {
            return queue.filter { it.status == "pending" }
        }
    }

    private fun processQueue() {
        scope.launch {
            while (true) {
                val task = synchronized(queue) {
                    queue.firstOrNull { it.status == "pending" }
                } ?: break

                task.status = "in_progress"
                activeTransfers[task.id] = task
                // Transfer execution happens here (to be integrated with FileTransferService)
                delay(100) // Placeholder
                task.status = "completed"
                activeTransfers.remove(task.id)
            }
        }
    }

    fun shutdown() {
        scope.cancel()
    }
}
