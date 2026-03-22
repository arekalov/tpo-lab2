package com.arekalov.tpolab2.functions.core

import kotlin.math.PI
import kotlin.math.cos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("reduceToMinusPiPi: период и ветки приведения")
class TrigAngleReductionTest {

    @Test
    fun `reduced angle in closed interval minus pi to pi`() {
        val xs = (-500..500).map { it * 0.03 }
        for (x in xs) {
            val y = reduceToMinusPiPi(x)
            assertTrue(y >= -PI && y <= PI, "x=$x -> y=$y out of [-π,π]")
            assertEquals(cos(x), cos(y), 1e-12, "cos periodicity x=$x")
        }
    }

    @Test
    fun `raw remainder below minus pi triggers plus two pi branch`() {
        val twoPi = 2.0 * PI
        val x0 = -5.0
        assertTrue((x0 % twoPi) < -PI, "на JVM -5 % 2π даёт остаток < −π, срабатывает y += 2π")
        val y = reduceToMinusPiPi(x0)
        assertTrue(y >= -PI && y <= PI)
        assertEquals(cos(x0), cos(y), 1e-12)
    }
}
