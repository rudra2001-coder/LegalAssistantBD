package com.rudra.legalassistantbd.ui.cases

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.core.util.toFormattedDate
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseListScreen(
    navController: NavController,
    viewModel: CaseViewModel = hiltViewModel()
) {
    val cases by viewModel.cases.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All") + Constants.CASE_STATUSES

    val filteredCases = when (selectedFilter) {
        "All" -> cases
        else -> cases.filter { it.status == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Cases",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { navController.navigate("create_case") }) {
                        Icon(Icons.Default.Add, contentDescription = "New Case", tint = scheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_case") },
                containerColor = scheme.primary,
                contentColor = scheme.background
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Case")
            }
        },
        containerColor = scheme.background
    ) { padding ->
        val isEmpty = filteredCases.isEmpty()
        if (isEmpty && selectedFilter == "All") {
            EmptyState(
                icon = Icons.Outlined.FolderOpen,
                title = "No Cases Yet",
                subtitle = "Create your first case to start tracking",
                modifier = Modifier.padding(padding)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                SectionHeader(
                    title = "My Cases",
                    actionText = "Create",
                    onClick = { navController.navigate("create_case") }
                )
                Spacer(Modifier.height(8.dp))

                ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                    containerColor = scheme.background,
                    contentColor = scheme.primary,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    filters.forEach { filter ->
                        Tab(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            text = {
                                Text(
                                    filter,
                                    fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (isEmpty) {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "No $selectedFilter Cases",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredCases) { case ->
                            CaseCard(
                                case = case,
                                onClick = { navController.navigate("case_detail/${case.id}") }
                            )
                        }
                    }
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
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    val statusColor = when (case.status) {
        "Active" -> c.successGreen
        "Pending" -> c.warningOrange
        "Closed" -> c.grayMedium
        else -> c.infoBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                IconBadge(
                    icon = Icons.Outlined.Gavel,
                    tint = scheme.primary,
                    size = 40,
                    iconSize = 22
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = case.caseNumber,
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.onSurfaceVariant
                        )
                        StatusPill(
                            text = case.status,
                            color = statusColor
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = case.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = case.caseType,
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                        case.courtName?.let {
                            Text(
                                text = "• $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Filed: ${case.filingDate.toFormattedDate()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = c.grayMedium
                        )
                        case.nextHearing?.let {
                            Text(
                                text = "Next: ${it.toFormattedDate()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.primary
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(statusColor)
            )
        }
    }
}
