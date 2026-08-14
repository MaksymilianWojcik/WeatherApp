package com.mw.medical.weatherapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class WeatherColors(
    val heroStart: Color,
    val heroEnd: Color,
    val onHero: Color,
    val divider: Color,
)

internal val LightWeatherColors = WeatherColors(
    heroStart = HeroStartLight,
    heroEnd = HeroEndLight,
    onHero = CloudWhite,
    divider = DividerLight,
)

internal val DarkWeatherColors = WeatherColors(
    heroStart = HeroStartDark,
    heroEnd = HeroEndDark,
    onHero = CloudWhite,
    divider = DividerDark,
)

internal val LocalWeatherColors = staticCompositionLocalOf { LightWeatherColors }
