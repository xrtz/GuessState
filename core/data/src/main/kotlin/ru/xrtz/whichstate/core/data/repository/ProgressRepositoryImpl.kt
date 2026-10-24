package ru.xrtz.whichstate.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.xrtz.whichstate.core.data.local.db.dao.StateProgressDao
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository
import javax.inject.Inject

internal class ProgressRepositoryImpl @Inject constructor(
    private val stateProgressDao: StateProgressDao,
) : ProgressRepository {
    override fun observeMastered(): Flow<Set<UsState>> =
        stateProgressDao.observeMastered().map { entities -> entities.map { UsState.valueOf(it.state) }.toSet() }
}
