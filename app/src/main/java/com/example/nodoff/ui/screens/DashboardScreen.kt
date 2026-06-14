package com.example.nodoff.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun DashboardScreen(viewModel: MainViewModel, onNavigate: (Int) -> Unit) {
    val isMonitoring by viewModel.isMonitoring.collectAsState()
    val pauseMedia by viewModel.pauseMedia.collectAsState()
    val turnOffScreen by viewModel.turnOffScreen.collectAsState()
    val disconnectBluetooth by viewModel.disconnectBluetooth.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(selectedTab = 0, onTabSelected = onNavigate) },
        containerColor = DeepBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = OffWhite)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nodoff_logo),
                        contentDescription = "NodOff Logo",
                        modifier = Modifier.size(24.dp).padding(end = 8.dp)
                    )
                    Text(text = "NodOff", color = BrushedCopper, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = OffWhite)
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
                Text(
                    text = if (isMonitoring) "EYES CLOSED" else "STANDBY",
                    color = if (isMonitoring) BrushedCopper else OffWhite,
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

            // Sleep Actions
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
                    Divider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(vertical = 12.dp))
                    ActionToggle(
                        title = "Turn Off Screen",
                        icon = Icons.Default.Smartphone,
                        checked = turnOffScreen,
                        onCheckedChange = { viewModel.setTurnOffScreen(it) }
                    )
                    Divider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(vertical = 12.dp))
                    ActionToggle(
                        title = "Disconnect Bluetooth",
                        icon = Icons.Default.BluetoothDisabled,
                        checked = disconnectBluetooth,
                        onCheckedChange = { viewModel.setDisconnectBluetooth(it) }
                    )
                }
            }
        }
    }
}
