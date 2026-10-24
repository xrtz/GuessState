package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.model.MasteryProgress
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveMasteryUseCaseTest {
    @Test
    fun `wraps the repository's mastered set with the fixed total of 50 states`() = runBlocking {
        val mastered = setOf(UsState.TX, UsState.NY)
        val repository = object : ProgressRepository {
            override fun observeMastered(): Flow<Set<UsState>> = flowOf(mastered)
        }
        val useCase = ObserveMasteryUseCase(repository)

        val progress = useCase().first()

        assertEquals(MasteryProgress(mastered), progress)
        assertEquals(50, progress.total)
    }
}
