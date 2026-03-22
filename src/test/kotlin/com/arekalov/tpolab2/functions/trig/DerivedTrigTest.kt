package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.core.Cos
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Sin / Sec / Tan / Csc через Cos")
class DerivedTrigTest {

    private val cosMod = Cos(TEST_EPS)
    private val sinMod = Sin(cosMod)
    private val secMod = Sec(cosMod)
    private val tanMod = Tan(sinMod, cosMod)
    private val cscMod = Csc(sinMod)

    @ParameterizedTest(name = "x={0}")
    @MethodSource("angles")
    fun `sin matches reference`(x: Double) {
        assertEquals(sin(x), sinMod.compute(x)!!, REF_TOLERANCE)
    }

    @Test
    fun `sin near zero uses small magnitude branch`() {
        val x = 1e-15
        assertEquals(sin(x), sinMod.compute(x)!!, 1e-12)
    }

    @ParameterizedTest(name = "x={0}")
    @MethodSource("angles")
    fun `sec matches reference`(x: Double) {
        val c = cos(x)
        if (c == 0.0) {
            assertNull(secMod.compute(x))
        } else {
            assertEquals(1.0 / c, secMod.compute(x)!!, REF_TOLERANCE)
        }
    }

    @ParameterizedTest(name = "x={0}")
    @MethodSource("angles")
    fun `tan matches reference`(x: Double) {
        val c = cos(x)
        if (c == 0.0) {
            assertNull(tanMod.compute(x))
        } else {
            assertEquals(tan(x), tanMod.compute(x)!!, REF_TOLERANCE)
        }
    }

    @ParameterizedTest(name = "x={0}")
    @MethodSource("angles")
    fun `csc matches reference`(x: Double) {
        val s = sin(x)
        if (s == 0.0) {
            assertNull(cscMod.compute(x))
        } else {
            assertEquals(1.0 / s, cscMod.compute(x)!!, REF_TOLERANCE)
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY])
    fun `sin rejects non finite`(x: Double) {
        assertNull(sinMod.compute(x))
    }

    companion object {
        @JvmStatic
        fun angles() = listOf(
            Arguments.of(-1.1),
            Arguments.of(0.3),
            Arguments.of(PI / 6),
            Arguments.of(-PI / 2 + 0.01),
        )
    }
}
