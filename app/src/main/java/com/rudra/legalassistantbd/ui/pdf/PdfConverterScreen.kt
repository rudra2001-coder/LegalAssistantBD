package com.rudra.legalassistantbd.ui.pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.pdf_converter.ParsedSection

import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.export.ExportShareUtil
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

@Composable
fun PdfConverterScreen(
    navController: NavController,
    viewModel: PdfViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val exportShareUtil = remember { ExportShareUtil(context) }
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

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
            TopBar(
                title = "PDF Law Importer",
                onBackClick = {
                    if (state.isImportComplete) {
                        navController.navigate(Constants.ROUTE_SEARCH) {
                            popUpTo(Constants.ROUTE_DASHBOARD) { inclusive = false }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        },
        containerColor = scheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Import Law Books",
                style = MaterialTheme.typography.headlineMedium,
                color = scheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Upload PDF law books to convert into searchable legal sections",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            StageIndicator(currentStage = state.stage)

            Spacer(Modifier.height(20.dp))

            when (state.stage) {
                PdfStage.SELECT -> SelectStage(
                    fileName = state.fileName,
                    showSettingsHint = state.showSettingsHint,
                    onPickFile = { pdfLauncher.launch("application/pdf") },
                    onStart = { viewModel.startExtraction() },
                    onDismissHint = { viewModel.dismissSettingsHint() }
                )
                PdfStage.EXTRACT -> ExtractStage(
                    currentPage = state.currentPage,
                    pageCount = state.pageCount
                )
                PdfStage.DETECT -> DetectStage(
                    sections = state.extractedSections,
                    isLoading = state.isLoading,
                    onImport = { viewModel.importToDatabase() }
                )
                PdfStage.IMPORT -> ImportCompleteStage(
                    fileName = state.fileName,
                    sectionsCount = state.importedSections.size,
                    onViewInSearch = {
                        navController.navigate(Constants.ROUTE_SEARCH) {
                            popUpTo(Constants.ROUTE_DASHBOARD) { inclusive = false }
                        }
                    },
                    onShareSummary = {
                        exportShareUtil.shareImportSummary(
                            fileName = state.fileName,
                            sections = state.importedSections
                        )
                    },
                    onImportAnother = {
                        viewModel.reset()
                    }
                )
            }

            state.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = scheme.error.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Error, null, tint = scheme.error)
                        Spacer(Modifier.width(12.dp))
                        Text(error, color = scheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StageIndicator(currentStage: PdfStage) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    val stages = listOf(PdfStage.SELECT, PdfStage.EXTRACT, PdfStage.DETECT, PdfStage.IMPORT)
    val labels = listOf("Select", "Extract", "Detect", "Import")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            val isActive = stage == currentStage
            val isDone = stage.ordinal < currentStage.ordinal

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        isDone -> c.successGreen
                        isActive -> scheme.primary
                        else -> scheme.surface
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when {
                            isDone -> Icon(Icons.Default.Check, null, tint = scheme.background, modifier = Modifier.size(18.dp))
                            isActive -> Text("${index + 1}", color = scheme.background, fontWeight = FontWeight.Bold)
                            else -> Text("${index + 1}", color = c.grayMedium)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = labels[index],
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isDone || isActive -> scheme.onSurface
                        else -> c.grayMedium
                    }
                )
            }

            if (index < stages.size - 1) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.5f)
                        .then(
                            if (index < currentStage.ordinal) Modifier else Modifier
                        )
                ) {
                    HorizontalDivider(
                        color = if (index < currentStage.ordinal) c.successGreen else scheme.surface,
                        thickness = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectStage(
    fileName: String,
    showSettingsHint: Boolean,
    onPickFile: () -> Unit,
    onStart: () -> Unit,
    onDismissHint: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column {
        Card(
            onClick = onPickFile,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = c.darkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.PictureAsPdf,
                    null,
                    tint = scheme.error,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (fileName.isBlank()) "Select PDF File" else fileName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (fileName.isBlank()) scheme.onSurfaceVariant else scheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                if (fileName.isBlank()) {
                    Text(
                        "Tap to browse files",
                        style = MaterialTheme.typography.bodySmall,
                        color = c.grayMedium
                    )
                }
            }
        }

        if (fileName.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            scheme.primaryButton(
                text = "Start Extraction",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.PlayArrow
            )
        }

        if (showSettingsHint && fileName.isBlank()) {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = scheme.primary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Info, null, tint = scheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tip", color = scheme.primary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Upload PDF files containing Bangladesh law sections. Supports Bengali (ধারা) and English formats.",
                            color = scheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = onDismissHint) {
                        Icon(Icons.Default.Close, null, tint = c.grayMedium, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtractStage(currentPage: Int, pageCount: Int) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = c.darkCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = scheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Extracting text from PDF...",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                if (pageCount > 0) {
                    Text(
                        text = "Page $currentPage of $pageCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { currentPage.toFloat() / pageCount.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = scheme.primary,
                        trackColor = scheme.surface
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Analyzing document structure...",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.grayMedium
                )
            }
        }
    }
}

@Composable
private fun DetectStage(
    sections: List<ParsedSection>,
    isLoading: Boolean,
    onImport: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Found ${sections.size} sections",
                style = MaterialTheme.typography.titleLarge,
                color = scheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            if (sections.isNotEmpty()) {
                Text(
                    text = "${sections.filter { it.content.isNotBlank() }.size} with content",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.grayMedium
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(sections) { index, section ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = c.darkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = scheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = section.sectionNumber,
                                    color = scheme.primary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = section.title.ifBlank { "Untitled Section" },
                                style = MaterialTheme.typography.titleSmall,
                                color = scheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (section.content.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = section.content.take(120),
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        scheme.primaryButton(
            text = if (isLoading) "Importing..." else "Import ${sections.size} Sections to Database",
            onClick = onImport,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Download,
            enabled = sections.isNotEmpty() && !isLoading
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Sections will be searchable via FTS5 full-text search",
            style = MaterialTheme.typography.bodySmall,
            color = c.grayMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun ImportCompleteStage(
    fileName: String,
    sectionsCount: Int,
    onViewInSearch: () -> Unit,
    onShareSummary: () -> Unit,
    onImportAnother: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = c.darkCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = c.successGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = c.successGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Import Successful",
                    style = MaterialTheme.typography.headlineSmall,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$sectionsCount sections added to database",
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Auto-generated basic legal procedures",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.grayMedium
                )
                Spacer(Modifier.height(24.dp))

                scheme.primaryButton(
                    text = "View in Search",
                    onClick = onViewInSearch,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Search
                )
                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onShareSummary,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = scheme.primary),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Share Import Summary")
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onImportAnother) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = scheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text("Import Another PDF", color = scheme.onSurfaceVariant)
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
