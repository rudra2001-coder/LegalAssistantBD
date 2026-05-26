package com.rudra.legalassistantbd.ui.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rudra.legalassistantbd.core.database.entity.ReminderEntity
import com.rudra.legalassistantbd.core.util.toFormattedDateTime
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Reminders",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder", tint = Gold)
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (reminders.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.NotificationsNone,
                title = "No Reminders",
                subtitle = "Add reminders for hearings, deadlines, and submissions",
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
                items(reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onComplete = { viewModel.markCompleted(reminder.id) },
                        onDelete = { viewModel.deleteReminder(reminder.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddReminderDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, desc, time, type ->
                viewModel.addReminder(title, desc, time, type, null)
                showDialog = false
            }
        )
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderEntity,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val typeColor = when (reminder.reminderType) {
        "Hearing" -> Gold
        "Deadline" -> ErrorRed
        "Document Submission" -> InfoBlue
        else -> GrayLight
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = typeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = reminder.reminderType,
                            style = MaterialTheme.typography.labelSmall,
                            color = typeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (reminder.isCompleted) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = WhiteSoft,
                    fontWeight = FontWeight.SemiBold
                )
                reminder.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayLight
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = reminder.dueTimestamp.toFormattedDateTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayMedium
                )
            }
            Column {
                if (!reminder.isCompleted) {
                    IconButton(onClick = onComplete) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = "Complete", tint = SuccessGreen)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = ErrorRed)
                }
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reminderType by remember { mutableStateOf("Hearing") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis() + 86400000) }

    val types = listOf("Hearing", "Deadline", "Document Submission", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Add Reminder", color = WhiteSoft, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors()
                )
                Spacer(Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = reminderType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { reminderType = type; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, description, selectedDate, reminderType) },
                enabled = title.isNotBlank()
            ) { Text("Add", color = Gold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = GrayLight) }
        }
    )
}
