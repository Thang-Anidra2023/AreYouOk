package com.anidra.areyouok.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import com.anidra.areyouok.components.AppHamburgerMenu
import com.anidra.areyouok.ui.CheckInScreen
import com.anidra.areyouok.ui.ForgotPasswordScreen
import com.anidra.areyouok.ui.LoginScreen
import com.anidra.areyouok.ui.RegisterScreen
import com.anidra.areyouok.permissions.RequestNotificationPermissionOnce
import com.anidra.areyouok.ui.SettingsRoute

import com.anidra.areyouok.viewmodel.SessionViewModel
import com.anidra.areyouok.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import com.anidra.areyouok.ui.AccountInfoScreen
import com.anidra.areyouok.ui.AccountRoute
import com.google.firebase.FirebaseApp



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = FirebaseApp.getInstance()
        Log.d("FirebaseTest", "name=${app.name}, appId=${app.options.applicationId}, projectId=${app.options.projectId}")

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            RequestNotificationPermissionOnce()
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
}

@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val sessionState by sessionViewModel.uiState.collectAsState()

    LaunchedEffect(sessionState.isLoggedIn) {
        if (sessionState.isLoggedIn) {
            settingsViewModel.reconcileReminderSchedule()
        }
    }

    if (sessionState.loading) {
        Box(
            modifier = Modifier.fillMaxSize().wrapContentSize()
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    val startRoute = if (sessionState.isLoggedIn) Routes.CHECK_IN else Routes.LOGIN

    val showMenu = sessionState.isLoggedIn &&
            !sessionState.loggingOut &&
            route != null &&
            route !in setOf(Routes.LOGIN, Routes.REGISTER, Routes.FORGOT_PASSWORD)

    fun navigateIfNotCurrent(target: String) {
        if (route == target) return
        navController.navigate(target) {
            launchSingleTop = true
        }
    }

    LaunchedEffect(sessionState.isLoggedIn, sessionState.loggingOut, route) {
        if (!sessionState.loggingOut &&
            !sessionState.isLoggedIn &&
            route != null &&
            route !in setOf(Routes.LOGIN, Routes.REGISTER, Routes.FORGOT_PASSWORD)
        ) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startRoute
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.CHECK_IN) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
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
                    onRegisterSuccess = {
                        navController.popBackStack()
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.CHECK_IN) { CheckInScreen() }
            composable(Routes.ACCOUNT) { AccountRoute() }
            composable(Routes.SETTINGS) {
                SettingsRoute(viewModel = hiltViewModel())
            }
        }

        if (showMenu) {
            AppHamburgerMenu(
                onCheckIn = { navigateIfNotCurrent(Routes.CHECK_IN) },
                onAccountInfo = { navigateIfNotCurrent(Routes.ACCOUNT) },
                onSettings = { navigateIfNotCurrent(Routes.SETTINGS) },
                onLogout = { sessionViewModel.logout() }
            )
        }

        if (sessionState.loggingOut) {
            Box(
                modifier = Modifier.fillMaxSize().wrapContentSize()
            ) {
                CircularProgressIndicator()
            }
        }
    }
}