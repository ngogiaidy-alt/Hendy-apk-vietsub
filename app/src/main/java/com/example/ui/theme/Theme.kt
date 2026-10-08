package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StudioColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = StudioBackground,
    primaryContainer = HyperVioletDim,
    onPrimaryContainer = StudioWhite,
    secondary = HyperViolet,
    onSecondary = StudioWhite,
    secondaryContainer = StudioSurfaceElevated,
    onSecondaryContainer = StudioWhite,
    tertiary = VividMagenta,
    onTertiary = StudioWhite,
    background = StudioBackground,
    onBackground = StudioWhite,
    surface = StudioSurface,
    onSurface = StudioWhite,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioTextMuted,
    outline = StudioCardBorder,
    outlineVariant = StudioSurfaceElevated
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We enforce studio dark theme for professional editing experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioBackground.toArgb()
                window.navigationBarColor = StudioBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content
    )
}
