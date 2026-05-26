package com.rudra.legalassistantbd.ui.laws

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

@Composable
fun SectionDetailScreen(
    sectionId: Int,
    navController: NavController,
    viewModel: LawViewModel = hiltViewModel()
) {
    LaunchedEffect(sectionId) {
        viewModel.loadSectionById(sectionId)
    }

    val section by viewModel.selectedSection.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    Scaffold(
        topBar = {
            TopBar(
                title = "Section ${section?.sectionNumber ?: ""}",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = scheme.background
    ) { padding ->
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            section?.let { sec ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = c.darkCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Section ${sec.sectionNumber}",
                                style = MaterialTheme.typography.titleLarge,
                                color = scheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = sec.titleEn,
                                style = MaterialTheme.typography.titleMedium,
                                color = scheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = sec.titleBn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = c.darkCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Content (English)",
                                style = MaterialTheme.typography.titleSmall,
                                color = scheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = sec.contentEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = c.darkCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "বিষয়বস্তু (বাংলা)",
                                style = MaterialTheme.typography.titleSmall,
                                color = scheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = sec.contentBn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurface
                            )
                        }
                    }

                    if (sec.punishment != null || sec.courtType != null || sec.bailStatus != null) {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = c.darkCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Legal Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = scheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                                sec.courtType?.let {
                                    DetailRow("Court Type", it)
                                }
                                sec.bailStatus?.let {
                                    DetailRow("Bail Status", it)
                                }
                                sec.punishment?.let {
                                    Spacer(Modifier.height(8.dp))
                                    DetailRow("Punishment", it)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    scheme.primaryButton(
                        text = "View Legal Procedure",
                        onClick = {
                            navController.navigate("procedures/${sectionId}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.AccountTree
                    )
                }
            } ?: run {
                EmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "Section not found"
                )
            }
        }
    }
}


