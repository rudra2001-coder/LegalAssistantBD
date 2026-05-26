package com.rudra.legalassistantbd.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.theme.*
import com.rudra.legalassistantbd.ui.theme.LocalAppColors

// ── Palette (local overrides for decorative elements) ─────────────────────────
private val Blue          = Color(0xFF5B9CF6)
private val Green         = Color(0xFF52E8A0)
private val Orange        = Color(0xFFFF8C42)
private val RedAccent     = Color(0xFFFF5C7A)
private val Purple        = Color(0xFFB57BEE)
private val BorderSubtle  = Color(0x12FFFFFF)

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current

    Scaffold(containerColor = scheme.background) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = scheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Header ──────────────────────────────────────────────────
                DashboardHeader()

                Spacer(Modifier.height(18.dp))

                // ── Hero Band ───────────────────────────────────────────────
                HeroBand(
                    lawCount = state.lawCount,
                    sectionCount = state.sectionCount
                )

                Spacer(Modifier.height(14.dp))

                // ── Stat Cards ──────────────────────────────────────────────
                StatGrid(state = state)

                Spacer(Modifier.height(20.dp))

                // ── Quick Actions ───────────────────────────────────────────
                SectionHeader(title = "Quick Actions", actionText = "See all")

                Spacer(Modifier.height(14.dp))

                QuickActionsGrid(navController = navController)

                Spacer(Modifier.height(20.dp))

                // ── Recent Activity ──────────────────────────────────────────
                SectionHeader(title = "Recent Activity", actionText = "View all")

                Spacer(Modifier.height(14.dp))

                RecentActivityList(items = state.recentItems)

                Spacer(Modifier.height(20.dp))

                // ── Footer ───────────────────────────────────────────────────
                Text(
                    text = "Legal Assistant BD · v1.0 · Bangladesh Law Database",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun DashboardHeader() {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "LEGAL ASSISTANT BD",
                color = scheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = buildAnnotatedStringWithGold("Good morning, Counsel 👋"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = scheme.onSurface
            )
        }

        // Avatar circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(listOf(Purple, Blue))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RK",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// Helper – annotated string coloring "Counsel" in Gold
@Composable
private fun buildAnnotatedStringWithGold(fullText: String): androidx.compose.ui.text.AnnotatedString {
    val scheme = MaterialTheme.colorScheme
    return androidx.compose.ui.text.buildAnnotatedString {
        val parts = fullText.split("Counsel")
        append(parts[0])
        pushStyle(androidx.compose.ui.text.SpanStyle(color = scheme.primary))
        append("Counsel")
        pop()
        if (parts.size > 1) append(parts[1])
    }
}

// ── Hero Band ─────────────────────────────────────────────────────────────────
@Composable
private fun HeroBand(lawCount: Int, sectionCount: Int) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF2A2040), Color(0xFF1E1B35), Color(0xFF251730))
                )
            )
    ) {
        // Decorative background glyph
        Text(
            text = "⚖",
            fontSize = 90.sp,
            color = Color.White.copy(alpha = 0.05f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-8).dp)
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TOTAL COVERAGE",
                color = scheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$lawCount",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = scheme.onSurface,
                    lineHeight = 46.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Laws",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Text(
                text = "across ${sectionCount.toFormattedString()} sections indexed",
                color = scheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(10.dp))
            // Status pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Green.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Green)
                )
                Text(
                    text = "System up to date",
                    color = Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun Int.toFormattedString(): String =
    if (this >= 1000) "${this / 1000}.${(this % 1000) / 100}k" else toString()

// ── Stat Grid ─────────────────────────────────────────────────────────────────
@Composable
private fun StatGrid(state: DashboardState) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                label = "Laws Indexed",
                value = "${state.lawCount}",
                icon = Icons.Outlined.LibraryBooks,
                iconColor = scheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Sections",
                value = state.sectionCount.toFormattedString(),
                icon = Icons.Outlined.Article,
                iconColor = Blue,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                label = "Active Cases",
                value = "${state.activeCases}",
                icon = Icons.Outlined.Gavel,
                iconColor = Green,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Reminders",
                value = "${state.pendingReminders}",
                icon = Icons.Outlined.Notifications,
                iconColor = Orange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = scheme.onSurface,
                lineHeight = 26.sp
            )
            Text(text = label, fontSize = 12.sp, color = scheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, actionText: String) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
        Text(
            text = "$actionText →",
            fontSize = 12.sp,
            color = scheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Quick Actions ─────────────────────────────────────────────────────────────
private data class ActionItem(
    val title: String,
    val description: String,
    val emoji: String,
    val accentColor: Color,
    val route: String,
    val isWide: Boolean = false
)

private val actionItems = listOf(
    ActionItem(
        title = "AI Legal Assistant",
        description = "Ask anything about Bangladeshi law",
        emoji = "🤖",
        accentColor = Purple,
        route = Constants.ROUTE_AI_CHAT,
        isWide = true
    ),
    ActionItem("Law Library",      "Browse all indexed laws",   "📚", Gold,      Constants.ROUTE_LAW_EXPLORER),
    ActionItem("Search",           "Find sections fast",        "🔍", Blue,      Constants.ROUTE_SEARCH),
    ActionItem("Case Files",       "Track active cases",        "⚖️", Green,     Constants.ROUTE_CASES),
    ActionItem("Documents",        "Manage your docs",          "📄", Orange,    Constants.ROUTE_DOCUMENTS),
    ActionItem("PDF Import",       "Convert & index PDFs",      "📑", RedAccent, Constants.ROUTE_PDF_CONVERTER),
    ActionItem("Custom Sections",  "Add private annotations",   "✏️", Gold,      Constants.ROUTE_CUSTOM_SECTION),
    ActionItem("Reminders",        "Upcoming court dates",      "🔔", RedAccent, Constants.ROUTE_REMINDERS),
    ActionItem("Procedures",       "Step-by-step guides",       "🗂️", Blue,      Constants.ROUTE_PROCEDURES.replace("/{sectionId}", "/0")),
    ActionItem("Security",         "PIN & privacy settings",    "🔒", Color(0xFFAACCFF), Constants.ROUTE_SECURITY),
)

@Composable
private fun QuickActionsGrid(navController: NavController) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val wideItem = actionItems.first { it.isWide }
        val gridItems = actionItems.filter { !it.isWide }

        // Wide AI card
        WideActionCard(item = wideItem, onClick = { navController.navigate(wideItem.route) })

        // 2-column grid
        gridItems.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { item ->
                    SmallActionCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(item.route) }
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WideActionCard(item: ActionItem, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Purple.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Purple.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 22.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(item.description, fontSize = 12.sp, color = scheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Purple.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("↗", color = Purple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SmallActionCard(item: ActionItem, modifier: Modifier, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        modifier = modifier
            .aspectRatio(0.95f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 20.sp)
                }
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = scheme.onSurface,
                    lineHeight = 17.sp
                )
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
            // Accent bottom strip
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                    .background(item.accentColor)
            )
        }
    }
}

// ── Recent Activity ───────────────────────────────────────────────────────────
@Composable
private fun RecentActivityList(items: List<RecentItem>) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (items.isEmpty()) {
            Text(
                text = "No recent activity",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            items.forEach { item ->
                RecentItemRow(item = item)
            }
        }
    }
}

@Composable
private fun RecentItemRow(item: RecentItem) {
    val scheme = MaterialTheme.colorScheme
    val c = LocalAppColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = c.darkCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(12.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(item.dotColor)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(item.subtitle, fontSize = 11.sp, color = scheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(item.dotColor.copy(alpha = 0.12f))
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                Text(
                    text = item.badgeLabel,
                    color = item.dotColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}