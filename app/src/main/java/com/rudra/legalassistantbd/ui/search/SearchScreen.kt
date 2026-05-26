package com.rudra.legalassistantbd.ui.search

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
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    Scaffold(
        topBar = {
            TopBar(title = "Search", onBackClick = { navController.popBackStack() })
        },
        containerColor = scheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconBadge(
                    icon = Icons.Default.Search,
                    tint = scheme.primary,
                    size = 40,
                    iconSize = 22
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search laws, sections, keywords...", color = c.grayMedium) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = scheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (state.query.isNotBlank()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = scheme.onSurfaceVariant)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors()
                )
            }

            Spacer(Modifier.height(16.dp))

            if (state.isSearching) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = scheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (state.hasSearched && state.results.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.SearchOff,
                    title = "No results found",
                    subtitle = "Try different keywords or search in Bengali"
                )
            } else {
                Column {
                    SectionHeader(
                        title = "Results (${state.results.size} found)"
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.results) { section ->
                            SectionCard(
                                sectionNumber = section.sectionNumber,
                                title = section.titleEn,
                                onClick = {
                                    navController.navigate("section_detail/${section.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
