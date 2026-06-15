package com.example.nodoff.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nodoff.data.db.EventEntity
import com.example.nodoff.ui.components.BottomNavBar
import com.example.nodoff.ui.components.NodOffCard
import com.example.nodoff.ui.theme.*
import com.example.nodoff.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy - HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun EventItem(event: EventEntity) {
    NodOffCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = event.actionType.replace("_", " "),
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(event.timestamp),
                    color = LowContrastGrey,
                    fontSize = 12.sp
                )
            }
            val icon = when (event.actionType) {
                "PAUSE_MEDIA" -> Icons.Default.Pause
                "DISCONNECT_BLUETOOTH" -> Icons.Default.Bluetooth
                "TURN_OFF_SCREEN" -> Icons.Default.Lock
                else -> Icons.Default.Info
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrushedCopper,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HistoryScreen(viewModel: MainViewModel, onNavigate: (Int) -> Unit) {
    val events by viewModel.allEvents.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(selectedTab = 1, onTabSelected = onNavigate) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HISTORY LOG",
                color = OffWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sleep actions triggered yet.",
                        color = LowContrastGrey,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(events) { event ->
                        EventItem(event)
                    }
                }
            }
        }
    }
}
