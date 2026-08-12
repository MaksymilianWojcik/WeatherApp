package com.mw.medical.weatherapp.core.common.result

import com.mw.medical.weatherapp.core.common.error.AppError

sealed interface Result<out T> {
    data class Success<out T>(val value: T) : Result<T>
    data class Failure(val error: AppError) : Result<Nothing>
}
