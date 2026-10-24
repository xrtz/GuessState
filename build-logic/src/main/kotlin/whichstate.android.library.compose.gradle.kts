import com.android.build.api.dsl.LibraryExtension
import ru.xrtz.whichstate.buildlogic.library
import ru.xrtz.whichstate.buildlogic.libs

plugins {
    id("whichstate.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<LibraryExtension> {
    buildFeatures {
        compose = true
    }
}

dependencies {
    val bom = platform(libs.library("androidx-compose-bom"))
    "implementation"(bom)
    "androidTestImplementation"(bom)

    "implementation"(libs.library("androidx-compose-ui"))
    "implementation"(libs.library("androidx-compose-ui-graphics"))
    "implementation"(libs.library("androidx-compose-ui-tooling-preview"))
    "implementation"(libs.library("androidx-compose-material3"))

    "debugImplementation"(libs.library("androidx-compose-ui-tooling"))
    "debugImplementation"(libs.library("androidx-compose-ui-test-manifest"))
    "androidTestImplementation"(libs.library("androidx-compose-ui-test-junit4"))
}
