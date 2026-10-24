package ru.xrtz.whichstate.core.testing.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository

class FakeProgressRepository(initialMastered: Set<UsState> = emptySet()) : ProgressRepository {
    private val mastered = MutableStateFlow(initialMastered)

    override fun observeMastered(): Flow<Set<UsState>> = mastered

    fun setMastered(states: Set<UsState>) {
        mastered.value = states
    }
}
