package com.example.nodoff.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun NodOffTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        background = DeepBlack,
        surface = DarkSlateGrey,
        primary = BrushedCopper,
        onBackground = OffWhite,
        onSurface = OffWhite,
        onPrimary = OffWhite,
        surfaceVariant = Color(0xFF2A2A2A),
        outline = Color(0xFF333333)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Standard Material3 typography or custom
        content = content
    )
}