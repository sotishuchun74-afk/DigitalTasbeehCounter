package com.tasbeeh.digital.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasbeeh.digital.presentation.counter.CounterScreen
import com.tasbeeh.digital.presentation.counter.CounterViewModel
import com.tasbeeh.digital.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    counterViewModel: CounterViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by counterViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Counter.route
    ) {
        composable(Screen.Counter.route) {
            CounterScreen(
                state = uiState,
                onEvent = counterViewModel::onEvent,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
