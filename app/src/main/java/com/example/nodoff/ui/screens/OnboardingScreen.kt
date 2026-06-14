package com.example.nodoff.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.nodoff.R
import com.example.nodoff.ui.components.NodOffButton
import com.example.nodoff.ui.components.NodOffCard
import com.example.nodoff.ui.components.ProtocolItem
import com.example.nodoff.ui.theme.*

private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

private fun isNotificationListenerEnabled(context: Context): Boolean {
    val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    val packageName = context.packageName
    return enabledListeners != null && enabledListeners.split(":").any {
        val componentName = android.content.ComponentName.unflattenFromString(it)
        componentName != null && componentName.packageName == packageName
    }
}

@Composable
fun OnboardingScreen(onNavigateToDashboard: () -> Unit) {
    val context = LocalContext.current
    var isCameraGranted by remember { mutableStateOf(hasCameraPermission(context)) }
    var isNotificationGranted by remember { mutableStateOf(isNotificationListenerEnabled(context)) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isCameraGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isCameraGranted = hasCameraPermission(context)
                isNotificationGranted = isNotificationListenerEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 64.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nodoff_logo),
                contentDescription = "NodOff Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NodOff",
                color = OffWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }

        // Protocols List
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "REQUIRED PROTOCOLS",
                color = LowContrastGrey,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            NodOffCard {
                ProtocolItem(
                    icon = Icons.Default.Videocam,
                    title = "Camera Access",
                    subtitle = "VISION SYSTEM",
                    isGranted = isCameraGranted,
                    onGrantClick = {
                        cameraLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
                Divider(color = Color(0xFF2A2A2A), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                ProtocolItem(
                    icon = Icons.Default.Security,
                    title = "Notification Listener",
                    subtitle = "ALERT SYSTEM / MEDIA CONTROL",
                    isGranted = isNotificationGranted,
                    onGrantClick = {
                        try {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                )
            }
        }

        NodOffButton(
            text = "Initialize System",
            onClick = {
                if (isCameraGranted && isNotificationGranted) {
                    onNavigateToDashboard()
                } else {
                    Toast.makeText(
                        context,
                        "Please grant all required permissions to initialize system.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
