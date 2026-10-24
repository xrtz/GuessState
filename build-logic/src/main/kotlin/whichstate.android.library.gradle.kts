import com.android.build.api.dsl.LibraryExtension
import ru.xrtz.whichstate.buildlogic.library
import ru.xrtz.whichstate.buildlogic.libs

plugins {
    id("com.android.library")
    id("whichstate.detekt")
}

extensions.configure<LibraryExtension> {
    compileSdk = 37

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    "testImplementation"(libs.library("junit"))
    "testImplementation"(libs.library("kotlin-test-junit"))
    "androidTestImplementation"(libs.library("androidx-junit"))
}
