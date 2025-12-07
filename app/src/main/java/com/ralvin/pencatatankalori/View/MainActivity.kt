package com.ralvin.pencatatankalori.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.ralvin.pencatatankalori.view.components.Onboarding.InitialOnboardingBottomSheet
import com.ralvin.pencatatankalori.view.components.Onboarding.OnboardingDialog
import com.ralvin.pencatatankalori.view.navigation.BottomNavBar
import com.ralvin.pencatatankalori.view.navigation.NavGraph
import com.ralvin.pencatatankalori.view.navigation.Screen
import com.ralvin.pencatatankalori.view.theme.PencatatanKaloriTheme
import com.ralvin.pencatatankalori.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			PencatatanKaloriTheme {
				val navController = rememberNavController()

				val profileViewModel: ProfileViewModel = hiltViewModel()

				val shouldShowBottomSheet by profileViewModel.repository.shouldShowInitialBottomSheet.collectAsStateWithLifecycle(
					initialValue = false
				)

				var showOnboardingDialog by remember { mutableStateOf(false) }

				LaunchedEffect(Unit) {
					profileViewModel.initializeOnboardingState()
				}

				val startDestination = Screen.Overview.route

				Scaffold(
					bottomBar = {
						BottomNavBar(navController = navController)
					}
				) { paddingValues ->
					NavGraph(
						navController = navController,
						modifier = Modifier.Companion.padding(paddingValues),
						startDestination = startDestination
					)
				}

				if (shouldShowBottomSheet) {
					InitialOnboardingBottomSheet(
						onDismiss = {
							profileViewModel.dismissInitialBottomSheet()
						},
						onSkip = {
							profileViewModel.dismissInitialBottomSheet()
						},
						onFillData = {
							showOnboardingDialog = true
						}
					)
				}

				if (showOnboardingDialog) {
					OnboardingDialog(
						onDismiss = {
							showOnboardingDialog = false
							profileViewModel.dismissInitialBottomSheet()
						},
						onboardingViewModel = hiltViewModel()
					)
				}
			}
		}
	}
}