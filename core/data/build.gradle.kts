plugins {
    id("whichstate.robolectric")
    id("whichstate.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
}

android {
    namespace = "ru.xrtz.whichstate.core.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.kotlinx.coroutines.test)
}
