package com.ralvin.pencatatankalori.View.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ralvin.pencatatankalori.R

@Composable
fun BottomNavBar(
	navController: NavController
) {
	val navItems = listOf(
		NavItem(
			name = stringResource(R.string.overview),
			route = Screen.Overview.route,
			icon = Icons.Default.Home
		),
		NavItem(
			name = stringResource(R.string.history),
			route = Screen.History.route,
			icon = Icons.Default.History
		),
		NavItem(
			name = stringResource(R.string.profile),
			route = Screen.ProfileSettings.route,
			icon = Icons.Default.Person
		)
	)

	NavigationBar {
		val navBackStackEntry by navController.currentBackStackEntryAsState()
		val currentRoute = navBackStackEntry?.destination?.route

		navItems.forEach { item ->
			NavigationBarItem(
				icon = { Icon(item.icon, contentDescription = item.name) },
				label = { Text(item.name) },
				selected = currentRoute == item.route,
				onClick = {
					navController.navigate(item.route) {
						popUpTo(navController.graph.startDestinationId) {
							saveState = true
						}
						launchSingleTop = true
						restoreState = true
					}
				}
			)
		}
	}
}
