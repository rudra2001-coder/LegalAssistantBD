package com.rudra.legalassistantbd.ui.pdf

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.pdf_converter.JsonConverter
import com.rudra.legalassistantbd.pdf_converter.PdfTextExtractor
import com.rudra.legalassistantbd.pdf_converter.StructureDetector
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PdfConverterState(
    val selectedUri: Uri? = null,
    val fileName: String = "",
    val extractedSections: List<com.rudra.legalassistantbd.pdf_converter.ParsedSection> = emptyList(),
    val jsonOutput: String = "",
    val isLoading: Boolean = false,
    val isImported: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PdfViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfTextExtractor: PdfTextExtractor,
    private val structureDetector: StructureDetector,
    private val jsonConverter: JsonConverter,
    private val lawRepository: LawRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PdfConverterState())
    val state: StateFlow<PdfConverterState> = _state.asStateFlow()

    private var nextLawId = 1
    private var nextSectionId = 1

    fun setPdfUri(uri: Uri, fileName: String) {
        _state.update { it.copy(selectedUri = uri, fileName = fileName, isImported = false, error = null) }
        extractAndConvert(uri)
    }

    private fun extractAndConvert(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val text = pdfTextExtractor.extractText(uri)
                if (text == null || text.isBlank()) {
                    _state.update { it.copy(isLoading = false, error = "Could not extract text from PDF") }
                    return@launch
                }
                val sections = structureDetector.detectSections(text)
                val json = jsonConverter.convertToJson(sections, _state.value.fileName)
                _state.update {
                    it.copy(
                        extractedSections = sections,
                        jsonOutput = json,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun importToDatabase() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val lawId = System.currentTimeMillis().toInt()
                val fileName = _state.value.fileName.removeSuffix(".pdf").take(50)
                val law = LawEntity(
                    id = lawId,
                    titleEn = fileName,
                    titleBn = fileName,
                    shortTitle = fileName,
                    year = 2024,
                    description = "Imported from PDF: ${_state.value.fileName}",
                    isActive = true
                )
                lawRepository.insertLaws(listOf(law))

                val sections = _state.value.extractedSections.mapIndexed { index, parsed ->
                    LawSectionEntity(
                        id = "${lawId}_$index".hashCode(),
                        lawId = lawId,
                        sectionNumber = parsed.sectionNumber,
                        titleEn = parsed.title,
                        titleBn = parsed.title,
                        contentEn = parsed.content,
                        contentBn = parsed.content,
                        orderIndex = index
                    )
                }
                lawRepository.insertSections(sections)

                _state.update { it.copy(isLoading = false, isImported = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Import failed: ${e.message}") }
            }
        }
    }
}

@Composable
fun PdfConverterScreen(
    navController: NavController,
    viewModel: PdfViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = getFileName(context, it) ?: "law_book.pdf"
            viewModel.setPdfUri(it, fileName)
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = "PDF to Law Converter", onBackClick = { navController.popBackStack() })
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Import Law Books",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteSoft,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Upload a PDF law book to convert it into structured legal data",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayLight
            )
            Spacer(Modifier.height(24.dp))

            Card(
                onClick = { pdfLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.PictureAsPdf,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Select PDF File",
                        style = MaterialTheme.typography.titleMedium,
                        color = WhiteSoft,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap to browse files",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayLight
                    )
                }
            }

            state.fileName.let { name ->
                if (name.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Description, contentDescription = null, tint = Gold)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, color = WhiteSoft, fontWeight = FontWeight.Medium)
                                Text("${state.extractedSections.size} sections found", color = GrayLight, style = MaterialTheme.typography.bodySmall)
                            }
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Gold, modifier = Modifier.size(24.dp))
                            } else if (state.isImported) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            }
                        }
                    }
                }
            }

            state.error?.let { error ->
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Error, contentDescription = null, tint = ErrorRed)
                        Spacer(Modifier.width(12.dp))
                        Text(error, color = ErrorRed, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (state.extractedSections.isNotEmpty() && !state.isImported) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Preview (${state.extractedSections.size} sections)",
                    style = MaterialTheme.typography.titleLarge,
                    color = WhiteSoft,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                state.extractedSections.take(5).forEach { section ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Section ${section.sectionNumber}: ${section.title}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Gold,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = section.content.take(100) + "...",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayLight
                            )
                        }
                    }
                }

                if (state.extractedSections.size > 5) {
                    Text(
                        text = "... and ${state.extractedSections.size - 5} more sections",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayMedium
                    )
                }

                Spacer(Modifier.height(24.dp))
                GoldButton(
                    text = "Import to Database",
                    onClick = { viewModel.importToDatabase() },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Download
                )
            }

            if (state.isImported) {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Import Successful", color = SuccessGreen, fontWeight = FontWeight.Bold)
                            Text("${state.extractedSections.size} sections added to database", color = GrayLight, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}
