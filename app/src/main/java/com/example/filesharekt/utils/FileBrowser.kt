package com.example.filesharekt.utils

import android.content.Context
import android.os.Environment
import java.io.File

object FileBrowser {
    fun getDownloadsDirectory(context: Context): File {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: File(context.filesDir, "downloads")
    }

    fun getDocumentsDirectory(context: Context): File {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: File(context.filesDir, "documents")
    }

    fun listFiles(directory: File, extension: String? = null): List<File> {
        return try {
            directory.listFiles { file ->
                if (extension != null) file.extension == extension else true
            }?.toList()?.sorted() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getFileSize(file: File): Long {
        return try {
            file.length()
        } catch (e: Exception) {
            0L
        }
    }

    fun formatFileSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        return String.format("%.2f %s", size, units[unitIndex])
    }
}
