package com.rudra.legalassistantbd.ui.cases

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.database.entity.ClientEntity
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCaseScreen(
    navController: NavController,
    viewModel: CaseViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var caseType by remember { mutableStateOf(Constants.CASE_TYPE_CRIMINAL) }
    var description by remember { mutableStateOf("") }
    var opponentName by remember { mutableStateOf("") }
    var opponentAdvocate by remember { mutableStateOf("") }
    var advocateName by remember { mutableStateOf("") }
    var advocatePhone by remember { mutableStateOf("") }
    var courtName by remember { mutableStateOf("") }
    var judgeName by remember { mutableStateOf("") }
    var policeStation by remember { mutableStateOf("") }
    var firNumber by remember { mutableStateOf("") }
    var firDate by remember { mutableStateOf<Long?>(null) }
    var filingNumber by remember { mutableStateOf("") }
    var caseYear by remember { mutableStateOf("") }
    var showFirDatePicker by remember { mutableStateOf(false) }

    var expandedTypeMenu by remember { mutableStateOf(false) }
    var expandedSectionMenu by remember { mutableStateOf(false) }
    var selectedSectionId by remember { mutableStateOf<Int?>(null) }
    var selectedSectionTitle by remember { mutableStateOf("None") }
    var expandedClientMenu by remember { mutableStateOf(false) }
    var selectedClientId by remember { mutableStateOf<Int?>(null) }
    var selectedClientName by remember { mutableStateOf("None") }
    var showNewClientDialog by remember { mutableStateOf(false) }
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }

    val types = Constants.CASE_TYPES
    val sections by viewModel.allSections.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destFile = File(context.cacheDir, "case_images/${System.currentTimeMillis()}_image.jpg")
            destFile.parentFile?.mkdirs()
            context.contentResolver.openInputStream(it)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            uploadedImageUri = Uri.fromFile(destFile)
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = "doc_${System.currentTimeMillis()}"
            val ext = it.lastPathSegment?.substringAfterLast('.') ?: "doc"
            val destFile = File(context.cacheDir, "evidence/${fileName}.$ext")
            destFile.parentFile?.mkdirs()
            context.contentResolver.openInputStream(it)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            uploadedImageUri = Uri.fromFile(destFile)
        }
    }

    if (showNewClientDialog) {
        NewClientDialog(
            onDismiss = { showNewClientDialog = false },
            onSave = { name, phone, email, address, fatherName, occupation ->
                val id = viewModel.createClient(name, phone, email, address, fatherName, occupation)
                selectedClientId = id
                selectedClientName = name
                showNewClientDialog = false
            }
        )
    }

    if (showFirDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = firDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showFirDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    firDate = datePickerState.selectedDateMillis
                    showFirDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showFirDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = "Create Case", onBackClick = { navController.popBackStack() })
        },
        containerColor = scheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "New Case",
                style = MaterialTheme.typography.headlineMedium,
                color = scheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Case Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedTypeMenu,
                onExpandedChange = { expandedTypeMenu = it }
            ) {
                OutlinedTextField(
                    value = caseType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Case Type *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedTypeMenu,
                    onDismissRequest = { expandedTypeMenu = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { caseType = type; expandedTypeMenu = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedClientMenu,
                onExpandedChange = { expandedClientMenu = it }
            ) {
                OutlinedTextField(
                    value = selectedClientName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Client") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClientMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedClientMenu,
                    onDismissRequest = { expandedClientMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None", color = scheme.onSurfaceVariant) },
                        onClick = { selectedClientId = null; selectedClientName = "None"; expandedClientMenu = false }
                    )
                    clients.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.name) },
                            onClick = { selectedClientId = client.id; selectedClientName = client.name; expandedClientMenu = false }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("+ Add New Client", color = scheme.primary) },
                        onClick = { showNewClientDialog = true; expandedClientMenu = false }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = c.darkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Link Law Section (Optional)",
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Linking a section will guide you through its legal procedure",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedSectionMenu,
                        onExpandedChange = { expandedSectionMenu = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSectionTitle,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Section") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSectionMenu) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSectionMenu,
                            onDismissRequest = { expandedSectionMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None", color = scheme.onSurfaceVariant) },
                                onClick = { selectedSectionId = null; selectedSectionTitle = "None"; expandedSectionMenu = false }
                            )
                            sections.forEach { section ->
                                DropdownMenuItem(
                                    text = { Text("${section.sectionNumber}. ${section.titleEn}") },
                                    onClick = { selectedSectionId = section.id; selectedSectionTitle = "Sec ${section.sectionNumber}: ${section.titleEn}"; expandedSectionMenu = false }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Party Information", style = MaterialTheme.typography.titleSmall, color = scheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = opponentName, onValueChange = { opponentName = it }, label = { Text("Opponent Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = opponentAdvocate, onValueChange = { opponentAdvocate = it }, label = { Text("Opponent Advocate") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = advocateName, onValueChange = { advocateName = it }, label = { Text("Your Advocate Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = advocatePhone, onValueChange = { advocatePhone = it }, label = { Text("Advocate Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())

            Spacer(Modifier.height(16.dp))
            Text("Court & FIR Details", style = MaterialTheme.typography.titleSmall, color = scheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = courtName, onValueChange = { courtName = it }, label = { Text("Court Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = judgeName, onValueChange = { judgeName = it }, label = { Text("Judge Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = policeStation, onValueChange = { policeStation = it }, label = { Text("Police Station") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = firNumber, onValueChange = { firNumber = it }, label = { Text("FIR Number") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = firDate?.let { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) } ?: "", onValueChange = {}, readOnly = true, label = { Text("FIR Date") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors(), trailingIcon = { IconButton(onClick = { showFirDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } })
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = filingNumber, onValueChange = { filingNumber = it }, label = { Text("Filing Number") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = caseYear, onValueChange = { caseYear = it }, label = { Text("Case Year") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            }

            Spacer(Modifier.height(16.dp))
            Text("Description", style = MaterialTheme.typography.titleSmall, color = scheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(16.dp))
            Text("Attach Document/Image (Optional)", style = MaterialTheme.typography.titleSmall, color = scheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Image")
                }
                OutlinedButton(
                    onClick = { documentPickerLauncher.launch("*/*") },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Document")
                }
            }
            if (uploadedImageUri != null) {
                Spacer(Modifier.height(4.dp))
                Text("File attached: ${uploadedImageUri!!.lastPathSegment}", style = MaterialTheme.typography.bodySmall, color = c.successGreen)
            }

            Spacer(Modifier.height(32.dp))

            scheme.primaryButton(
                text = "Create Case",
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.createCase(
                            title = title,
                            caseType = caseType,
                            description = description,
                            clientId = selectedClientId,
                            opponentName = opponentName.ifBlank { null },
                            opponentAdvocate = opponentAdvocate.ifBlank { null },
                            advocateName = advocateName.ifBlank { null },
                            advocatePhone = advocatePhone.ifBlank { null },
                            courtName = courtName.ifBlank { null },
                            judgeName = judgeName.ifBlank { null },
                            policeStation = policeStation.ifBlank { null },
                            firNumber = firNumber.ifBlank { null },
                            firDate = firDate,
                            filingNumber = filingNumber.ifBlank { null },
                            caseYear = caseYear.ifBlank { null },
                            sectionId = selectedSectionId
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank(),
                icon = Icons.Default.Add
            )

            if (selectedSectionId != null) {
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = c.darkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = scheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Procedure guidance will be available for this case", style = MaterialTheme.typography.bodySmall, color = scheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun NewClientDialog(
    onDismiss: () -> Unit,
    onSave: (String, String?, String?, String?, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    val scheme = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Client", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = fatherName, onValueChange = { fatherName = it }, label = { Text("Father's Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = occupation, onValueChange = { occupation = it }, label = { Text("Occupation") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), minLines = 2, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) { onSave(name, phone.ifBlank { null }, email.ifBlank { null }, address.ifBlank { null }, fatherName.ifBlank { null }, occupation.ifBlank { null }) } },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
