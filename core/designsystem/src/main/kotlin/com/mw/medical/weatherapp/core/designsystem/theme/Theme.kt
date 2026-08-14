package com.mw.medical.weatherapp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = CloudWhite,
    secondary = StormGrey,
    onSecondary = CloudWhite,
    secondaryContainer = PaleSky,
    onSecondaryContainer = DeepSlate,
    tertiary = HorizonTeal,
    onTertiary = CloudWhite,
    background = Mist,
    onBackground = Ink,
    surface = CloudWhite,
    onSurface = Ink,
    surfaceVariant = CloudGrey,
    onSurfaceVariant = SlateGrey,
    outline = Fog,
    outlineVariant = FogSoft,
    error = ErrorRed,
    onError = CloudWhite,
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueLight,
    onPrimary = MidnightBlue,
    secondary = StormGreyLight,
    onSecondary = MidnightBlue,
    secondaryContainer = DuskGrey,
    onSecondaryContainer = PaleSky,
    tertiary = HorizonTealLight,
    onTertiary = MidnightBlue,
    background = NightSky,
    onBackground = Snow,
    surface = NightSurface,
    onSurface = Snow,
    surfaceVariant = NightGrey,
    onSurfaceVariant = MistGrey,
    outline = NightOutline,
    outlineVariant = NightOutlineSoft,
    error = ErrorRedLight,
    onError = MidnightBlue,
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val weatherColors = if (darkTheme) DarkWeatherColors else LightWeatherColors

    CompositionLocalProvider(LocalWeatherColors provides weatherColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = WeatherShapes,
            typography = Typography,
            content = content,
        )
    }
}
