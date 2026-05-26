package com.rudra.legalassistantbd.ui.customsection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

@Composable
fun CustomSectionScreen(
    navController: NavController,
    viewModel: CustomSectionViewModel = hiltViewModel()
) {
    val customSections by viewModel.customSections.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var editSectionId by remember { mutableStateOf<Int?>(null) }
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    LaunchedEffect(Unit) {
        viewModel.resetForm()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Custom Sections",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        viewModel.resetForm()
                        editSectionId = null
                        showForm = !showForm
                    }) {
                        Icon(
                            if (showForm) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (showForm) "Close" else "Add",
                            tint = scheme.primary
                        )
                    }
                }
            )
        },
        containerColor = scheme.background
    ) { padding ->
        if (showForm) {
            CustomSectionForm(
                viewModel = viewModel,
                saveStatus = saveStatus,
                isSaving = isSaving,
                onBack = {
                    showForm = false
                    viewModel.resetForm()
                    editSectionId = null
                }
            )
        } else {
            if (customSections.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.NoteAdd,
                    title = "No Custom Sections",
                    subtitle = "Tap + to create your own legal sections with step-by-step procedures",
                    modifier = Modifier.padding(padding)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "My Custom Sections (${customSections.size})",
                            style = MaterialTheme.typography.titleLarge,
                            color = scheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    items(customSections) { section ->
                        CustomSectionListItem(
                            section = section,
                            onEdit = {
                                viewModel.loadCustomSectionForEdit(section.id)
                                editSectionId = section.id
                                showForm = true
                            },
                            onDelete = { viewModel.deleteCustomSection(section.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSectionListItem(
    section: LawSectionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section.sectionNumber,
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.titleEn,
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = section.titleBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                    StatusPill(text = "Custom", color = scheme.primary)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = c.infoBlue, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = scheme.error, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(scheme.primary)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = scheme.surface,
            title = { Text("Delete Section", color = scheme.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text("Delete \"${section.titleEn}\" and all its procedures?", color = scheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = scheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = scheme.onSurfaceVariant) }
            }
        )
    }
}

@Composable
fun CustomSectionForm(
    viewModel: CustomSectionViewModel,
    saveStatus: String?,
    isSaving: Boolean,
    onBack: () -> Unit
) {
    val sectionForm by viewModel.sectionForm.collectAsState()
    val procedureSteps by viewModel.procedureSteps.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Section Details",
                style = MaterialTheme.typography.titleLarge,
                color = scheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Define a custom law section with its legal procedure",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = sectionForm.sectionNumber,
                onValueChange = { viewModel.updateSectionField(sectionNumber = it) },
                label = { Text("Section Number *") },
                placeholder = { Text("e.g. 101A") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = sectionForm.titleEn,
                onValueChange = { viewModel.updateSectionField(titleEn = it) },
                label = { Text("Title (English) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = sectionForm.titleBn,
                onValueChange = { viewModel.updateSectionField(titleBn = it) },
                label = { Text("Title (বাংলা)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = sectionForm.contentEn,
                onValueChange = { viewModel.updateSectionField(contentEn = it) },
                label = { Text("Content (English)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = sectionForm.contentBn,
                onValueChange = { viewModel.updateSectionField(contentBn = it) },
                label = { Text("বিষয়বস্তু (বাংলা)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sectionForm.courtType ?: "",
                    onValueChange = { viewModel.updateSectionField(courtType = it.ifBlank { null }) },
                    label = { Text("Court Type") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
                OutlinedTextField(
                    value = sectionForm.bailStatus ?: "",
                    onValueChange = { viewModel.updateSectionField(bailStatus = it.ifBlank { null }) },
                    label = { Text("Bail Status") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
            }
        }

        item {
            OutlinedTextField(
                value = sectionForm.punishment ?: "",
                onValueChange = { viewModel.updateSectionField(punishment = it.ifBlank { null }) },
                label = { Text("Punishment") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = sectionForm.isBailable,
                    onCheckedChange = { viewModel.updateSectionField(isBailable = it) },
                    colors = CheckboxDefaults.colors(checkedColor = scheme.primary)
                )
                Text("Bailable", color = scheme.onSurface, modifier = Modifier.weight(1f))
                Checkbox(
                    checked = sectionForm.isCognizable,
                    onCheckedChange = { viewModel.updateSectionField(isCognizable = it) },
                    colors = CheckboxDefaults.colors(checkedColor = scheme.primary)
                )
                Text("Cognizable", color = scheme.onSurface)
            }
        }

        item {
            HorizontalDivider(color = scheme.surface, modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Procedure Steps",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.addProcedureStep() }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Step", tint = scheme.primary)
                }
            }
        }

        itemsIndexed(procedureSteps) { index, step ->
            ProcedureStepForm(
                index = index,
                step = step,
                onUpdate = { viewModel.updateProcedureStep(index, it) },
                onRemove = { viewModel.removeProcedureStep(index) },
                canRemove = procedureSteps.size > 1
            )
        }

        item {
            Spacer(Modifier.height(16.dp))

            saveStatus?.let { status ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (status.contains("success", true)) c.successGreen.copy(alpha = 0.1f)
                        else if (status.contains("Error", true)) scheme.error.copy(alpha = 0.1f)
                        else scheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = status,
                        color = if (status.contains("Error", true)) scheme.error else c.successGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            scheme.primaryButton(
                text = if (isSaving) "Saving..." else "Save Section & Procedures",
                onClick = {
                    viewModel.saveCustomSection(onSuccess = { onBack() })
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && sectionForm.titleEn.isNotBlank() && sectionForm.sectionNumber.isNotBlank(),
                icon = Icons.Default.Save
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProcedureStepForm(
    index: Int,
    step: CustomProcedureStep,
    onUpdate: (CustomProcedureStep) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = scheme.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Step ${step.stepNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                if (canRemove) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.RemoveCircle, contentDescription = "Remove", tint = scheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = step.titleEn,
                onValueChange = { onUpdate(step.copy(titleEn = it)) },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = step.titleBn,
                onValueChange = { onUpdate(step.copy(titleBn = it)) },
                label = { Text("Title (বাংলা)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = step.descriptionEn,
                onValueChange = { onUpdate(step.copy(descriptionEn = it)) },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors()
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = step.requiredDocuments,
                    onValueChange = { onUpdate(step.copy(requiredDocuments = it)) },
                    label = { Text("Required Docs") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors()
                )
                OutlinedTextField(
                    value = step.duration,
                    onValueChange = { onUpdate(step.copy(duration = it)) },
                    label = { Text("Duration") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors()
                )
            }
        }
    }
}


