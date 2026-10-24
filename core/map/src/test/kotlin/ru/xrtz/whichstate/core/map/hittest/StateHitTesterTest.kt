package ru.xrtz.whichstate.core.map.hittest

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Test
import ru.xrtz.whichstate.core.map.geometry.StateGeometry
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StateHitTesterTest {
    private fun square(code: String, left: Float, top: Float, right: Float, bottom: Float) = StateGeometry(
        code = code,
        name = code,
        usState = null,
        rings = listOf(
            listOf(Offset(left, top), Offset(right, top), Offset(right, bottom), Offset(left, bottom)),
        ),
        bbox = Rect(left, top, right, bottom),
        label = Offset((left + right) / 2f, (top + bottom) / 2f),
    )

    private val squareA = square("A", 0f, 0f, 10f, 10f)
    private val squareB = square("B", 10f, 0f, 20f, 10f)

    @Test
    fun `point at the center of a square hits that square`() {
        val hit = StateHitTester.hitTest(Offset(5f, 5f), listOf(squareA, squareB))

        assertEquals(squareA, hit)
    }

    @Test
    fun `point outside every polygon and outside tolerance misses`() {
        val hit = StateHitTester.hitTest(Offset(100f, 100f), listOf(squareA, squareB), tolerance = 5f)

        assertNull(hit)
    }

    @Test
    fun `point just past a boundary is a miss with no tolerance`() {
        val hit = StateHitTester.hitTest(Offset(-1f, 5f), listOf(squareA, squareB))

        assertNull(hit)
    }

    @Test
    fun `point just past a boundary within tolerance hits the nearest polygon`() {
        val hit = StateHitTester.hitTest(Offset(-1f, 5f), listOf(squareA, squareB), tolerance = 2f)

        assertEquals(squareA, hit)
    }

    @Test
    fun `tolerance picks whichever polygon boundary is actually closer`() {
        val hit = StateHitTester.hitTest(Offset(11f, -1f), listOf(squareA, squareB), tolerance = 3f)

        assertEquals(squareB, hit)
    }

    @Test
    fun `a state is a hit if the point falls inside any of its disjoint rings`() {
        val multiRing = StateGeometry(
            code = "M",
            name = "M",
            usState = null,
            rings = listOf(
                listOf(Offset(0f, 0f), Offset(4f, 0f), Offset(4f, 4f), Offset(0f, 4f)),
                listOf(Offset(20f, 20f), Offset(24f, 20f), Offset(24f, 24f), Offset(20f, 24f)),
            ),
            bbox = Rect(0f, 0f, 24f, 24f),
            label = Offset(2f, 2f),
        )

        assertTrue(StateHitTester.contains(multiRing, Offset(22f, 22f)))
        assertTrue(StateHitTester.contains(multiRing, Offset(2f, 2f)))
    }

    @Test
    fun `a concave ring does not register a hit in its notch`() {
        val notched = StateGeometry(
            code = "U",
            name = "U",
            usState = null,
            rings = listOf(
                listOf(
                    Offset(0f, 0f),
                    Offset(4f, 0f),
                    Offset(4f, 6f),
                    Offset(6f, 6f),
                    Offset(6f, 0f),
                    Offset(10f, 0f),
                    Offset(10f, 10f),
                    Offset(0f, 10f),
                ),
            ),
            bbox = Rect(0f, 0f, 10f, 10f),
            label = Offset(5f, 8f),
        )

        assertEquals(false, StateHitTester.contains(notched, Offset(5f, 2f)), "point in the notch should be outside")
        assertTrue(StateHitTester.contains(notched, Offset(2f, 2f)), "point in the solid left arm should be inside")
    }
}
