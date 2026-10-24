import ru.xrtz.whichstate.buildlogic.library
import ru.xrtz.whichstate.buildlogic.libs

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("whichstate.detekt")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
    useJUnit()
}

dependencies {
    "testImplementation"(libs.library("junit"))
    "testImplementation"(libs.library("kotlin-test-junit"))
}
