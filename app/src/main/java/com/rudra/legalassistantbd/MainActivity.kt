package com.rudra.legalassistantbd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rudra.legalassistantbd.core.database.DataInitializer
import com.rudra.legalassistantbd.core.util.Constants
import com.rudra.legalassistantbd.ui.navigation.BottomNavBar
import com.rudra.legalassistantbd.ui.navigation.NavGraph
import com.rudra.legalassistantbd.ui.onboarding.isOnboardingComplete
import com.rudra.legalassistantbd.ui.theme.LegalAssistantBDTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataInitializer: DataInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            dataInitializer.initializeIfNeeded()
        }

        val onboardingComplete = isOnboardingComplete(this)
        val startDestination = if (onboardingComplete) {
            Constants.ROUTE_DASHBOARD
        } else {
            Constants.ROUTE_ONBOARDING
        }

        setContent {
            LegalAssistantBDTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
