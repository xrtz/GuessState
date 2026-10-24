import io.gitlab.arturbosch.detekt.Detekt
import ru.xrtz.whichstate.buildlogic.library
import ru.xrtz.whichstate.buildlogic.libs

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
}

dependencies {
    "detektPlugins"(libs.library("detekt-formatting"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}
