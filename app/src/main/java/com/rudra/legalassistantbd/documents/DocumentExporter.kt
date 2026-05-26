package com.rudra.legalassistantbd.documents

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentExporter @Inject constructor() {

    fun exportToText(context: Context, content: String, fileName: String): Uri? {
        return try {
            val file = File(context.cacheDir, "documents")
            file.mkdirs()
            val textFile = File(file, fileName.replace(" ", "_") + ".txt")
            textFile.writeText(content)

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                textFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareDocument(context: Context, content: String, fileName: String) {
        val uri = exportToText(context, content, fileName)
        if (uri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Document"))
        }
    }
}
