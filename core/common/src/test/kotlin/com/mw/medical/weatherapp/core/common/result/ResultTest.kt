package com.mw.medical.weatherapp.core.common.result

import com.mw.medical.weatherapp.core.common.error.AppError
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

class ResultTest {

    @Test
    fun `should transform value on success and pass failure through`() {
        val success: Result<Int> = Result.Success(2)
        val failure: Result<Int> = Result.Failure(AppError.Generic)

        success.map { it * 10 } shouldBeEqualTo Result.Success(20)
        failure.map { it * 10 } shouldBeEqualTo Result.Failure(AppError.Generic)
    }

    @Test
    fun `should return value on success and null on failure for getOrNull`() {
        Result.Success("value").getOrNull() shouldBeEqualTo "value"
        Result.Failure(AppError.Generic).getOrNull().shouldBeNull()
    }
}
