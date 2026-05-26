package com.rudra.legalassistantbd.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopBar(title = "Legal Assistant BD")
        },
        containerColor = DarkBackground
    ) { padding ->
        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WhiteSoft,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Laws",
                        value = "${state.lawCount}",
                        icon = Icons.Outlined.LibraryBooks,
                        modifier = Modifier.weight(1f),
                        color = Gold
                    )
                    StatCard(
                        title = "Sections",
                        value = "${state.sectionCount}",
                        icon = Icons.Outlined.Article,
                        modifier = Modifier.weight(1f),
                        color = InfoBlue
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Cases",
                        value = "${state.activeCases}",
                        icon = Icons.Outlined.Gavel,
                        modifier = Modifier.weight(1f),
                        color = SuccessGreen
                    )
                    StatCard(
                        title = "Reminders",
                        value = "${state.pendingReminders}",
                        icon = Icons.Outlined.Notifications,
                        modifier = Modifier.weight(1f),
                        color = WarningOrange
                    )
                }

                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    color = WhiteSoft,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                QuickActionGrid(navController = navController)

                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Legal Assistant BD v1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

private val quickActions = listOf(
    QuickAction("Law Library", Icons.Outlined.LibraryBooks, Constants.ROUTE_LAW_EXPLORER, Gold),
    QuickAction("Search", Icons.Outlined.Search, Constants.ROUTE_SEARCH, InfoBlue),
    QuickAction("Cases", Icons.Outlined.Gavel, Constants.ROUTE_CASES, SuccessGreen),
    QuickAction("Custom Sections", Icons.Outlined.NoteAdd, Constants.ROUTE_CUSTOM_SECTION, Gold),
    QuickAction("AI Assistant", Icons.Outlined.SmartToy, Constants.ROUTE_AI_CHAT, Gold),
    QuickAction("Documents", Icons.Outlined.Description, Constants.ROUTE_DOCUMENTS, WarningOrange),
    QuickAction("Reminders", Icons.Outlined.Notifications, Constants.ROUTE_REMINDERS, ErrorRed),
    QuickAction("Procedures", Icons.Outlined.AccountTree, Constants.ROUTE_PROCEDURES.replace("/{sectionId}", "/0"), InfoBlue),
    QuickAction("PDF Import", Icons.Outlined.PictureAsPdf, Constants.ROUTE_PDF_CONVERTER, ErrorRed),
    QuickAction("Security", Icons.Outlined.Lock, Constants.ROUTE_SECURITY, GoldLight)
)

@Composable
fun QuickActionGrid(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        quickActions.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { action ->
                    QuickActionItem(
                        title = action.title,
                        icon = action.icon,
                        color = action.color,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(action.route)
                        }
                    )
                }
                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = GrayLight,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
