package com.example.nodoff.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    background = DeepBlack,
    surface = DarkSlateGrey,
    primary = BrushedCopper,
    onBackground = OffWhite,
    onSurface = OffWhite,
    onPrimary = OffWhite,
    surfaceVariant = Color(0xFF2A2A2A),
    outline = Color(0xFF333333)
)

private val LightColorScheme = lightColorScheme(
    background = StarkWhite,
    surface = LightSlateGrey,
    primary = BrushedCopper,
    onBackground = DarkText,
    onSurface = DarkText,
    onPrimary = OffWhite,
    surfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF9CA3AF)
)

@Composable
fun NodOffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Standard Material3 typography or custom
        content = content
    )
}