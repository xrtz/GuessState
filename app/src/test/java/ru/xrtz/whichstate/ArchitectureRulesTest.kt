package ru.xrtz.whichstate

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import org.junit.Test
import kotlin.test.assertTrue

class ArchitectureRulesTest {
    @Test
    fun `core-domain has no Android or Compose imports`() {
        val domainFiles = Konsist.scopeFromModule("core/domain").files

        assertTrue(
            domainFiles.all { file -> file.imports.none { it.name.startsWith("android") } },
            "core:domain must stay pure Kotlin (docs/ARCHITECTURE.md §2.2) — " +
                "found an android or androidx import.",
        )
    }

    @Test
    fun `ViewModel classes live only in feature modules`() {
        val viewModels = Konsist.scopeFromProject().classes().withNameEndingWith("ViewModel")

        assertTrue(
            viewModels.all { it.resideInPackage("ru.xrtz.whichstate.feature..") },
            "ViewModel classes belong in a :feature:* module (docs/ARCHITECTURE.md §3.1).",
        )
    }

    @Test
    fun `Room Dao and Entity classes stay inside core-data`() {
        val daoAndEntity = Konsist.scopeFromProject()
            .classes()
            .filter { it.name.endsWith("Dao") || it.name.endsWith("Entity") }

        assertTrue(
            daoAndEntity.all { it.resideInPackage("ru.xrtz.whichstate.core.data..") },
            "@Dao/@Entity classes must not leave :core:data (docs/ARCHITECTURE.md §3.1).",
        )
    }

    @Test
    fun `UseCase classes expose exactly one invoke function`() {
        val useCases = Konsist.scopeFromProject().classes().withNameEndingWith("UseCase")

        assertTrue(
            useCases.all { useCase -> useCase.functions().count { it.name == "invoke" } == 1 },
            "A *UseCase class must expose exactly one operator fun invoke(...) (docs/ARCHITECTURE.md §3.1).",
        )
    }
}
