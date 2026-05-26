package com.rudra.legalassistantbd.ui.laws

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
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.util.Constants.ROUTE_LAW_DETAIL
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawExplorerScreen(
    navController: NavController,
    viewModel: LawViewModel = hiltViewModel()
) {
    val laws by viewModel.laws.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    Scaffold(
        topBar = {
            TopBar(title = "Law Library", onBackClick = { navController.popBackStack() })
        },
        containerColor = scheme.background
    ) { padding ->
        if (laws.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.LibraryBooks,
                title = "No Laws Loaded",
                subtitle = "Import law books via PDF or JSON to get started",
                modifier = Modifier.padding(padding)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                SectionHeader(
                    title = "Law Library",
                    actionText = "Search",
                    onClick = { navController.navigate("search") }
                )
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(laws) { law ->
                        LawCard(
                            law = law,
                            onClick = {
                                navController.navigate(
                                    ROUTE_LAW_DETAIL.replace("{lawId}", "${law.id}")
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LawCard(
    law: LawEntity,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    icon = Icons.Outlined.Description,
                    tint = scheme.primary,
                    size = 40,
                    iconSize = 22
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = law.shortTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = law.titleEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${law.year} | Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = c.successGreen
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = c.grayMedium
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(scheme.primary)
            )
        }
    }
}
