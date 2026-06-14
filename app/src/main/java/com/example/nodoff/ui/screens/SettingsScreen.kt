package com.example.nodoff.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nodoff.ui.components.*
import com.example.nodoff.ui.theme.*
import com.example.nodoff.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel, onNavigate: (Int) -> Unit) {
    val eyeCloseDelayFlow by viewModel.eyeCloseDelay.collectAsState()
    val pollingRateFlow by viewModel.pollingRate.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(selectedTab = 2, onTabSelected = onNavigate) },
        containerColor = DeepBlack
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrushedCopper)
                    }
                    Text(
                        text = "SETTINGS",
                        color = OffWhite,
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
                    Text(text = "Eye-Close Delay", color = OffWhite)
                    Text(text = "Time before restorative alert triggers.", color = LowContrastGrey, fontSize = 12.sp)
                    var sliderValue by remember(eyeCloseDelayFlow) { mutableStateOf(eyeCloseDelayFlow) }
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { viewModel.setEyeCloseDelay(sliderValue) },
                        valueRange = 3f..60f,
                        colors = SliderDefaults.colors(
                            thumbColor = BrushedCopper,
                            activeTrackColor = BrushedCopper,
                            inactiveTrackColor = Color(0xFF2A2A2A)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "3S", color = LowContrastGrey, fontSize = 10.sp)
                        Text(text = "${sliderValue.toInt()}S", color = LowContrastGrey, fontSize = 10.sp)
                        Text(text = "60S", color = LowContrastGrey, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                NodOffCard {
                    Text(text = "Camera Polling Rate", color = OffWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    SegmentedButton(
                        options = listOf("CONTINUOUS", "2S", "5S"),
                        selectedIndex = pollingRateFlow,
                        onSelectedIndexChange = { viewModel.setPollingRate(it) }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item { SettingSectionHeader("AUTOMATION") }
            item {
                NodOffCard {
                    Text(text = "Auto-Start Monitor with Apps", color = OffWhite)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        AppChip("YOUTUBE")
                        Spacer(modifier = Modifier.width(8.dp))
                        AppChip("NETFLIX")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    NodOffButton(text = "+ ADD APP", onClick = {}, modifier = Modifier.fillMaxWidth(), isPrimary = false)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item { SettingSectionHeader("APPEARANCE") }
            item {
                NodOffCard {
                    Text(text = "Theme", color = OffWhite)
                    Spacer(modifier = Modifier.height(12.dp))
                    SegmentedButton(
                        options = listOf("SYSTEM", "LIGHT", "DARK"),
                        selectedIndex = 2,
                        onSelectedIndexChange = {}
                    )
                }
            }
        }
    }
}
