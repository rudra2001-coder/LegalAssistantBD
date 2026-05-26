package com.rudra.legalassistantbd.ui.laws

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
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.util.Constants.ROUTE_LAW_DETAIL
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawExplorerScreen(
    navController: NavController,
    viewModel: LawViewModel = hiltViewModel()
) {
    val laws by viewModel.laws.collectAsState()

    Scaffold(
        topBar = {
            TopBar(title = "Law Library", onBackClick = { navController.popBackStack() })
        },
        containerColor = DarkBackground
    ) { padding ->
        if (laws.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.LibraryBooks,
                title = "No Laws Loaded",
                subtitle = "Import law books via PDF or JSON to get started",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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

@Composable
fun LawCard(
    law: LawEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(36.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = law.shortTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = WhiteSoft,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = law.titleEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayLight
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${law.year} | Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GrayMedium
            )
        }
    }
}
