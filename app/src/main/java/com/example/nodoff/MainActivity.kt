package com.example.nodoff

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nodoff.ui.screens.DashboardScreen
import com.example.nodoff.ui.screens.HistoryScreen
import com.example.nodoff.ui.screens.OnboardingScreen
import com.example.nodoff.ui.screens.SettingsScreen
import com.example.nodoff.ui.theme.NodOffTheme
import com.example.nodoff.ui.viewmodel.MainViewModel

enum class Screen {
    ONBOARDING,
    DASHBOARD,
    HISTORY,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContent {
            val themePreference by viewModel.themePreference.collectAsState()
            val isSystemDark = isSystemInDarkTheme()
            val darkTheme = when (themePreference) {
                1 -> false // Light
                2 -> true  // Dark
                else -> isSystemDark // System
            }

            AnimatedContent(
                targetState = darkTheme,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "theme_transition"
            ) { isDark ->
                NodOffTheme(darkTheme = isDark) {
                    val context = LocalContext.current
                    
                    fun hasRequiredPermissions(): Boolean {
                        val hasCamera = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        val enabledListeners = Settings.Secure.getString(
                            context.contentResolver,
                            "enabled_notification_listeners"
                        )
                        val hasNotification = enabledListeners != null && enabledListeners.split(":").any {
                            val componentName = android.content.ComponentName.unflattenFromString(it)
                            componentName != null && componentName.packageName == context.packageName
                        }

                        return hasCamera && hasNotification
                    }

                    var currentScreen by remember {
                        mutableStateOf(
                            if (hasRequiredPermissions()) Screen.DASHBOARD else Screen.ONBOARDING
                        )
                    }

                    when (currentScreen) {
                        Screen.ONBOARDING -> {
                            OnboardingScreen(
                                onNavigateToDashboard = { currentScreen = Screen.DASHBOARD }
                            )
                        }
                        Screen.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        0 -> currentScreen = Screen.DASHBOARD
                                        1 -> currentScreen = Screen.HISTORY
                                        2 -> currentScreen = Screen.SETTINGS
                                    }
                                }
                            )
                        }
                        Screen.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        0 -> currentScreen = Screen.DASHBOARD
                                        1 -> currentScreen = Screen.HISTORY
                                        2 -> currentScreen = Screen.SETTINGS
                                    }
                                }
                            )
                        }
                        Screen.SETTINGS -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        0 -> currentScreen = Screen.DASHBOARD
                                        1 -> currentScreen = Screen.HISTORY
                                        2 -> currentScreen = Screen.SETTINGS
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}