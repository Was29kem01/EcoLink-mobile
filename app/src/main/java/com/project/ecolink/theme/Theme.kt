package com.project.ecolink.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.project.ecolink.data.AppSettings

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4E8B5C),
    secondary = Color(0xFFC4693C),
    tertiary = Color(0xFFD7A24A),
    background = Color(0xFF121411),
    surface = Color(0xFF1C201A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE4E2DD),
    onSurface = Color(0xFFE4E2DD)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2F4B3C),
    secondary = Color(0xFFC4693C),
    tertiary = Color(0xFFD7A24A),
    background = Color(0xFFF4EFE6),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF21261F),
    onSurface = Color(0xFF21261F)
)

@Composable
fun CleantrackTheme(
    darkTheme: Boolean = AppSettings.isDarkMode ?: isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
