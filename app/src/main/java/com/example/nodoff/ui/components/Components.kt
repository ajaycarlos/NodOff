package com.example.nodoff.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nodoff.ui.theme.*

@Composable
fun NodOffButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) BrushedCopper else MaterialTheme.colorScheme.surface,
            contentColor = OffWhite,
            disabledContainerColor = (if (isPrimary) BrushedCopper else MaterialTheme.colorScheme.surface).copy(alpha = 0.5f),
            disabledContentColor = OffWhite.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (!isPrimary) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun NodOffCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun ProtocolItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onGrantClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = BrushedCopper, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = OffWhite, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = LowContrastGrey, fontSize = 10.sp)
            }
        }
        Text(
            text = if (isGranted) "GRANTED" else "GRANT",
            color = if (isGranted) LowContrastGrey else BrushedCopper,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = if (isGranted) Modifier else Modifier.clickable { onGrantClick() }
        )
    }
}

@Composable
fun ActionToggle(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = LowContrastGrey, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = OffWhite)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OffWhite,
                checkedTrackColor = BrushedCopper,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
fun SettingSectionHeader(text: String) {
    Text(
        text = text,
        color = LowContrastGrey,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun AppChip(name: String, onRemoveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, color = OffWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = LowContrastGrey,
            modifier = Modifier
                .size(12.dp)
                .clickable { onRemoveClick() }
        )
    }
}

@Composable
fun SegmentedButton(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
            .height(40.dp)
    ) {
        options.forEachIndexed { index, option ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (index == selectedIndex) BrushedCopper else Color.Transparent)
                    .clickable { onSelectedIndexChange(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (index == selectedIndex) OffWhite else LowContrastGrey,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (index < options.size - 1) {
                VerticalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = Color(0xFF333333))
            }
        }
    }
}

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        val items = listOf("DASHBOARD", "HISTORY", "SETTINGS")
        val icons = listOf<ImageVector>(
            androidx.compose.material.icons.Icons.Filled.Speed,
            androidx.compose.material.icons.Icons.Filled.BarChart,
            androidx.compose.material.icons.Icons.Filled.Settings
        )
        
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrushedCopper,
                    selectedTextColor = BrushedCopper,
                    unselectedIconColor = LowContrastGrey,
                    unselectedTextColor = LowContrastGrey,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
