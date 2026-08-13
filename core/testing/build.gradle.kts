plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(libs.junit.jupiter.api)
    api(libs.kotlinx.coroutines.test)
}
