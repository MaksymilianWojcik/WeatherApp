package com.mw.medical.weatherapp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

object WeatherTheme {

    val colors: WeatherColors
        @Composable
        @ReadOnlyComposable
        get() = LocalWeatherColors.current

    val heroGradient: Brush
        @Composable
        @ReadOnlyComposable
        get() = Brush.verticalGradient(
            listOf(
                LocalWeatherColors.current.heroStart,
                LocalWeatherColors.current.heroEnd,
            ),
        )

    val sectionLabelStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.8.sp)
}
