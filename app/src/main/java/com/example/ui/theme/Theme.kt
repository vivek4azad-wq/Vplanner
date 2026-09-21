package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = SkyBlue80,
    onPrimary = SlateDark,
    primaryContainer = SkyBlue40,
    onPrimaryContainer = Color.White,
    secondary = AmberAccent,
    onSecondary = SlateDark,
    tertiary = EmeraldSuccess,
    background = SlateDark,
    surface = SlateCard,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = Color(0xFFE2E8F0)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SkyBlue40,
    onPrimary = Color.White,
    primaryContainer = SkyBlue90,
    onPrimaryContainer = SkyBlue10,
    secondary = AmberAccent,
    onSecondary = Color.White,
    tertiary = EmeraldSuccess,
    background = SlateLight,
    surface = CardLight,
    onBackground = SlateDark,
    onSurface = SlateDark,
    surfaceVariant = OutlineLight,
    onSurfaceVariant = SlateCard
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
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
