package com.rudra.legalassistantbd.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.theme.DarkSurface
import com.rudra.legalassistantbd.ui.theme.Gold
import com.rudra.legalassistantbd.ui.theme.GrayLight
import com.rudra.legalassistantbd.ui.theme.GrayMedium

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Constants.ROUTE_DASHBOARD, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Laws", Constants.ROUTE_LAW_EXPLORER, Icons.Filled.LibraryBooks, Icons.Outlined.LibraryBooks),
    BottomNavItem("Cases", Constants.ROUTE_CASES, Icons.Filled.Gavel, Icons.Outlined.Gavel),
    BottomNavItem("AI", Constants.ROUTE_AI_CHAT, Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
    BottomNavItem("All", Constants.ROUTE_ALL_FEATURES, Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
)

val bottomNavRoutes = bottomNavItems.map { it.route }.toSet()

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute in bottomNavRoutes) {
        NavigationBar(
            containerColor = DarkSurface
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(Constants.ROUTE_DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        unselectedIconColor = GrayMedium,
                        unselectedTextColor = GrayLight,
                        indicatorColor = Gold.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}
