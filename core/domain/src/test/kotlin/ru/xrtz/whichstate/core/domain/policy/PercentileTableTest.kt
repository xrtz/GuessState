package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.Level
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentileTableTest {
    private val table = PercentileTable()

    @Test
    fun `zero correct answers is the zeroth percentile on every level`() {
        assertEquals(0, table.percentile(Level.ROOKIE, correct = 0))
        assertEquals(0, table.percentile(Level.EXPLORER, correct = 0))
        assertEquals(0, table.percentile(Level.MASTER, correct = 0))
    }

    @Test
    fun `rookie table steps match the site exactly`() {
        assertEquals(5, table.percentile(Level.ROOKIE, correct = 1))
        assertEquals(12, table.percentile(Level.ROOKIE, correct = 4))
        assertEquals(25, table.percentile(Level.ROOKIE, correct = 7))
        assertEquals(78, table.percentile(Level.ROOKIE, correct = 16))
    }

    @Test
    fun `value between two thresholds uses the lower (already-passed) threshold`() {
        assertEquals(12, table.percentile(Level.ROOKIE, correct = 6))
    }

    @Test
    fun `a correct count past the highest threshold stays at that threshold's percentile`() {
        assertEquals(78, table.percentile(Level.ROOKIE, correct = 50))
        assertEquals(80, table.percentile(Level.EXPLORER, correct = 100))
        assertEquals(79, table.percentile(Level.MASTER, correct = 208))
    }

    @Test
    fun `explorer and master tables differ from rookie`() {
        assertEquals(4, table.percentile(Level.EXPLORER, correct = 1))
        assertEquals(3, table.percentile(Level.MASTER, correct = 1))
    }
}
