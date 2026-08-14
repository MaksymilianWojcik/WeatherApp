package com.mw.medical.weatherapp.core.testing

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.Locale

class DefaultLocaleExtension(
    private val locale: Locale = Locale.UK,
) : BeforeEachCallback, AfterEachCallback {

    private lateinit var original: Locale

    override fun beforeEach(context: ExtensionContext) {
        original = Locale.getDefault()
        Locale.setDefault(locale)
    }

    override fun afterEach(context: ExtensionContext) {
        Locale.setDefault(original)
    }
}
