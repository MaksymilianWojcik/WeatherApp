package com.mw.medical.weatherapp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush

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
}
