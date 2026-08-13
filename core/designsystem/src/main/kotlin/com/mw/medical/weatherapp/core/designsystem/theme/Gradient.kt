package com.mw.medical.weatherapp.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Brush

val ColorScheme.heroGradient: Brush
    get() = Brush.verticalGradient(
        listOf(
            primary,
            tertiary,
        ),
    )
