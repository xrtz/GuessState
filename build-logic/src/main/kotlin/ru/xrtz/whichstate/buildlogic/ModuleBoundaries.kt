package ru.xrtz.whichstate.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

fun Project.forbidModuleDependencies(vararg forbiddenProjectPaths: String) {
    val forbidden = forbiddenProjectPaths.toSet()
    val checkTask = tasks.register("checkModuleBoundaries") {
        group = "verification"
        description = "Fails if this module depends on another module forbidden by docs/ARCHITECTURE.md §3."
        doLast {
            val violations = configurations
                .asSequence()
                .flatMap { it.dependencies.asSequence() }
                .filterIsInstance<ProjectDependency>()
                .map { it.path }
                .filter { it in forbidden }
                .distinct()
                .sorted()
                .toList()
            check(violations.isEmpty()) {
                "Module '${project.path}' depends on forbidden module(s): $violations. " +
                    "See docs/ARCHITECTURE.md §3 (\"Запрещено\")."
            }
        }
    }
    tasks.named("check") { dependsOn(checkTask) }
}
