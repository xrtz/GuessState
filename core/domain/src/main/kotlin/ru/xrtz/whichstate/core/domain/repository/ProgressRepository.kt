package ru.xrtz.whichstate.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.xrtz.whichstate.core.domain.model.UsState

interface ProgressRepository {
    fun observeMastered(): Flow<Set<UsState>>
}
