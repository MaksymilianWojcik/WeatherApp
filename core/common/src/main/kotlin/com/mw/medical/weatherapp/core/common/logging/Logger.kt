package com.mw.medical.weatherapp.core.common.logging

interface Logger {
    fun debug(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )
}
