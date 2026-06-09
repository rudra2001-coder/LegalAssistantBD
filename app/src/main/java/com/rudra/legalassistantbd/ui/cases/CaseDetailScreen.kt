package com.rudra.legalassistantbd.ui.cases

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.rudra.legalassistantbd.core.database.entity.*
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.core.util.toFormattedDate
import com.rudra.legalassistantbd.core.util.toFormattedDateTime
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    caseId: Int,
    navController: NavController,
    viewModel: CaseViewModel = hiltViewModel()
) {
    LaunchedEffect(caseId) {
        viewModel.loadCase(caseId)
    }

    val case by viewModel.selectedCase.collectAsState()
    val evidence by viewModel.evidence.collectAsState()
    val hearings by viewModel.hearings.collectAsState()
    val bails by viewModel.bails.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val linkedSection by viewModel.linkedSection.collectAsState()
    val sectionProcedures by viewModel.sectionProcedures.collectAsState()
    val procedureProgress by viewModel.procedureProgress.collectAsState()
    val progressSummary by viewModel.progressSummary.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Hearings", "Documents", "Bail", "Notes")

    Scaffold(
        topBar = {
            TopBar(
                title = case?.title ?: "Case Details",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = scheme.background
    ) { padding ->
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            case?.let { cse ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = scheme.surface,
                        contentColor = scheme.primary,
                        divider = { HorizontalDivider(color = scheme.surfaceVariant) }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }

                    when (selectedTab) {
                        0 -> OverviewTab(cse, linkedSection, sectionProcedures, procedureProgress, progressSummary, evidence, clients, viewModel, scheme, c)
                        1 -> HearingsTab(caseId, hearings, viewModel, scheme, c)
                        2 -> DocumentsTab(caseId, documents, viewModel, scheme, c)
                        3 -> BailsTab(caseId, bails, viewModel, scheme, c)
                        4 -> NotesTab(caseId, notes, viewModel, scheme, c)
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewTab(
    cse: CaseEntity,
    linkedSection: LawSectionEntity?,
    sectionProcedures: List<ProcedureEntity>,
    procedureProgress: List<CaseProcedureProgressEntity>,
    progressSummary: Pair<Int, Int>,
    evidence: List<EvidenceEntity>,
    clients: List<ClientEntity>,
    viewModel: CaseViewModel,
    scheme: ColorScheme,
    c: AppColors
) {
    val client = clients.find { it.id == cse.clientId }
    var showEditDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAddEvidenceDialog by remember { mutableStateOf(false) }

    if (showStatusDialog) {
        StatusUpdateDialog(
            currentStatus = cse.status,
            onDismiss = { showStatusDialog = false },
            onSelect = { status ->
                viewModel.updateCaseStatus(cse.id, status)
                showStatusDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Case") },
            text = { Text("Are you sure you want to delete this case? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteCase(cse.id); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = scheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }

    if (showAddEvidenceDialog) {
        AddEvidenceDialog(
            onDismiss = { showAddEvidenceDialog = false },
            onAdd = { title, desc, path ->
                viewModel.addEvidence(cse.id, title, desc, path)
                showAddEvidenceDialog = false
            }
        )
    }

    if (showEditDialog) {
        EditCaseDialog(
            caseEntity = cse,
            clients = clients,
            sections = viewModel.allSections.value,
            onDismiss = { showEditDialog = false },
            onSave = { title, caseType, description, clientId, opponentName, opponentAdvocate, advocateName, advocatePhone, courtName, judgeName, policeStation, firNumber, firDate, filingNumber, caseYear, sectionId ->
                viewModel.updateCaseDetails(
                    caseId = cse.id, title = title, caseType = caseType, description = description,
                    opponentName = opponentName, opponentAdvocate = opponentAdvocate,
                    advocateName = advocateName, advocatePhone = advocatePhone,
                    courtName = courtName, judgeName = judgeName,
                    policeStation = policeStation, firNumber = firNumber, firDate = firDate,
                    filingNumber = filingNumber, caseYear = caseYear, sectionId = sectionId
                )
                showEditDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cse.caseNumber, style = MaterialTheme.typography.labelMedium, color = scheme.onSurfaceVariant)
                    Surface(
                        color = when(cse.status) {
                            "Active" -> c.successGreen; "Pending" -> c.warningOrange; else -> c.grayMedium
                        }.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text(cse.status, color = when(cse.status) { "Active" -> c.successGreen; "Pending" -> c.warningOrange; else -> c.grayMedium }, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) }
                }
                Spacer(Modifier.height(12.dp))
                Text(cse.title, style = MaterialTheme.typography.headlineSmall, color = scheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(cse.caseType, style = MaterialTheme.typography.bodyLarge, color = scheme.primary)
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Case Information", style = MaterialTheme.typography.titleMedium, color = scheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                DetailRow("Filing Date", cse.filingDate.toFormattedDate())
                cse.filingNumber?.let { DetailRow("Filing No", it) }
                cse.caseYear?.let { DetailRow("Case Year", it) }
                cse.courtName?.let { DetailRow("Court", it) }
                cse.judgeName?.let { DetailRow("Judge", it) }
                cse.nextHearing?.let { DetailRow("Next Hearing", it.toFormattedDate()) }
                cse.policeStation?.let { DetailRow("Police Station", it) }
                cse.firNumber?.let { DetailRow("FIR No", it) }
                cse.firDate?.let { DetailRow("FIR Date", it.toFormattedDate()) }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Parties", style = MaterialTheme.typography.titleMedium, color = scheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                if (client != null) {
                    client.fatherName?.let { DetailRow("Client", "${client.name} ($it)") } ?: DetailRow("Client", client.name)
                    client.phone?.let { DetailRow("Client Phone", it) }
                    client.occupation?.let { DetailRow("Occupation", it) }
                }
                cse.opponentName?.let { DetailRow("Opponent", it) }
                cse.opponentAdvocate?.let { DetailRow("Opp. Advocate", it) }
                cse.advocateName?.let { DetailRow("Your Advocate", it) }
                cse.advocatePhone?.let { DetailRow("Adv. Phone", it) }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Description", style = MaterialTheme.typography.titleMedium, color = scheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(cse.description ?: "No description", style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
            }
        }

        if (linkedSection != null && sectionProcedures.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Procedure Guidance", style = MaterialTheme.typography.titleMedium, color = scheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("${linkedSection.titleEn} - Progress: ${progressSummary.first}/${progressSummary.second}", style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    sectionProcedures.forEach { procedure ->
                        val progress = procedureProgress.find { it.procedureId == procedure.id }
                        ProcedureGuidanceStep(
                            procedure = procedure,
                            isCompleted = progress?.isCompleted == true,
                            stepNumber = sectionProcedures.indexOf(procedure) + 1,
                            onToggle = {
                                if (progress?.isCompleted == true) viewModel.markProcedureIncomplete(cse.id, procedure.id)
                                else viewModel.markProcedureCompleted(cse.id, procedure.id)
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Evidence (${evidence.size})", style = MaterialTheme.typography.titleMedium, color = scheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (evidence.isEmpty()) {
                    Text("No evidence added yet", style = MaterialTheme.typography.bodyMedium, color = c.grayMedium)
                } else {
                    evidence.forEach { ev ->
                        Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Description, contentDescription = null, tint = scheme.primary, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Column { Text(ev.title, color = scheme.onSurface, fontWeight = FontWeight.Medium); ev.description?.let { Text(it, color = scheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) } }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { showAddEvidenceDialog = true }, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add Evidence") }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            scheme.primaryButton(text = "Change Status", onClick = { showStatusDialog = true }, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showEditDialog = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Edit") }
            OutlinedButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = scheme.error)) { Text("Delete") }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun HearingsTab(
    caseId: Int,
    hearings: List<HearingEntity>,
    viewModel: CaseViewModel,
    scheme: ColorScheme,
    c: AppColors
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<HearingEntity?>(null) }

    if (showAddDialog) {
        AddHearingDialog(
            onDismiss = { showAddDialog = false },
            onSave = { date, type, court, judge, desc ->
                viewModel.addHearing(caseId, date, type, court, judge, desc)
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { hearing ->
        UpdateHearingDialog(
            hearing = hearing,
            onDismiss = { showEditDialog = null },
            onSave = { outcome, nextDate, notes ->
                viewModel.updateHearingOutcome(hearing.id, outcome, nextDate, notes)
                showEditDialog = null
            },
            onDelete = {
                viewModel.deleteHearing(hearing.id)
                showEditDialog = null
            }
        )
    }

    if (hearings.isEmpty()) {
        EmptyState(icon = Icons.Outlined.Event, title = "No Hearings Yet", subtitle = "Add your first hearing", modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Hearings (${hearings.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                    FilledTonalButton(onClick = { showAddDialog = true }, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add") }
                }
                Spacer(Modifier.height(8.dp))
            }
            items(hearings) { hearing ->
                Card(
                    onClick = { showEditDialog = hearing },
                    colors = CardDefaults.cardColors(containerColor = c.darkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(hearing.hearingType, style = MaterialTheme.typography.titleSmall, color = scheme.primary, fontWeight = FontWeight.Bold)
                            Text(hearing.hearingDate.toFormattedDate(), style = MaterialTheme.typography.labelMedium, color = scheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(4.dp))
                        hearing.courtName?.let { Text("Court: $it", style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant) }
                        hearing.judgeName?.let { Text("Judge: $it", style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant) }
                        hearing.description?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall, color = scheme.onSurface) }
                        hearing.outcome?.let { Spacer(Modifier.height(4.dp)); Text("Outcome: $it", style = MaterialTheme.typography.labelSmall, color = c.warningOrange) }
                        hearing.nextHearingDate?.let { Spacer(Modifier.height(2.dp)); Text("Next: ${it.toFormattedDate()}", style = MaterialTheme.typography.labelSmall, color = c.infoBlue) }
                    }
                }
            }
        }
    }

    if (hearings.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {}
    }
}

@Composable
fun DocumentsTab(
    caseId: Int,
    documents: List<CaseDocumentEntity>,
    viewModel: CaseViewModel,
    scheme: ColorScheme,
    c: AppColors
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val destFile = File(context.cacheDir, "case_images/${System.currentTimeMillis()}_image.jpg")
            destFile.parentFile?.mkdirs()
            context.contentResolver.openInputStream(it)?.use { input -> destFile.outputStream().use { output -> input.copyTo(output) } }
            viewModel.addDocument(caseId, "Image ${System.currentTimeMillis()}", null, destFile.absolutePath, "Image", "image/jpeg", destFile.length())
        }
    }

    val docPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val fileName = "doc_${System.currentTimeMillis()}"
            val ext = it.lastPathSegment?.substringAfterLast('.') ?: "doc"
            val destFile = File(context.cacheDir, "evidence/${fileName}.$ext")
            destFile.parentFile?.mkdirs()
            context.contentResolver.openInputStream(it)?.use { input -> destFile.outputStream().use { output -> input.copyTo(output) } }
            viewModel.addDocument(caseId, fileName, null, destFile.absolutePath, "Document", it.let { context.contentResolver.getType(it) }, destFile.length())
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Document", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showAddDialog = false; imagePickerLauncher.launch("image/*") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Image, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Add Image") }
                    OutlinedButton(onClick = { showAddDialog = false; docPickerLauncher.launch("*/*") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.AttachFile, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Add Document") }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }

    if (documents.isEmpty()) {
        EmptyState(icon = Icons.Outlined.Folder, title = "No Documents", subtitle = "Upload images or documents", modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Documents (${documents.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                    FilledTonalButton(onClick = { showAddDialog = true }, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add") }
                }
                Spacer(Modifier.height(8.dp))
            }
            items(documents) { doc ->
                Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (doc.documentType == "Image") {
                            val file = File(doc.filePath)
                            if (file.exists()) {
                                val bitmap = remember(file) {
                                    BitmapFactory.decodeFile(file.absolutePath)
                                }
                                if (bitmap != null) {
                                    Image(
                                        painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.asImageBitmap()),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (doc.documentType == "Image") Icons.Default.Image else Icons.Outlined.Description, contentDescription = null, tint = scheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.title, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface, fontWeight = FontWeight.Medium)
                                Text(doc.addedTimestamp.toFormattedDate(), style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { viewModel.deleteDocument(doc.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = scheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (documents.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {}
    }
}

@Composable
fun BailsTab(
    caseId: Int,
    bails: List<BailEntity>,
    viewModel: CaseViewModel,
    scheme: ColorScheme,
    c: AppColors
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<BailEntity?>(null) }

    if (showAddDialog) {
        AddBailDialog(
            onDismiss = { showAddDialog = false },
            onSave = { type, petitionDate, petitionNo, court, hearingDate ->
                viewModel.addBail(caseId, type, petitionDate, petitionNo, court, hearingDate)
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { bail ->
        UpdateBailDialog(
            bail = bail,
            onDismiss = { showEditDialog = null },
            onSave = { status, orderDate, orderDetails, surety, amount ->
                viewModel.updateBailStatus(bail.id, status, orderDate, orderDetails, surety, amount)
                showEditDialog = null
            },
            onDelete = {
                viewModel.deleteBail(bail.id)
                showEditDialog = null
            }
        )
    }

    if (bails.isEmpty()) {
        EmptyState(icon = Icons.Outlined.Gavel, title = "No Bail Petitions", subtitle = "Add bail information", modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Bail Petitions (${bails.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                    FilledTonalButton(onClick = { showAddDialog = true }, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add") }
                }
                Spacer(Modifier.height(8.dp))
            }
            items(bails) { bail ->
                val statusColor = when(bail.bailStatus) {
                    "Granted" -> c.successGreen; "Rejected" -> scheme.error; "Filed" -> c.infoBlue; else -> c.warningOrange
                }
                Card(
                    onClick = { showEditDialog = bail },
                    colors = CardDefaults.cardColors(containerColor = c.darkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("${bail.bailType} Bail", style = MaterialTheme.typography.titleSmall, color = scheme.primary, fontWeight = FontWeight.Bold)
                            Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                                Text(bail.bailStatus, color = statusColor, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        DetailRow("Petition Date", bail.petitionDate.toFormattedDate())
                        bail.petitionNumber?.let { DetailRow("Petition No", it) }
                        bail.courtName?.let { DetailRow("Court", it) }
                        bail.hearingDate?.let { DetailRow("Hearing", it.toFormattedDate()) }
                        bail.orderDate?.let { DetailRow("Order Date", it.toFormattedDate()) }
                        bail.bailAmount?.let { DetailRow("Amount", it) }
                    }
                }
            }
        }
    }

    if (bails.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {}
    }
}

@Composable
fun NotesTab(
    caseId: Int,
    notes: List<CaseNoteEntity>,
    viewModel: CaseViewModel,
    scheme: ColorScheme,
    c: AppColors
) {
    var noteText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Case Diary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = scheme.onSurface)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Write a note...") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors()
        )
        Spacer(Modifier.height(8.dp))
        scheme.primaryButton(
            text = "Add Note",
            onClick = { if (noteText.isNotBlank()) { viewModel.addNote(caseId, noteText); noteText = "" } },
            enabled = noteText.isNotBlank(),
            icon = Icons.Default.Add
        )

        Spacer(Modifier.height(16.dp))

        if (notes.isEmpty()) {
            Text("No notes yet", style = MaterialTheme.typography.bodyMedium, color = c.grayMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notes) { note ->
                    Card(colors = CardDefaults.cardColors(containerColor = c.darkCard), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(note.addedTimestamp.toFormattedDateTime(), style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(note.content, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
                            Spacer(Modifier.height(4.dp))
                            TextButton(onClick = { viewModel.deleteNote(note.id) }, colors = ButtonDefaults.textButtonColors(contentColor = scheme.error), contentPadding = PaddingValues(0.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Delete", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProcedureGuidanceStep(
    procedure: ProcedureEntity,
    isCompleted: Boolean,
    stepNumber: Int,
    onToggle: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) c.successGreen.copy(alpha = 0.08f) else c.darkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                if (isCompleted) Icons.Default.CheckCircleOutline else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) c.successGreen else c.grayMedium,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = scheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Step $stepNumber",
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (isCompleted) {
                        Spacer(Modifier.width(8.dp))
                        Text("Completed", style = MaterialTheme.typography.labelSmall, color = c.successGreen)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = procedure.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = procedure.titleBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = procedure.descriptionEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
                procedure.requiredDocuments?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Docs: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = c.warningOrange
                    )
                }
                procedure.duration?.let {
                    Text(
                        text = "Time: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = c.infoBlue
                    )
                }
            }
        }
    }
}

@Composable
fun StatusUpdateDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Constants.CASE_STATUSES.forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSelect(status) }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = status == currentStatus, onClick = { onSelect(status) })
                        Spacer(Modifier.width(8.dp))
                        Text(status)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCaseDialog(
    caseEntity: CaseEntity,
    clients: List<ClientEntity>,
    sections: List<LawSectionEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int?, String?, String?, String?, String?, String?, String?, String?, String?, Long?, String?, String?, Int?) -> Unit
) {
    var title by remember { mutableStateOf(caseEntity.title) }
    var caseType by remember { mutableStateOf(caseEntity.caseType) }
    var description by remember { mutableStateOf(caseEntity.description ?: "") }
    var opponentName by remember { mutableStateOf(caseEntity.opponentName ?: "") }
    var opponentAdvocate by remember { mutableStateOf(caseEntity.opponentAdvocate ?: "") }
    var advocateName by remember { mutableStateOf(caseEntity.advocateName ?: "") }
    var advocatePhone by remember { mutableStateOf(caseEntity.advocatePhone ?: "") }
    var courtName by remember { mutableStateOf(caseEntity.courtName ?: "") }
    var judgeName by remember { mutableStateOf(caseEntity.judgeName ?: "") }
    var policeStation by remember { mutableStateOf(caseEntity.policeStation ?: "") }
    var firNumber by remember { mutableStateOf(caseEntity.firNumber ?: "") }
    var firDate by remember { mutableStateOf(caseEntity.firDate) }
    var filingNumber by remember { mutableStateOf(caseEntity.filingNumber ?: "") }
    var caseYear by remember { mutableStateOf(caseEntity.caseYear ?: "") }
    var selectedClientId by remember { mutableStateOf(caseEntity.clientId) }
    var selectedSectionId by remember { mutableStateOf(caseEntity.sectionId) }
    var showFirDatePicker by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    if (showFirDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = firDate ?: System.currentTimeMillis())
        DatePickerDialog(onDismissRequest = { showFirDatePicker = false },
            confirmButton = { TextButton(onClick = { firDate = datePickerState.selectedDateMillis; showFirDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showFirDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Case", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Case Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = it }) {
                    OutlinedTextField(value = caseType, onValueChange = {}, readOnly = true, label = { Text("Case Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                    ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) { Constants.CASE_TYPES.forEach { t -> DropdownMenuItem(text = { Text(t) }, onClick = { caseType = t; expandedType = false }) } }
                }
                OutlinedTextField(value = opponentName, onValueChange = { opponentName = it }, label = { Text("Opponent Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = opponentAdvocate, onValueChange = { opponentAdvocate = it }, label = { Text("Opponent Advocate") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = advocateName, onValueChange = { advocateName = it }, label = { Text("Your Advocate") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = advocatePhone, onValueChange = { advocatePhone = it }, label = { Text("Adv. Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = courtName, onValueChange = { courtName = it }, label = { Text("Court Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = judgeName, onValueChange = { judgeName = it }, label = { Text("Judge Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = policeStation, onValueChange = { policeStation = it }, label = { Text("Police Station") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = firNumber, onValueChange = { firNumber = it }, label = { Text("FIR Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = firDate?.let { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) } ?: "", onValueChange = {}, readOnly = true, label = { Text("FIR Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showFirDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = filingNumber, onValueChange = { filingNumber = it }, label = { Text("Filing No") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                    OutlinedTextField(value = caseYear, onValueChange = { caseYear = it }, label = { Text("Case Year") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                }
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp), colors = fieldColors())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, caseType, description, selectedClientId, opponentName.ifBlank { null }, opponentAdvocate.ifBlank { null }, advocateName.ifBlank { null }, advocatePhone.ifBlank { null }, courtName.ifBlank { null }, judgeName.ifBlank { null }, policeStation.ifBlank { null }, firNumber.ifBlank { null }, firDate, filingNumber.ifBlank { null }, caseYear.ifBlank { null }, selectedSectionId)
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddEvidenceDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val context = LocalContext.current
    var filePath by remember { mutableStateOf<String?>(null) }

    val docPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val destFile = File(context.cacheDir, "evidence/doc_${System.currentTimeMillis()}")
            destFile.parentFile?.mkdirs()
            context.contentResolver.openInputStream(it)?.use { input -> destFile.outputStream().use { output -> input.copyTo(output) } }
            filePath = destFile.absolutePath
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Evidence", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedButton(onClick = { docPickerLauncher.launch("*/*") }, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text(if (filePath != null) "File Selected" else "Attach File") }
            }
        },
        confirmButton = { Button(onClick = { if (title.isNotBlank()) { onAdd(title, desc, filePath) } }, enabled = title.isNotBlank(), shape = RoundedCornerShape(12.dp)) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHearingDialog(
    onDismiss: () -> Unit,
    onSave: (Long, String, String?, String?, String?) -> Unit
) {
    var hearingType by remember { mutableStateOf(Constants.HEARING_REGULAR) }
    var courtName by remember { mutableStateOf("") }
    var judgeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { selectedDate = datePickerState.selectedDateMillis ?: selectedDate; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Hearing", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = it }) {
                    OutlinedTextField(value = hearingType, onValueChange = {}, readOnly = true, label = { Text("Hearing Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                    ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        Constants.HEARING_TYPES.forEach { type -> DropdownMenuItem(text = { Text(type) }, onClick = { hearingType = type; expandedType = false }) }
                    }
                }
                OutlinedTextField(value = selectedDate.toFormattedDate(), onValueChange = {}, readOnly = true, label = { Text("Hearing Date *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
                OutlinedTextField(value = courtName, onValueChange = { courtName = it }, label = { Text("Court Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = judgeName, onValueChange = { judgeName = it }, label = { Text("Judge Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
            }
        },
        confirmButton = { Button(onClick = { onSave(selectedDate, hearingType, courtName.ifBlank { null }, judgeName.ifBlank { null }, description.ifBlank { null }) }, shape = RoundedCornerShape(12.dp)) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateHearingDialog(
    hearing: HearingEntity,
    onDismiss: () -> Unit,
    onSave: (String?, Long?, String?) -> Unit,
    onDelete: () -> Unit
) {
    var outcome by remember { mutableStateOf(hearing.outcome ?: "") }
    var notes by remember { mutableStateOf(hearing.notes ?: "") }
    var nextDate by remember { mutableStateOf(hearing.nextHearingDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedOutcome by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = nextDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { nextDate = datePickerState.selectedDateMillis; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Hearing", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Date: ${hearing.hearingDate.toFormattedDate()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Type: ${hearing.hearingType}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                ExposedDropdownMenuBox(expanded = expandedOutcome, onExpandedChange = { expandedOutcome = it }) {
                    OutlinedTextField(value = outcome.ifBlank { "Select Outcome" }, onValueChange = {}, readOnly = true, label = { Text("Outcome") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOutcome) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                    ExposedDropdownMenu(expanded = expandedOutcome, onDismissRequest = { expandedOutcome = false }) {
                        Constants.HEARING_OUTCOMES.forEach { o -> DropdownMenuItem(text = { Text(o) }, onClick = { outcome = o; expandedOutcome = false }) }
                    }
                }
                OutlinedTextField(value = nextDate?.toFormattedDate() ?: "", onValueChange = {}, readOnly = true, label = { Text("Next Hearing Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDelete, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Delete") }
                Button(onClick = { onSave(outcome.ifBlank { null }, nextDate, notes.ifBlank { null }) }, shape = RoundedCornerShape(12.dp)) { Text("Update") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBailDialog(
    onDismiss: () -> Unit,
    onSave: (String, Long, String?, String?, Long?) -> Unit
) {
    var bailType by remember { mutableStateOf(Constants.BAIL_REGULAR) }
    var petitionNumber by remember { mutableStateOf("") }
    var courtName by remember { mutableStateOf("") }
    var petitionDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var hearingDate by remember { mutableStateOf<Long?>(null) }
    var showPetitionDatePicker by remember { mutableStateOf(false) }
    var showHearingDatePicker by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    if (showPetitionDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = petitionDate)
        DatePickerDialog(
            onDismissRequest = { showPetitionDatePicker = false },
            confirmButton = { TextButton(onClick = { petitionDate = datePickerState.selectedDateMillis ?: petitionDate; showPetitionDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showPetitionDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }
    if (showHearingDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = hearingDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showHearingDatePicker = false },
            confirmButton = { TextButton(onClick = { hearingDate = datePickerState.selectedDateMillis; showHearingDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showHearingDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bail Petition", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = it }) {
                    OutlinedTextField(value = bailType, onValueChange = {}, readOnly = true, label = { Text("Bail Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                    ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) { Constants.BAIL_TYPES.forEach { t -> DropdownMenuItem(text = { Text(t) }, onClick = { bailType = t; expandedType = false }) } }
                }
                OutlinedTextField(value = petitionDate.toFormattedDate(), onValueChange = {}, readOnly = true, label = { Text("Petition Date *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showPetitionDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
                OutlinedTextField(value = petitionNumber, onValueChange = { petitionNumber = it }, label = { Text("Petition Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = courtName, onValueChange = { courtName = it }, label = { Text("Court Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = hearingDate?.toFormattedDate() ?: "", onValueChange = {}, readOnly = true, label = { Text("Hearing Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showHearingDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
            }
        },
        confirmButton = { Button(onClick = { onSave(bailType, petitionDate, petitionNumber.ifBlank { null }, courtName.ifBlank { null }, hearingDate) }, shape = RoundedCornerShape(12.dp)) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBailDialog(
    bail: BailEntity,
    onDismiss: () -> Unit,
    onSave: (String, Long?, String?, String?, String?) -> Unit,
    onDelete: () -> Unit
) {
    var status by remember { mutableStateOf(bail.bailStatus) }
    var orderDetails by remember { mutableStateOf(bail.orderDetails ?: "") }
    var suretyDetails by remember { mutableStateOf(bail.suretyDetails ?: "") }
    var bailAmount by remember { mutableStateOf(bail.bailAmount ?: "") }
    var orderDate by remember { mutableStateOf(bail.orderDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = orderDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { orderDate = datePickerState.selectedDateMillis; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Bail", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Petition: ${bail.bailType} - ${bail.petitionDate.toFormattedDate()}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                ExposedDropdownMenuBox(expanded = expandedStatus, onExpandedChange = { expandedStatus = it }) {
                    OutlinedTextField(value = status, onValueChange = {}, readOnly = true, label = { Text("Status") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                    ExposedDropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }) { Constants.BAIL_STATUSES.forEach { s -> DropdownMenuItem(text = { Text(s) }, onClick = { status = s; expandedStatus = false }) } }
                }
                OutlinedTextField(value = orderDate?.toFormattedDate() ?: "", onValueChange = {}, readOnly = true, label = { Text("Order Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
                OutlinedTextField(value = orderDetails, onValueChange = { orderDetails = it }, label = { Text("Order Details") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = suretyDetails, onValueChange = { suretyDetails = it }, label = { Text("Surety Details") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = bailAmount, onValueChange = { bailAmount = it }, label = { Text("Bail Amount") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDelete, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Delete") }
                Button(onClick = { onSave(status, orderDate, orderDetails.ifBlank { null }, suretyDetails.ifBlank { null }, bailAmount.ifBlank { null }) }, shape = RoundedCornerShape(12.dp)) { Text("Update") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
