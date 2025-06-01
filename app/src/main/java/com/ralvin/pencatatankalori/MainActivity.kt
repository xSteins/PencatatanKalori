package com.ralvin.pencatatankalori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ralvin.pencatatankalori.ui.navigation.BottomNavBar
import com.ralvin.pencatatankalori.ui.navigation.NavGraph
import com.ralvin.pencatatankalori.ui.navigation.Screen
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PencatatanKaloriTheme {
                val navController = rememberNavController()

                val startDestination = Screen.Overview.route

                Scaffold(
                    bottomBar = {
                        BottomNavBar(navController = navController)
                    }
                ) { paddingValues -> 
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}