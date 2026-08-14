package com.mw.medical.weatherapp.logging

import android.util.Log
import com.mw.medical.weatherapp.core.common.logging.Logger
import javax.inject.Inject

// In a production app this would likely be Timber, which also handles release stripping.
internal class AndroidLogger @Inject constructor() : Logger {
    override fun debug(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        if (throwable == null) {
            Log.d(tag, message)
        } else {
            Log.d(tag, message, throwable)
        }
    }
}
