import com.android.build.api.dsl.LibraryExtension
import ru.xrtz.whichstate.buildlogic.library
import ru.xrtz.whichstate.buildlogic.libs

plugins {
    id("whichstate.android.library")
}

extensions.configure<LibraryExtension> {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED",
        "--add-opens=java.base/sun.security.util=ALL-UNNAMED",
        "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
        "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
    )
}

dependencies {
    "testImplementation"(libs.library("robolectric"))
}
