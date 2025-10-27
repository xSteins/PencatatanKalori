package com.ralvin.pencatatankalori.view.navigation

sealed class Screen(val route: String) {
	object Onboarding : Screen("onboarding")
	object Overview : Screen("overview")
	object History : Screen("history")
	object ProfileSettings : Screen("profile")
} 