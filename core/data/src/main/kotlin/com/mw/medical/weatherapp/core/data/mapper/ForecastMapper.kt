package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.ForecastEntryDto
import com.mw.medical.weatherapp.core.data.remote.dto.ForecastResponse
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.abs

internal fun ForecastResponse.toDomain(): Forecast {
    val offset = ZoneOffset.ofTotalSeconds(city.timezone)
    val entries = list.map { TimedEntry(it, it.localTime(offset)) }
    return Forecast(
        hourly = entries.toHourly(),
        daily = entries.toDaily(),
    )
}

private fun List<TimedEntry>.toHourly(): List<HourlyForecast> {
    return map {
        HourlyForecast(
            time = it.time,
            temperature = it.dto.main.temp,
            condition = it.dto.weather.toCondition(),
        )
    }
}

private fun List<TimedEntry>.toDaily(): List<DailyForecast> {
    return groupBy { it.time.toLocalDate() }
        .toSortedMap()
        .map { (date, dayEntries) ->
            DailyForecast(
                date = date,
                minTemperature = dayEntries.minOf { it.dto.main.tempMin },
                maxTemperature = dayEntries.maxOf { it.dto.main.tempMax },
                condition = dayEntries.representativeCondition(),
            )
        }
}

private fun List<TimedEntry>.representativeCondition(): WeatherCondition {
    val closestToMidday = minByOrNull {
        abs(it.time.toLocalTime().toSecondOfDay() - LocalTime.NOON.toSecondOfDay())
    }
    return closestToMidday?.dto?.weather?.toCondition() ?: WeatherCondition.EMPTY
}

private fun ForecastEntryDto.localTime(offset: ZoneOffset) =
    LocalDateTime.ofEpochSecond(dt, 0, offset)

private data class TimedEntry(
    val dto: ForecastEntryDto,
    val time: LocalDateTime,
)
