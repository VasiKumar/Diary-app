package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = CleanPrimary, 
    secondary = CleanSecondaryContainer, 
    tertiary = SageProductive,
    background = CleanBg,
    surface = CleanSurface,
    onBackground = CleanOnBackground,
    onSurface = CleanOnBackground,
    primaryContainer = CleanPrimary,
    onPrimaryContainer = CleanOnPrimary,
    surfaceVariant = CleanBorder,
    onSurfaceVariant = CleanTextMuted,
    outline = CleanBorder,
    secondaryContainer = CleanActiveIndicator,
    onSecondaryContainer = CleanOnSecondaryContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CleanPrimary,
    secondary = CleanSecondaryContainer,
    tertiary = SageProductive,
    background = CleanBg,
    surface = CleanSurface,
    onBackground = CleanOnBackground,
    onSurface = CleanOnBackground,
    primaryContainer = CleanPrimary,
    onPrimaryContainer = CleanOnPrimary,
    surfaceVariant = CleanBorder,
    onSurfaceVariant = CleanTextMuted,
    outline = CleanBorder,
    secondaryContainer = CleanActiveIndicator,
    onSecondaryContainer = CleanOnSecondaryContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default matching the Clean Minimalism spec
  dynamicColor: Boolean = false, // Disable system dynamic color override to maintain the strict clean minimalist theme branding
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
