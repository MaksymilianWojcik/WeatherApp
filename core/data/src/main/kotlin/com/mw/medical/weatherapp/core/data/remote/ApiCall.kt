package com.mw.medical.weatherapp.core.data.remote

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import kotlinx.coroutines.CancellationException

/**
 * Runs a network/mapping [block] and converts the outcome into a typed [Result], so repositories never
 * throw: callers (and everything above them — domain, presentation) get a [Result.Failure] carrying an
 * [AppError] instead of a raw [retrofit2.HttpException] / [java.io.IOException] / serialization error.
 * This is the single place technical exceptions are turned into the app's error vocabulary.
 *
 * Why the explicit [CancellationException] rethrow, and how it ties to coroutines:
 * in structured concurrency a coroutine is cancelled by throwing a [CancellationException] *inside* it
 * (e.g. the caller's scope is torn down while a suspend call is in flight). Suspend functions must let
 * that exception propagate so the coroutine actually stops and its parent learns it was cancelled.
 * A blanket `catch (e: Exception)` would also catch [CancellationException] (it *is* an [Exception]) and
 * turn a cancellation into an ordinary `Result.Failure` — the coroutine would look like it completed
 * normally instead of cancelling, silently breaking cancellation/structured concurrency. So we rethrow
 * it before the general catch; only genuine failures become [AppError.Generic].
 */
internal inline fun <T> apiCall(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Failure(AppError.Generic)
    }
}
