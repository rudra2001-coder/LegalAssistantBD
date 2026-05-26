package com.rudra.legalassistantbd.ui.onboarding

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rudra.legalassistantbd.ui.theme.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            icon = Icons.Outlined.Gavel,
            title = "Welcome to Legal Assistant BD",
            subtitle = "Your complete offline legal operating system for Bangladesh. Access laws, sections, procedures, and more — all on your device.",
            color = Gold
        ),
        OnboardingPage(
            icon = Icons.Outlined.PictureAsPdf,
            title = "Import Law Books via PDF",
            subtitle = "Upload Bangladesh law books in PDF format. Our engine extracts sections, structures them, and makes them fully searchable — automatically.",
            color = ErrorRed
        ),
        OnboardingPage(
            icon = Icons.Outlined.Search,
            title = "Full-Text Search in Bengali & English",
            subtitle = "Search across all imported laws in both Bengali (বাংলা) and English. Find relevant sections, procedures, and legal guidance instantly.",
            color = SuccessGreen
        )
    )

    Scaffold(containerColor = DarkBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                markOnboardingComplete(context)
                onComplete()
            }) {
                Text("Skip", color = GrayLight)
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = page.color.copy(alpha = 0.12f),
                    modifier = Modifier.size(140.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            page.icon,
                            contentDescription = null,
                            tint = page.color,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = WhiteSoft,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = GrayLight,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Gold else DarkSurface
                        )
                )
            }
        }

        // Action Button
        Button(
            onClick = {
                if (pagerState.currentPage < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    markOnboardingComplete(context)
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gold),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (pagerState.currentPage < 2) "Next" else "Get Started",
                color = DarkBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(16.dp))
        }
    }
}

private fun markOnboardingComplete(context: Context) {
    context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("completed", true)
        .apply()
}

fun isOnboardingComplete(context: Context): Boolean {
    return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        .getBoolean("completed", false)
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color
)
