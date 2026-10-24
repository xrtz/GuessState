plugins {
    id("whichstate.android.library.compose")
    id("whichstate.hilt")
}

android {
    namespace = "ru.xrtz.whichstate.core.ads.yandex"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        val demoBlocks = mapOf(
            "BANNER_MENU_AD_UNIT_ID" to "demo-banner-yandex",
            "BANNER_RESULTS_AD_UNIT_ID" to "demo-banner-yandex",
            "INTERSTITIAL_AD_UNIT_ID" to "demo-interstitial-yandex",
        )
        debug {
            demoBlocks.forEach { (field, value) -> buildConfigField("String", field, "\"$value\"") }
        }
        release {
            demoBlocks.forEach { (field, value) -> buildConfigField("String", field, "\"$value\"") }
        }
    }
}

dependencies {
    implementation(project(":core:ads"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.yandex.mobileads)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
