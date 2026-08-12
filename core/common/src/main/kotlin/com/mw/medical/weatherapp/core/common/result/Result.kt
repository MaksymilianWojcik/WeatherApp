package com.mw.medical.weatherapp.core.common.result

import com.mw.medical.weatherapp.core.common.error.AppError

sealed interface Result<out T> {
    data class Success<out T>(val value: T) : Result<T>
    data class Failure(val error: AppError) : Result<Nothing>
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(value))
    is Result.Failure -> this
}

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.value
