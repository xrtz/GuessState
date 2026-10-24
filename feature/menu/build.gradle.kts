plugins {
    id("whichstate.android.feature")
    id("whichstate.hilt")
    id("whichstate.robolectric")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "ru.xrtz.whichstate.feature.menu"
}

roborazzi {
    outputDir.set(layout.projectDirectory.dir("src/test/screenshots"))
}

dependencies {
    implementation(project(":core:ads"))
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(project(":core:testing"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.androidx.compose.ui.test.junit4)
}
