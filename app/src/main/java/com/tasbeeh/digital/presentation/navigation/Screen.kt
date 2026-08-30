package com.tasbeeh.digital.presentation.navigation

sealed class Screen(val route: String) {
    data object Counter : Screen("counter_screen")
    data object Settings : Screen("settings_screen")
}
