plugins {
    id("whichstate.android.library.compose")
    id("whichstate.robolectric")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "ru.xrtz.whichstate.core.designsystem"
}

roborazzi {
    outputDir.set(layout.projectDirectory.dir("src/test/screenshots"))
}

dependencies {
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.androidx.compose.ui.test.junit4)
}
