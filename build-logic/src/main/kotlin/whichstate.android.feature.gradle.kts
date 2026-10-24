import ru.xrtz.whichstate.buildlogic.forbidModuleDependencies

plugins {
    id("whichstate.android.library.compose")
}

dependencies {
    "implementation"(project(":core:domain"))
    "implementation"(project(":core:designsystem"))
}

val siblingFeatures = listOf(":feature:menu", ":feature:game", ":feature:results", ":feature:party", ":feature:history")
    .filter { it != project.path }

forbidModuleDependencies(
    ":core:data",
    ":core:ads-yandex",
    *siblingFeatures.toTypedArray(),
)
