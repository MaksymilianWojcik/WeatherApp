plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(project(":core:common"))
    implementation(libs.javax.inject)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.kluent)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}
