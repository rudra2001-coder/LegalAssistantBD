package com.rudra.legalassistantbd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rudra.legalassistantbd.core.database.DataInitializer
import com.rudra.legalassistantbd.ui.navigation.NavGraph
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

        setContent {
            LegalAssistantBDTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
