package com.mw.medical.weatherapp.core.designsystem.icon

import androidx.annotation.DrawableRes
import com.mw.medical.weatherapp.core.designsystem.R

enum class WeatherIcon(@DrawableRes val drawableRes: Int) {
    ClearDay(R.drawable.ic_clear_day),
    ClearNight(R.drawable.ic_clear_night),
    FewClouds(R.drawable.ic_few_clouds),
    ScatteredClouds(R.drawable.ic_scattered_clouds),
    BrokenClouds(R.drawable.ic_broken_clouds),
    ShowerRain(R.drawable.ic_shower_rain),
    Rain(R.drawable.ic_rain),
    Thunderstorm(R.drawable.ic_thunderstorm),
    Snow(R.drawable.ic_snow),
    Mist(R.drawable.ic_mist),
    Unknown(R.drawable.ic_unknown),
}
