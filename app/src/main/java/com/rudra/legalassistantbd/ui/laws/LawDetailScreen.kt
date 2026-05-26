package com.rudra.legalassistantbd.ui.laws

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*

@Composable
fun LawDetailScreen(
    lawId: Int,
    navController: NavController,
    viewModel: LawViewModel = hiltViewModel()
) {
    LaunchedEffect(lawId) {
        viewModel.loadSections(lawId)
    }

    val law by viewModel.selectedLaw.collectAsState()
    val sections by viewModel.sections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = law?.shortTitle ?: "Law Details",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                law?.let { l ->
                    item {
                        Text(
                            text = l.titleEn,
                            style = MaterialTheme.typography.titleLarge,
                            color = WhiteSoft,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = l.titleBn,
                            style = MaterialTheme.typography.bodyLarge,
                            color = GrayLight
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = l.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayLight
                        )
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = DarkSurface)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Sections (${sections.size})",
                            style = MaterialTheme.typography.titleLarge,
                            color = WhiteSoft,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                if (sections.isEmpty()) {
                    item {
                        Text(
                            text = "No sections loaded",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GrayMedium
                        )
                    }
                } else {
                    items(sections) { section ->
                        SectionCard(
                            sectionNumber = section.sectionNumber,
                            title = section.titleEn,
                            onClick = {
                                navController.navigate(
                                    "section_detail/${section.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
