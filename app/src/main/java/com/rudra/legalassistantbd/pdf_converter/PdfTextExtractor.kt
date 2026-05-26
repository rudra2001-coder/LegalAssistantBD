package com.rudra.legalassistantbd.pdf_converter

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfTextExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        PDFBoxResourceLoader.init(context)
    }

    fun extractText(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { document ->
                    val stripper = PDFTextStripper()
                    stripper.sortByPosition = true
                    stripper.getText(document)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun extractTextWithStructure(uri: Uri): List<LawPageContent> {
        val pages = mutableListOf<LawPageContent>()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { document ->
                    val stripper = PDFTextStripper()
                    stripper.sortByPosition = true
                    for (i in 0 until document.numberOfPages) {
                        stripper.startPage = i + 1
                        stripper.endPage = i + 1
                        val text = stripper.getText(document)
                        pages.add(LawPageContent(pageNumber = i + 1, text = text))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pages
    }
}

data class LawPageContent(
    val pageNumber: Int,
    val text: String
)
