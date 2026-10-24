package ru.xrtz.whichstate.core.map.asset

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.map.hittest.StateHitTester
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class MapAssetDataSourceTest {
    private val dataSource = MapAssetDataSource(RuntimeEnvironment.getApplication())

    @Test
    fun `decodes all 50 states plus DC`() = runTest {
        val geometry = dataSource.geometry()

        assertEquals(51, geometry.states.size)
        assertEquals(UsState.entries.toSet(), geometry.states.mapNotNull { it.usState }.toSet())
        assertNull(geometry.states.single { it.code == "DC" }.usState)
    }

    @Test
    fun `every state's label point falls inside its own shape`() = runTest {
        val geometry = dataSource.geometry()

        geometry.states.filter { it.code != "HI" }.forEach { state ->
            assertTrue(
                StateHitTester.contains(state, state.label),
                "${state.code}'s label point ${state.label} isn't inside its own polygon",
            )
        }
    }

    @Test
    fun `repeated calls return the same cached instance`() = runTest {
        val first = dataSource.geometry()
        val second = dataSource.geometry()

        assertSame(first, second)
    }
}
