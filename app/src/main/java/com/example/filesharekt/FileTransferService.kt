package com.example.filesharekt

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.ServerSocket
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

object FileTransferService {
    const val PORT = 8988
    private val TAG = "FileTransferService"

    private val logFile by lazy {
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "filesharekt_transfer.log")
    }

    fun startServer(context: Context) {
        thread(isDaemon = true) {
            try {
                val server = ServerSocket(PORT)
                log("Server listening on port $PORT")

                while (true) {
                    val client = server.accept()
                    log("Client connected from ${client.inetAddress.hostAddress}")

                    thread(isDaemon = true) {
                        try {
                            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "received_${System.currentTimeMillis()}")
                            file.parentFile?.mkdirs()

                            FileOutputStream(file).use { out ->
                                client.getInputStream().use { input ->
                                    var totalBytes = 0L
                                    val buffer = ByteArray(65536)
                                    var bytesRead: Int
                                    val startTime = System.currentTimeMillis()

                                    while (input.read(buffer).also { bytesRead = it } > 0) {
                                        out.write(buffer, 0, bytesRead)
                                        totalBytes += bytesRead
                                    }

                                    val duration = System.currentTimeMillis() - startTime
                                    val speed = if (duration > 0) (totalBytes / (duration / 1000.0)).toLong() else 0
                                    log("File received: ${file.name} (${formatBytes(totalBytes)}, ${formatBytes(speed)}/s)")
                                }
                            }
                            client.close()
                        } catch (e: Exception) {
                            log("Error handling client: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                log("Server error: ${e.message}")
            }
        }
    }

    fun log(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val logEntry = "[$timestamp] $message\n"

        Log.i(TAG, message)

        try {
            logFile.appendText(logEntry)
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to log", e)
        }
    }

    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        return String.format("%.2f %s", size, units[unitIndex])
    }

    fun getLogs(): String = try {
        logFile.readText()
    } catch (e: Exception) {
        "No logs available"
    }

    fun clearLogs() {
        try {
            logFile.writeText("")
            log("Logs cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing logs", e)
        }
    }
}
