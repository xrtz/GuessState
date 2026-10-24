package ru.xrtz.whichstate.core.data.asset

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.UsState
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CitiesAssetDataSourceTest {
    private val dataSource = CitiesAssetDataSource(RuntimeEnvironment.getApplication())

    @Test
    fun `decodes cities dot json with the real Phase 01 population figures`() = runTest {
        val cities = dataSource.cities()

        val newYork = cities.single { it.id.value == "new-york-ny" }
        assertEquals("New York", newYork.name)
        assertEquals(UsState.NY, newYork.state)
        assertEquals(8_804_190, newYork.population)
        assertEquals(Level.ROOKIE, newYork.minLevel)

        assertTrue(cities.size >= 200, "expected the full Phase 01 city list, got ${cities.size}")
    }

    @Test
    fun `repeated calls return the same cached list instance`() = runTest {
        val first = dataSource.cities()
        val second = dataSource.cities()

        assertSame(first, second)
    }
}
