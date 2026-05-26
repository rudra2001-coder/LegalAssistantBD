package com.rudra.legalassistantbd.ui.pdf

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.PdfImportEntity
import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.pdf_converter.PdfTextExtractor
import com.rudra.legalassistantbd.pdf_converter.ParsedSection
import com.rudra.legalassistantbd.pdf_converter.StructureDetector
import com.rudra.legalassistantbd.ui.procedure.ProcedureGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PdfStage { SELECT, EXTRACT, DETECT, IMPORT }

data class PdfImportState(
    val stage: PdfStage = PdfStage.SELECT,
    val selectedUri: Uri? = null,
    val fileName: String = "",
    val pageCount: Int = 0,
    val currentPage: Int = 0,
    val extractedText: String = "",
    val extractedSections: List<ParsedSection> = emptyList(),
    val importedSections: List<LawSectionEntity> = emptyList(),
    val lawId: Int? = null,
    val isLoading: Boolean = false,
    val isImportComplete: Boolean = false,
    val error: String? = null,
    val showSettingsHint: Boolean = true
)

@HiltViewModel
class PdfViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfTextExtractor: PdfTextExtractor,
    private val structureDetector: StructureDetector,
    private val lawRepository: LawRepository,
    private val procedureGenerator: ProcedureGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(PdfImportState())
    val state: StateFlow<PdfImportState> = _state.asStateFlow()

    private var _navigateToSearch = MutableStateFlow(false)
    val navigateToSearch: StateFlow<Boolean> = _navigateToSearch.asStateFlow()

    fun setPdfUri(uri: Uri, fileName: String) {
        _state.update {
            it.copy(
                selectedUri = uri,
                fileName = fileName,
                stage = PdfStage.SELECT,
                isImportComplete = false,
                error = null
            )
        }
    }

    fun startExtraction() {
        val uri = _state.value.selectedUri ?: return
        viewModelScope.launch {
            _state.update { it.copy(stage = PdfStage.EXTRACT, isLoading = true, error = null) }
            try {
                val count = pdfTextExtractor.getPageCount(uri)
                _state.update { it.copy(pageCount = count, currentPage = 0) }

                val text = pdfTextExtractor.extractTextWithProgress(uri) { current, total ->
                    _state.update { it.copy(currentPage = current, pageCount = total) }
                }

                if (text.isNullOrBlank()) {
                    _state.update { it.copy(isLoading = false, error = "Could not extract text from this PDF") }
                    return@launch
                }

                _state.update {
                    it.copy(extractedText = text, isLoading = false, stage = PdfStage.DETECT)
                }

                detectSections(text)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Extraction failed: ${e.message}") }
            }
        }
    }

    private fun detectSections(text: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val sections = structureDetector.detectSections(text)
                _state.update {
                    it.copy(
                        extractedSections = sections,
                        isLoading = false,
                        stage = PdfStage.DETECT
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Structure detection failed: ${e.message}") }
            }
        }
    }

    fun importToDatabase() {
        val state = _state.value
        if (state.extractedSections.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val fileName = state.fileName.removeSuffix(".pdf").removeSuffix(".PDF").take(50)
                val lawId = "${System.currentTimeMillis()}_${fileName.hashCode()}".hashCode()

                val law = LawEntity(
                    id = lawId,
                    titleEn = fileName,
                    titleBn = fileName,
                    shortTitle = fileName.take(20),
                    year = 2024,
                    description = "Imported from PDF: ${state.fileName}",
                    isActive = true
                )
                lawRepository.insertLaws(listOf(law))

                val sections = state.extractedSections.mapIndexed { index, parsed ->
                    LawSectionEntity(
                        id = "${lawId}_$index".hashCode(),
                        lawId = lawId,
                        sectionNumber = parsed.sectionNumber,
                        titleEn = parsed.title,
                        titleBn = parsed.title,
                        contentEn = parsed.content,
                        contentBn = parsed.content,
                        orderIndex = index,
                        isCustom = false
                    )
                }
                lawRepository.insertSections(sections)

                procedureGenerator.generateBasicProcedures(lawId, sections)

                val importRecord = PdfImportEntity(
                    fileName = state.fileName,
                    lawId = lawId,
                    sectionsCount = sections.size
                )
                lawRepository.insertImport(importRecord)

                _state.update {
                    it.copy(
                        isLoading = false,
                        isImportComplete = true,
                        stage = PdfStage.IMPORT,
                        importedSections = sections,
                        lawId = lawId
                    )
                }

                delay(1500)
                _navigateToSearch.value = true
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Import failed: ${e.message}") }
            }
        }
    }

    fun reset() {
        _state.value = PdfImportState()
        _navigateToSearch.value = false
    }

    fun dismissSettingsHint() {
        _state.update { it.copy(showSettingsHint = false) }
    }
}
