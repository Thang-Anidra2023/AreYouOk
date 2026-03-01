package com.anidra.areyouok.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anidra.areyouok.components.AppHamburgerMenu
import com.anidra.areyouok.ui.CheckInScreen
import com.anidra.areyouok.ui.ForgotPasswordScreen
import com.anidra.areyouok.ui.LoginScreen
import com.anidra.areyouok.ui.NotificationsScreen
import com.anidra.areyouok.ui.RegisterScreen
import com.anidra.areyouok.ui.screens.AccountInfoScreen
import com.anidra.areyouok.ui.screens.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation()
            }
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val CHECK_IN = "check_in"

    const val ACCOUNT = "account"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    val showMenu = route !in setOf(Routes.LOGIN, Routes.REGISTER, Routes.FORGOT_PASSWORD)

    fun navigateIfNotCurrent(target: String) {
        if (route == target) return
        navController.navigate(target) {
            launchSingleTop = true
        }
    }

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLogin = { email, password ->
                        // TODO: validate / call ViewModel / auth first
                        navController.navigate(Routes.CHECK_IN) {
                            // optional: remove login from back stack
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onForgotPassword = {
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    },
                    onSignUp = {
                        navController.navigate(Routes.REGISTER) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onLoginClick = { navController.popBackStack() }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.CHECK_IN) {
                CheckInScreen()
            }

            composable(Routes.ACCOUNT) { AccountInfoScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(Routes.NOTIFICATIONS) { NotificationsScreen() }
        }

        if (showMenu) {
            AppHamburgerMenu(
                onCheckIn = { navigateIfNotCurrent(Routes.CHECK_IN) },
                onAccountInfo = { navigateIfNotCurrent(Routes.ACCOUNT) },
                onSettings = { navigateIfNotCurrent(Routes.SETTINGS) },
                onNotifications = { navigateIfNotCurrent(Routes.NOTIFICATIONS) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

