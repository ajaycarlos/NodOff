package com.example.nodoff.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nodoff.ui.components.*
import com.example.nodoff.ui.theme.*
import com.example.nodoff.ui.viewmodel.MainViewModel
import com.example.nodoff.ui.viewmodel.AppInfoItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import android.graphics.Canvas

private fun getAppLabel(context: Context, packageName: String): String {
    val pm = context.packageManager
    return try {
        val info = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(info).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName.split(".").last().uppercase()
    }
}

private fun drawableToImageBitmap(drawable: android.graphics.drawable.Drawable): ImageBitmap {
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

@Composable
fun AppPickerDialog(
    initialSelectedPackages: Set<String>,
    getInstalledApps: () -> List<AppInfoItem>,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    val installedApps = remember { getInstalledApps() }
    var selectedPackages by remember { mutableStateOf(initialSelectedPackages) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredApps = installedApps.filter {
        it.label.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "SELECT APPLICATIONS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search apps...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = BrushedCopper,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        text = {
            Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                if (filteredApps.isEmpty()) {
                    Text(
                        text = "No apps found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn {
                        items(filteredApps) { app ->
                            val isChecked = selectedPackages.contains(app.packageName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPackages = if (isChecked) {
                                            selectedPackages - app.packageName
                                        } else {
                                            selectedPackages + app.packageName
                                        }
                                    }
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // App Icon
                                app.icon?.let { drawable ->
                                    val imageBitmap = remember(drawable) { drawableToImageBitmap(drawable) }
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                
                                // App Name
                                Text(
                                    text = app.label,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                // Checkbox on the far right
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedPackages = if (checked == true) {
                                            selectedPackages + app.packageName
                                        } else {
                                            selectedPackages - app.packageName
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = BrushedCopper,
                                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        checkmarkColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selectedPackages) }) {
                Text("SAVE", color = BrushedCopper, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
    )
}

@Composable
fun SettingsScreen(viewModel: MainViewModel, onNavigate: (Int) -> Unit) {
    val context = LocalContext.current
    val eyeCloseDelayFlow by viewModel.eyeCloseDelay.collectAsState()
    val pollingRateFlow by viewModel.pollingRate.collectAsState()
    val automationApps by viewModel.automationApps.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()
    val faceLostTimeoutSecondsFlow by viewModel.faceLostTimeoutSeconds.collectAsState()

    var faceLostTimeoutMinutes by remember(faceLostTimeoutSecondsFlow) { mutableStateOf(faceLostTimeoutSecondsFlow / 60) }

    var showActivationDelayInfo by remember { mutableStateOf(false) }
    var showBatterySaverInfo by remember { mutableStateOf(false) }
    var showFaceLostTimeoutInfo by remember { mutableStateOf(false) }
    var showAppPicker by remember { mutableStateOf(false) }

    if (showActivationDelayInfo) {
        AlertDialog(
            onDismissRequest = { showActivationDelayInfo = false },
            title = { Text(text = "Activation Delay", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(text = "How many seconds your eyes must remain completely closed before the app triggers.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { showActivationDelayInfo = false }) {
                    Text(text = "OK", color = BrushedCopper)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
        )
    }

    if (showBatterySaverInfo) {
        AlertDialog(
            onDismissRequest = { showBatterySaverInfo = false },
            title = { Text(text = "Battery Saver Mode", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(text = "How frequently the camera checks your eyes. Slower checks save battery but react slower.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { showBatterySaverInfo = false }) {
                    Text(text = "OK", color = BrushedCopper)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
        )
    }

    if (showFaceLostTimeoutInfo) {
        AlertDialog(
            onDismissRequest = { showFaceLostTimeoutInfo = false },
            title = { Text(text = "Face Absence Timeout", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(text = "If the camera cannot detect a face at all (e.g., phone falls face down) for this duration, it will trigger the sleep actions.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { showFaceLostTimeoutInfo = false }) {
                    Text(text = "OK", color = BrushedCopper)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
        )
    }

    if (showAppPicker) {
        AppPickerDialog(
            initialSelectedPackages = automationApps,
            getInstalledApps = { viewModel.getInstalledApps() },
            onDismiss = { showAppPicker = false },
            onSave = { selected ->
                viewModel.setAutomationApps(selected)
                showAppPicker = false
            }
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(selectedTab = 2, onTabSelected = onNavigate) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onNavigate(0) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrushedCopper)
                    }
                    Text(
                        text = "SETTINGS",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item { SettingSectionHeader("TUNING") }
            item {
                NodOffCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Activation Delay", color = MaterialTheme.colorScheme.onSurface)
                        IconButton(
                            onClick = { showActivationDelayInfo = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(text = "Time before restorative alert triggers.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    var sliderValue by remember(eyeCloseDelayFlow) { mutableStateOf(eyeCloseDelayFlow) }
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { viewModel.setEyeCloseDelay(sliderValue) },
                        valueRange = 3f..60f,
                        colors = SliderDefaults.colors(
                            thumbColor = BrushedCopper,
                            activeTrackColor = BrushedCopper,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "3S", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        Text(text = "${sliderValue.toInt()}S", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        Text(text = "60S", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                NodOffCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Face Absence Timeout", color = MaterialTheme.colorScheme.onSurface)
                        IconButton(
                            onClick = { showFaceLostTimeoutInfo = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(text = "Time before trigger if no face is detected.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Slider(
                        value = faceLostTimeoutMinutes.toFloat(),
                        onValueChange = { faceLostTimeoutMinutes = it.toInt() },
                        onValueChangeFinished = { viewModel.setFaceLostTimeoutSeconds(faceLostTimeoutMinutes * 60) },
                        valueRange = 1f..10f,
                        colors = SliderDefaults.colors(
                            thumbColor = BrushedCopper,
                            activeTrackColor = BrushedCopper,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "1 min", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        Text(
                            text = if (faceLostTimeoutMinutes == 1) "1 min" else "$faceLostTimeoutMinutes mins",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Text(text = "10 mins", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                NodOffCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Battery Saver Mode", color = MaterialTheme.colorScheme.onSurface)
                        IconButton(
                            onClick = { showBatterySaverInfo = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    var expanded by remember { mutableStateOf(false) }
                    val options = listOf("CONT", "2S", "5S", "10S", "30S", "60S")
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { expanded = true }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = options.getOrElse(pollingRateFlow) { "CONT" },
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        ) {
                            options.forEachIndexed { index, option ->
                                DropdownMenuItem(
                                    text = { Text(text = option, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        viewModel.setPollingRate(index)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item { SettingSectionHeader("AUTOMATION") }
            item {
                NodOffCard {
                    Text(text = "Auto-Start Monitor with Apps", color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (automationApps.isEmpty()) {
                        Text(text = "No apps selected", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            automationApps.forEach { pkg ->
                                AppChip(
                                    name = getAppLabel(context, pkg),
                                    onRemoveClick = { viewModel.removeAutomationApp(pkg) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    NodOffButton(
                        text = "+ ADD APPS",
                        onClick = { showAppPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = false
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item { SettingSectionHeader("APPEARANCE") }
            item {
                NodOffCard {
                    Text(text = "Theme", color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(12.dp))
                    SegmentedButton(
                        options = listOf("SYSTEM", "LIGHT", "DARK"),
                        selectedIndex = themePreference,
                        onSelectedIndexChange = { viewModel.setThemePreference(it) }
                    )
                }
            }
        }
    }
}
