package com.mw.medical.weatherapp.core.common.error

sealed interface AppError {
    data object Generic : AppError
}
