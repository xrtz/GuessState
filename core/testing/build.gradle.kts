plugins {
    id("whichstate.jvm.library")
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.test)
}
