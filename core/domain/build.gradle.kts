plugins {
    id("whichstate.jvm.library")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
