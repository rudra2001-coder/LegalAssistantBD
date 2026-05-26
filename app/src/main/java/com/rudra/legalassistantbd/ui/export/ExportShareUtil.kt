package com.rudra.legalassistantbd.ui.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import dagger.hilt.android.scopes.ViewModelScoped
import java.io.File

@ViewModelScoped
class ExportShareUtil(private val context: Context) {

    fun shareSection(section: LawSectionEntity) {
        val text = buildString {
            appendLine("=== ${section.titleEn} ===")
            appendLine("Section: ${section.sectionNumber}")
            appendLine()
            appendLine(section.contentEn)
            appendLine()
            appendLine("--- Exported from Legal Assistant BD ---")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, section.titleEn)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Section"))
    }

    fun shareImportSummary(fileName: String, sections: List<LawSectionEntity>) {
        val text = buildString {
            appendLine("=== Legal Assistant BD - Import Summary ===")
            appendLine("File: $fileName")
            appendLine("Sections imported: ${sections.size}")
            appendLine()
            appendLine("--- Sections ---")
            sections.forEachIndexed { index, section ->
                appendLine("${index + 1}. Section ${section.sectionNumber}: ${section.titleEn}")
            }
            appendLine()
            appendLine("--- Legal Assistant BD ---")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Import Summary: $fileName")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Import Summary"))
    }

    fun exportSectionAsText(section: LawSectionEntity): File? {
        return try {
            val dir = File(context.cacheDir, "documents")
            dir.mkdirs()
            val file = File(dir, "section_${section.sectionNumber}_${section.id}.txt")
            file.writeText(
                buildString {
                    appendLine("=== ${section.titleEn} ===")
                    appendLine("Section: ${section.sectionNumber}")
                    appendLine()
                    appendLine(section.contentEn)
                }
            )
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareExportedFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share File"))
        } catch (e: Exception) {
            sharePlainText(file)
        }
    }

    private fun sharePlainText(file: File) {
        val text = file.readText()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
    }
}
