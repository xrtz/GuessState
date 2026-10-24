package ru.xrtz.whichstate.core.data.repository

import ru.xrtz.whichstate.core.data.asset.CitiesAssetDataSource
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import javax.inject.Inject

internal class CityRepositoryImpl @Inject constructor(
    private val assets: CitiesAssetDataSource,
) : CityRepository {
    override suspend fun cities(): List<City> = assets.cities()
}
