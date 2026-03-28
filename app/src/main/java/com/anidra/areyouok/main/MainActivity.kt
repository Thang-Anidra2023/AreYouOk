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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.anidra.areyouok.ui.AccountInfoScreen
import com.anidra.areyouok.ui.AccountRoute
import com.anidra.areyouok.ui.AddPersonToWatchScreen
import com.anidra.areyouok.ui.EditAccountRoute
import com.anidra.areyouok.ui.PeopleIWatchScreen
import com.anidra.areyouok.ui.PersonWatchDetailScreen
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
    const val EDIT_ACCOUNT = "edit_account"

    const val PEOPLE_I_WATCH = "people_i_watch"
    const val ADD_PERSON_TO_WATCH = "add_person_to_watch"
    const val PERSON_WATCH_DETAIL = "person_watch_detail/{personId}"

    fun personWatchDetail(personId: String): String = "person_watch_detail/$personId"
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
            composable(Routes.ACCOUNT) { backStackEntry ->
                val refreshOnReturn by backStackEntry
                    .savedStateHandle
                    .getStateFlow("account_updated", false)
                    .collectAsState()

                AccountRoute(
                    onEditProfile = {
                        navController.navigate(Routes.EDIT_ACCOUNT)
                    },
                    refreshOnReturn = refreshOnReturn,
                    onRefreshConsumed = {
                        backStackEntry.savedStateHandle["account_updated"] = false
                    }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsRoute(viewModel = hiltViewModel())
            }

            composable(Routes.EDIT_ACCOUNT) {
                EditAccountRoute(
                    onSaved = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("account_updated", true)

                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.PEOPLE_I_WATCH) {
                PeopleIWatchScreen(
                    onAddClick = { navController.navigate(Routes.ADD_PERSON_TO_WATCH) },
                    onPersonClick = { personId ->
                        navController.navigate(Routes.personWatchDetail(personId))
                    },
                    onResendInviteClick = {
                        // mock only for now
                    }
                )
            }

            composable(Routes.ADD_PERSON_TO_WATCH) {
                AddPersonToWatchScreen(
                    onCancel = { navController.popBackStack() },
                    onSendInvite = { _, _, _ ->
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.PERSON_WATCH_DETAIL,
                arguments = listOf(
                    navArgument("personId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getString("personId").orEmpty()

                PersonWatchDetailScreen(
                    personId = personId,
                    onBack = { navController.popBackStack() },
                    onOpenMapsClick = {
                        // later: open geo intent here
                    }
                )
            }
        }



        if (showMenu) {
            AppHamburgerMenu(
                onCheckIn = { navigateIfNotCurrent(Routes.CHECK_IN) },
                onAccountInfo = { navigateIfNotCurrent(Routes.ACCOUNT) },
                onPeopleIWatch = {
                    navController.navigate(Routes.PEOPLE_I_WATCH)
                },
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