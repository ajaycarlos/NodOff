package com.example.nodoff.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.example.nodoff.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nodoff.ui.components.ActionToggle
import com.example.nodoff.ui.components.BottomNavBar
import com.example.nodoff.ui.components.NodOffCard
import com.example.nodoff.ui.theme.*
import com.example.nodoff.ui.viewmodel.MainViewModel
import com.example.nodoff.camera.EyeStatus
import kotlinx.coroutines.launch
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.nodoff.service.NodOffDeviceAdminReceiver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun DrawerItem(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = OffWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel, onNavigate: (Int) -> Unit) {
    val context = LocalContext.current
    val isMonitoring by viewModel.isMonitoring.collectAsState()
    val eyeTrackingState by viewModel.eyeTrackingState.collectAsState()
    val pauseMedia by viewModel.pauseMedia.collectAsState()
    val turnOffScreen by viewModel.turnOffScreen.collectAsState()
    val disconnectBluetooth by viewModel.disconnectBluetooth.collectAsState()

    var isDeviceAdminGranted by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                val adminComponent = ComponentName(context, NodOffDeviceAdminReceiver::class.java)
                isDeviceAdminGranted = dpm?.isAdminActive(adminComponent) == true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showBugDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    // New state: show the Screen Lock Permission educational dialog
    var showScreenLockDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // ── Screen Lock Permission Educational Dialog ──────────────────────────────
    if (showScreenLockDialog) {
        AlertDialog(
            onDismissRequest = { showScreenLockDialog = false },
            title = {
                Text(
                    text = "Screen Lock Permission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhite,
                    letterSpacing = 1.sp
                )
            },
            text = {
                Text(
                    text = "NodOff requires Device Admin rights strictly to use the screen-lock feature. We do not access or modify any other administrative data.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LowContrastGrey
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showScreenLockDialog = false
                        // Only now launch the OS Device Admin intent
                        val adminComponent = ComponentName(context, NodOffDeviceAdminReceiver::class.java)
                        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                            putExtra(
                                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                "Required to turn off and lock the screen when sleep is detected."
                            )
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrushedCopper)
                ) {
                    Text(
                        text = "Proceed",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showScreenLockDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = LowContrastGrey)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            },
            containerColor = Color(0xEE1A1D1E),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
        )
    }

    // ── About Dialog ──────────────────────────────────────────────────────────
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(text = "About NodOff", color = OffWhite) },
            text = { Text(text = "NodOff is a premium background sleep monitoring tool designed to assist with restorative sleep by managing active device operations.", color = LowContrastGrey) },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(text = "OK", color = BrushedCopper)
                }
            },
            containerColor = Color(0xFF151819),
            titleContentColor = OffWhite,
            textContentColor = OffWhite,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
        )
    }

    // ── Privacy Dialog ────────────────────────────────────────────────────────
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(text = "Privacy Policy", color = OffWhite) },
            text = { Text(text = "NodOff operates 100% offline. Face classification and eye tracking are performed entirely on-device using local machine learning models. Your camera feed, video, and personal data are never recorded, saved, or transmitted to any server.", color = LowContrastGrey) },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(text = "OK", color = BrushedCopper)
                }
            },
            containerColor = Color(0xFF151819),
            titleContentColor = OffWhite,
            textContentColor = OffWhite,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
        )
    }

    // ── Bug Dialog ────────────────────────────────────────────────────────────
    if (showBugDialog) {
        AlertDialog(
            onDismissRequest = { showBugDialog = false },
            title = { Text(text = "Report a Bug", color = OffWhite) },
            text = { Text(text = "To report a bug, please contact support@nodoff.example.com or visit our GitHub repository.", color = LowContrastGrey) },
            confirmButton = {
                TextButton(onClick = { showBugDialog = false }) {
                    Text(text = "OK", color = BrushedCopper)
                }
            },
            containerColor = Color(0xFF151819),
            titleContentColor = OffWhite,
            textContentColor = OffWhite,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
        )
    }

    // ── Bottom Sheet ──────────────────────────────────────────────────────────
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color(0xFF151819),
            contentColor = OffWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "APP STATISTICS",
                    color = BrushedCopper,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                NodOffCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Times Paused Media", color = OffWhite)
                        Text(text = "42", color = BrushedCopper, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                NodOffCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Devices Locked", color = OffWhite)
                        Text(text = "18", color = BrushedCopper, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = @Composable {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF151819),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "NodOff Menu",
                    color = BrushedCopper,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(color = Color(0xFF2A2A2A), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                DrawerItem(label = "About") {
                    scope.launch { drawerState.close() }
                    showAboutDialog = true
                }
                DrawerItem(label = "Privacy Policy") {
                    scope.launch { drawerState.close() }
                    showPrivacyDialog = true
                }
                DrawerItem(label = "Report a Bug") {
                    scope.launch { drawerState.close() }
                    showBugDialog = true
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = { BottomNavBar(selectedTab = 0, onTabSelected = onNavigate) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            // ── FIX: Apply paddingValues and make content scrollable ──────────
            Column(
                modifier = Modifier
                    .padding(paddingValues)           // respects bottom nav bar inset
                    .fillMaxSize()
                    .verticalScroll(scrollState)      // enables scroll past bottom nav
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = OffWhite)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_nodoff_logo),
                            contentDescription = "NodOff Logo",
                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                        )
                        Text(text = "NodOff", color = BrushedCopper, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // System State Card
                NodOffCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SYSTEM STATE",
                        color = LowContrastGrey,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    val statusText = if (!isMonitoring) {
                        "STANDBY"
                    } else {
                        when (eyeTrackingState.status) {
                            EyeStatus.IDLE -> "IDLE"
                            EyeStatus.EYES_OPEN -> "EYES OPEN"
                            EyeStatus.EYES_CLOSED -> "EYES CLOSED"
                            EyeStatus.FACE_LOST_TIMEOUT -> "FACE LOST"
                        }
                    }
                    val statusColor = if (isMonitoring && eyeTrackingState.status == EyeStatus.EYES_CLOSED) BrushedCopper else OffWhite
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "FREQ: 12.3Hz", color = LowContrastGrey, fontSize = 10.sp)
                        Text(text = "CONF: 98.8%", color = LowContrastGrey, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Start Monitoring Button
                IconButton(
                    onClick = { viewModel.toggleMonitoring() },
                    modifier = Modifier
                        .size(120.dp)
                        .border(2.dp, BrushedCopper, RoundedCornerShape(100.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isMonitoring) Icons.Default.Pause else Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = BrushedCopper,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = if (isMonitoring) "STOP MONITORING" else "START MONITORING",
                    color = BrushedCopper,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Sleep Actions Card
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SLEEP ACTIONS",
                        color = LowContrastGrey,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    NodOffCard {
                        ActionToggle(
                            title = "Pause Media",
                            icon = Icons.Default.Pause,
                            checked = pauseMedia,
                            onCheckedChange = { viewModel.setPauseMedia(it) }
                        )
                        HorizontalDivider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(vertical = 12.dp))
                        ActionToggle(
                            title = "Turn Off Screen",
                            icon = Icons.Default.Smartphone,
                            checked = turnOffScreen && isDeviceAdminGranted,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                                    val adminComponent = ComponentName(context, NodOffDeviceAdminReceiver::class.java)
                                    val isAdminActive = dpm?.isAdminActive(adminComponent) == true

                                    if (isAdminActive) {
                                        viewModel.setTurnOffScreen(true)
                                    } else {
                                        // FIX: Show educational dialog FIRST, then launch intent on Proceed
                                        viewModel.setTurnOffScreen(false)
                                        showScreenLockDialog = true
                                    }
                                } else {
                                    viewModel.setTurnOffScreen(false)
                                }
                            }
                        )
                        HorizontalDivider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(vertical = 12.dp))
                        ActionToggle(
                            title = "Disconnect Bluetooth",
                            icon = Icons.Default.BluetoothDisabled,
                            checked = disconnectBluetooth,
                            onCheckedChange = { viewModel.setDisconnectBluetooth(it) }
                        )
                    }
                    // FIX: Extra bottom spacer so last row isn't obscured when not scrolled
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
