package com.rudra.legalassistantbd.ui.cases

import androidx.compose.foundation.clickable
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
import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import com.rudra.legalassistantbd.core.util.toFormattedDate
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*

@Composable
fun CaseListScreen(
    navController: NavController,
    viewModel: CaseViewModel = hiltViewModel()
) {
    val cases by viewModel.cases.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Cases",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { navController.navigate("create_case") }) {
                        Icon(Icons.Default.Add, contentDescription = "New Case", tint = Gold)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_case") },
                containerColor = Gold,
                contentColor = DarkBackground
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Case")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        if (cases.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.FolderOpen,
                title = "No Cases Yet",
                subtitle = "Create your first case to start tracking",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cases) { case ->
                    CaseCard(
                        case = case,
                        onClick = {
                            navController.navigate("case_detail/${case.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CaseCard(
    case: CaseEntity,
    onClick: () -> Unit
) {
    val statusColor = when (case.status) {
        "Active" -> SuccessGreen
        "Pending" -> WarningOrange
        "Closed" -> GrayMedium
        else -> InfoBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = case.caseNumber,
                    style = MaterialTheme.typography.labelMedium,
                    color = GrayLight
                )
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = case.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = case.title,
                style = MaterialTheme.typography.titleMedium,
                color = WhiteSoft,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = case.caseType,
                style = MaterialTheme.typography.bodySmall,
                color = GrayLight
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Filed: ${case.filingDate.toFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayMedium
                )
                case.nextHearing?.let {
                    Text(
                        text = "Next: ${it.toFormattedDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold
                    )
                }
            }
        }
    }
}
