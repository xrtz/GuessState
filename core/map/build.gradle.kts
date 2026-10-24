plugins {
    id("whichstate.android.library.compose")
    id("whichstate.robolectric")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.xrtz.whichstate.core.map"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
}
