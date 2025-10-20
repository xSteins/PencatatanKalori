package com.ralvin.pencatatankalori.View.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ralvin.pencatatankalori.View.screens.History
// import com.ralvin.pencatatankalori.View.screens.OnboardingScreen // bypass dulu
import com.ralvin.pencatatankalori.View.screens.OverviewScreen
import com.ralvin.pencatatankalori.View.screens.ProfileSettings

@Composable
fun NavGraph(
	navController: NavHostController,
	modifier: Modifier = Modifier,
	startDestination: String
) {
	NavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier
	) {
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