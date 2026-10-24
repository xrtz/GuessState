package ru.xrtz.whichstate.core.map.hittest

import androidx.compose.ui.geometry.Offset
import ru.xrtz.whichstate.core.map.geometry.StateGeometry
import kotlin.math.hypot

object StateHitTester {
    fun hitTest(point: Offset, states: List<StateGeometry>, tolerance: Float = 0f): StateGeometry? {
        states.firstOrNull { contains(it, point) }?.let { return it }
        if (tolerance <= 0f) return null
        return states
            .asSequence()
            .map { it to distanceToBoundary(it, point) }
            .filter { (_, distance) -> distance <= tolerance }
            .minByOrNull { (_, distance) -> distance }
            ?.first
    }

    fun contains(state: StateGeometry, point: Offset): Boolean {
        if (!inBbox(state, point)) return false
        return state.rings.fold(false) { inside, ring -> inside xor rayCastCrossesOdd(ring, point) }
    }

    private fun inBbox(state: StateGeometry, point: Offset) =
        point.x in state.bbox.left..state.bbox.right && point.y in state.bbox.top..state.bbox.bottom

    private fun rayCastCrossesOdd(ring: List<Offset>, point: Offset): Boolean {
        var inside = false
        var j = ring.size - 1
        for (i in ring.indices) {
            val pi = ring[i]
            val pj = ring[j]
            if ((pi.y > point.y) != (pj.y > point.y)) {
                val xIntersect = pi.x + (point.y - pi.y) / (pj.y - pi.y) * (pj.x - pi.x)
                if (point.x < xIntersect) inside = !inside
            }
            j = i
        }
        return inside
    }

    private fun distanceToBoundary(state: StateGeometry, point: Offset): Float =
        state.rings.minOf { ring ->
            ring.indices.minOf { i -> distanceToSegment(point, ring[i], ring[(i + 1) % ring.size]) }
        }

    private fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
        val abx = b.x - a.x
        val aby = b.y - a.y
        val lengthSquared = abx * abx + aby * aby
        if (lengthSquared == 0f) return hypot((p.x - a.x).toDouble(), (p.y - a.y).toDouble()).toFloat()
        val t = (((p.x - a.x) * abx) + ((p.y - a.y) * aby)) / lengthSquared
        val clamped = t.coerceIn(0f, 1f)
        val projX = a.x + clamped * abx
        val projY = a.y + clamped * aby
        return hypot((p.x - projX).toDouble(), (p.y - projY).toDouble()).toFloat()
    }
}
