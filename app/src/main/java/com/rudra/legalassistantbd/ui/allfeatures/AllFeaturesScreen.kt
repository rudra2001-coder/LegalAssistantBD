package com.rudra.legalassistantbd.ui.allfeatures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.components.TopBar
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

private data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

private val allFeatures = listOf(
    FeatureItem("Dashboard", Icons.Outlined.Home, Constants.ROUTE_DASHBOARD, Gold),
    FeatureItem("Law Library", Icons.Outlined.LibraryBooks, Constants.ROUTE_LAW_EXPLORER, Gold),
    FeatureItem("Search", Icons.Outlined.Search, Constants.ROUTE_SEARCH, InfoBlue),
    FeatureItem("Cases", Icons.Outlined.Gavel, Constants.ROUTE_CASES, SuccessGreen),
    FeatureItem("Create Case", Icons.Outlined.NoteAdd, Constants.ROUTE_CREATE_CASE, SuccessGreen),
    FeatureItem("AI Assistant", Icons.Outlined.SmartToy, Constants.ROUTE_AI_CHAT, Gold),
    FeatureItem("Documents", Icons.Outlined.Description, Constants.ROUTE_DOCUMENTS, WarningOrange),
    FeatureItem("Reminders", Icons.Outlined.Notifications, Constants.ROUTE_REMINDERS, ErrorRed),
    FeatureItem("Procedures", Icons.Outlined.AccountTree, Constants.ROUTE_PROCEDURES.replace("/{sectionId}", "/0"), InfoBlue),
    FeatureItem("Custom Sections", Icons.Outlined.Task, Constants.ROUTE_CUSTOM_SECTION, Gold),
    FeatureItem("PDF Import", Icons.Outlined.PictureAsPdf, Constants.ROUTE_PDF_CONVERTER, ErrorRed),
    FeatureItem("Security", Icons.Outlined.Lock, Constants.ROUTE_SECURITY, GoldLight),
    FeatureItem("Settings", Icons.Outlined.Settings, Constants.ROUTE_SETTINGS, InfoBlue)
)

@Composable
fun AllFeaturesScreen(navController: NavController) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Scaffold(
        topBar = {
            TopBar(title = "All Features")
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
                text = "All Features",
                style = MaterialTheme.typography.headlineMedium,
                color = scheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${allFeatures.size} features available",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            allFeatures.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { feature ->
                        FeatureGridItem(
                            title = feature.title,
                            icon = feature.icon,
                            color = feature.color,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(feature.route) }
                        )
                    }
                    if (row.size < 3) {
                        repeat(3 - row.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun FeatureGridItem(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
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
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
