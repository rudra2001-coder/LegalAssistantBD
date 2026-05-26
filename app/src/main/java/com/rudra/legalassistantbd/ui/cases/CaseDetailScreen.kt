package com.rudra.legalassistantbd.ui.cases

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.database.entity.CaseProcedureProgressEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import com.rudra.legalassistantbd.core.util.Constants.ROUTE_PROCEDURE_GUIDANCE
import com.rudra.legalassistantbd.core.util.toFormattedDate
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*

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
    val isLoading by viewModel.isLoading.collectAsState()
    val linkedSection by viewModel.linkedSection.collectAsState()
    val sectionProcedures by viewModel.sectionProcedures.collectAsState()
    val procedureProgress by viewModel.procedureProgress.collectAsState()
    val progressSummary by viewModel.progressSummary.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = case?.title ?: "Case Details",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            case?.let { c ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = c.caseNumber,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GrayLight
                                )
                                Surface(
                                    color = when(c.status) {
                                        "Active" -> SuccessGreen
                                        "Pending" -> WarningOrange
                                        else -> GrayMedium
                                    }.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = c.status,
                                        color = when(c.status) {
                                            "Active" -> SuccessGreen
                                            "Pending" -> WarningOrange
                                            else -> GrayMedium
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = c.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = WhiteSoft,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = c.caseType,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Case Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            c.courtName?.let { DetailRow("Court", it) }
                            c.judgeName?.let { DetailRow("Judge", it) }
                            c.opponentName?.let { DetailRow("Opponent", it) }
                            DetailRow("Filing Date", c.filingDate.toFormattedDate())
                            c.nextHearing?.let { DetailRow("Next Hearing", it.toFormattedDate()) }
                            if (linkedSection != null) {
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(color = DarkSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                DetailRow("Linked Section", "Sec ${linkedSection!!.sectionNumber}: ${linkedSection!!.titleEn}")
                                DetailRow("Procedure Progress", "${progressSummary.first}/${progressSummary.second}")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = c.description ?: "No description",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WhiteSoft
                            )
                        }
                    }

                    if (linkedSection != null && sectionProcedures.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Procedure Guidance",
                            style = MaterialTheme.typography.titleLarge,
                            color = WhiteSoft,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = linkedSection!!.titleEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = Gold
                        )
                        Spacer(Modifier.height(12.dp))

                        sectionProcedures.forEach { procedure ->
                            val progress = procedureProgress.find { it.procedureId == procedure.id }
                            ProcedureGuidanceStep(
                                procedure = procedure,
                                isCompleted = progress?.isCompleted == true,
                                stepNumber = sectionProcedures.indexOf(procedure) + 1,
                                onToggle = {
                                    if (progress?.isCompleted == true) {
                                        viewModel.markProcedureIncomplete(caseId, procedure.id)
                                    } else {
                                        viewModel.markProcedureCompleted(caseId, procedure.id)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Evidence (${evidence.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = WhiteSoft,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    if (evidence.isEmpty()) {
                        Text(
                            text = "No evidence added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMedium
                        )
                    } else {
                        evidence.forEach { ev ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Description,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(ev.title, color = WhiteSoft, fontWeight = FontWeight.Medium)
                                        ev.description?.let {
                                            Text(it, color = GrayLight, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GoldButton(
                            text = "Mark Closed",
                            onClick = { viewModel.updateCaseStatus(caseId, "Closed") },
                            modifier = Modifier.weight(1f)
                        )
                        GoldButton(
                            text = "Delete",
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        )
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
    Card(
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SuccessGreen.copy(alpha = 0.08f) else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                if (isCompleted) Icons.Default.CheckCircleOutline else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) SuccessGreen else GrayMedium,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Gold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Step $stepNumber",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (isCompleted) {
                        Spacer(Modifier.width(8.dp))
                        Text("Completed", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = procedure.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    color = WhiteSoft,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = procedure.titleBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayLight
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = procedure.descriptionEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayLight
                )
                procedure.requiredDocuments?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Docs: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningOrange
                    )
                }
                procedure.duration?.let {
                    Text(
                        text = "Time: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = InfoBlue
                    )
                }
            }
        }
    }
}
