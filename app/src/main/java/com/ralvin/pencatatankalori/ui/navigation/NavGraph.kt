package com.ralvin.pencatatankalori.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ralvin.pencatatankalori.ui.screens.History
// import com.ralvin.pencatatankalori.ui.screens.OnboardingScreen // bypass dulu
import com.ralvin.pencatatankalori.ui.screens.OverviewScreen
import com.ralvin.pencatatankalori.ui.screens.ProfileSettings

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String 
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier // Diterapkan ke NavHost
    ) {
        // TODO: FIX ONBOARDING MODAL SUPAYA POP UP JIKA USERDATA BELUM ADA
        // composable(Screen.Onboarding.route) { // bypass dulu
        //     OnboardingScreen(onOnboardingComplete = { /* bypass dulu */ })
        // }
        composable(Screen.Overview.route) {
            OverviewScreen()
        }
        composable(Screen.History.route) {
            History()
        }
        composable(Screen.ProfileSettings.route) {
            ProfileSettings()
        }
    }
} 