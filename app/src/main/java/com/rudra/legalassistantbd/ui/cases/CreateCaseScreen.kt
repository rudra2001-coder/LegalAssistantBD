package com.rudra.legalassistantbd.ui.cases

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*

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
    var courtName by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }
    var expandedSectionMenu by remember { mutableStateOf(false) }
    var selectedSectionId by remember { mutableStateOf<Int?>(null) }
    var selectedSectionTitle by remember { mutableStateOf("None") }
    val types = listOf(Constants.CASE_TYPE_CRIMINAL, Constants.CASE_TYPE_CIVIL, Constants.CASE_TYPE_FAMILY, Constants.CASE_TYPE_LABOUR, Constants.CASE_TYPE_OTHER)
    val sections by viewModel.allSections.collectAsState()

    Scaffold(
        topBar = {
            TopBar(title = "Create Case", onBackClick = { navController.popBackStack() })
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
                text = "New Case",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteSoft,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Case Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedMenu,
                onExpandedChange = { expandedMenu = it }
            ) {
                OutlinedTextField(
                    value = caseType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Case Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                caseType = type
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Link Law Section (Optional)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Gold,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Linking a section will guide you through its legal procedure",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayLight
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSectionMenu,
                            onDismissRequest = { expandedSectionMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None", color = GrayLight) },
                                onClick = {
                                    selectedSectionId = null
                                    selectedSectionTitle = "None"
                                    expandedSectionMenu = false
                                }
                            )
                            sections.forEach { section ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "${section.sectionNumber}. ${section.titleEn}",
                                                color = WhiteSoft,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            if (section.isCustom) {
                                                Text(
                                                    text = "Custom Section",
                                                    color = Gold,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedSectionId = section.id
                                        selectedSectionTitle = "Sec ${section.sectionNumber}: ${section.titleEn}"
                                        expandedSectionMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = opponentName,
                onValueChange = { opponentName = it },
                label = { Text("Opponent Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = courtName,
                onValueChange = { courtName = it },
                label = { Text("Court Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(32.dp))

            GoldButton(
                text = "Create Case",
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.createCase(
                            title = title,
                            caseType = caseType,
                            description = description,
                            clientId = null,
                            opponentName = opponentName.ifBlank { null },
                            courtName = courtName.ifBlank { null },
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
                    colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Procedure guidance will be available for this case",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gold
                        )
                    }
                }
            }
        }
    }
}


