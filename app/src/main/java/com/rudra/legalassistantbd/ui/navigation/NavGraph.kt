package com.rudra.legalassistantbd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rudra.legalassistantbd.core.util.Constants.*
import com.rudra.legalassistantbd.ui.ai.AIChatScreen
import com.rudra.legalassistantbd.ui.cases.CaseDetailScreen
import com.rudra.legalassistantbd.ui.cases.CaseListScreen
import com.rudra.legalassistantbd.ui.cases.CreateCaseScreen
import com.rudra.legalassistantbd.ui.customsection.CustomSectionScreen
import com.rudra.legalassistantbd.ui.dashboard.DashboardScreen
import com.rudra.legalassistantbd.ui.documents.DocumentGeneratorScreen
import com.rudra.legalassistantbd.ui.laws.LawDetailScreen
import com.rudra.legalassistantbd.ui.laws.LawExplorerScreen
import com.rudra.legalassistantbd.ui.laws.SectionDetailScreen
import com.rudra.legalassistantbd.ui.pdf.PdfConverterScreen
import com.rudra.legalassistantbd.ui.procedures.ProcedureScreen
import com.rudra.legalassistantbd.ui.reminders.ReminderScreen
import com.rudra.legalassistantbd.ui.search.SearchScreen
import com.rudra.legalassistantbd.ui.security.SecurityScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_DASHBOARD
    ) {
        composable(ROUTE_DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        composable(ROUTE_LAW_EXPLORER) {
            LawExplorerScreen(navController = navController)
        }
        composable(
            route = ROUTE_LAW_DETAIL,
            arguments = listOf(navArgument("lawId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lawId = backStackEntry.arguments?.getInt("lawId") ?: return@composable
            LawDetailScreen(lawId = lawId, navController = navController)
        }
        composable(
            route = ROUTE_SECTION_DETAIL,
            arguments = listOf(navArgument("sectionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getInt("sectionId") ?: return@composable
            SectionDetailScreen(sectionId = sectionId, navController = navController)
        }
        composable(ROUTE_SEARCH) {
            SearchScreen(navController = navController)
        }
        composable(ROUTE_CASES) {
            CaseListScreen(navController = navController)
        }
        composable(
            route = ROUTE_CASE_DETAIL,
            arguments = listOf(navArgument("caseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getInt("caseId") ?: return@composable
            CaseDetailScreen(caseId = caseId, navController = navController)
        }
        composable(ROUTE_CREATE_CASE) {
            CreateCaseScreen(navController = navController)
        }
        composable(
            route = ROUTE_PROCEDURES,
            arguments = listOf(navArgument("sectionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getInt("sectionId") ?: return@composable
            ProcedureScreen(sectionId = sectionId, navController = navController)
        }
        composable(ROUTE_AI_CHAT) {
            AIChatScreen(navController = navController)
        }
        composable(ROUTE_DOCUMENTS) {
            DocumentGeneratorScreen(navController = navController)
        }
        composable(ROUTE_REMINDERS) {
            ReminderScreen(navController = navController)
        }
        composable(ROUTE_PDF_CONVERTER) {
            PdfConverterScreen(navController = navController)
        }
        composable(ROUTE_SECURITY) {
            SecurityScreen(navController = navController)
        }
        composable(ROUTE_CUSTOM_SECTION) {
            CustomSectionScreen(navController = navController)
        }
    }
}
