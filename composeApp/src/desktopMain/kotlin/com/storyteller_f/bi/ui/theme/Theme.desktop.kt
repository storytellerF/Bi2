package com.storyteller_f.bi.ui.theme

import androidx.compose.runtime.Composable

@Composable
actual fun colorScheme(
    dynamicColor: Boolean,
    darkTheme: Boolean
) = if (darkTheme) {
    DarkColors
} else {
    LightColors
}
