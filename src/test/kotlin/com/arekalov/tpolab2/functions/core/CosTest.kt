package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import kotlin.math.PI
import kotlin.math.cos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * В JUnit 5 `@Test` только на методах; на классе — например `@DisplayName`.
 */
@DisplayName("Cos: ряд Тейлора, периодичность, ОДЗ")
class CosTest {

    @ParameterizedTest(name = "x={0}")
    @MethodSource("angles")
    fun `cos series near kotlin reference`(x: Double) {
        val c = Cos(TEST_EPS)
        val y = c.compute(x)!!
        assertEquals(cos(x), y, REF_TOLERANCE)
    }

    @Test
    fun `periodicity two pi`() {
        val c = Cos(TEST_EPS)
        val x = -1.7
        assertEquals(c.compute(x)!!, c.compute(x + 4 * PI)!!, REF_TOLERANCE)
    }

    @ParameterizedTest
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `non finite returns null`(x: Double) {
        assertNull(Cos(TEST_EPS).compute(x))
    }

    @Test
    fun `init rejects non positive epsilon`() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Cos(0.0)
        }
    }

    @Test
    fun `max terms fallback returns partial sum`() {
        val c = Cos(epsilon = 1e-30, maxTerms = 4)
        val y = c.compute(1.0)!!
        org.junit.jupiter.api.Assertions.assertTrue(y.isFinite())
    }

    companion object {
        @JvmStatic
        fun angles() = listOf(
            Arguments.of(0.0),
            Arguments.of(PI / 4),
            Arguments.of(-PI / 3),
            Arguments.of(2.1),
            Arguments.of(-5.5),
        )
    }
}
