package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import kotlin.math.E
import kotlin.math.ln
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Ln: ряд atanh, масштабирование, ОДЗ")
class LnTest {

    @ParameterizedTest(name = "x={0}")
    @MethodSource("positiveArgs")
    fun `ln series near kotlin reference`(x: Double) {
        val l = Ln(TEST_EPS)
        assertEquals(ln(x), l.compute(x)!!, REF_TOLERANCE)
    }

    @Test
    fun `ln one is zero`() {
        assertEquals(0.0, Ln(TEST_EPS).compute(1.0)!!, 0.0)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY])
    fun `domain and nan return null`(x: Double) {
        assertNull(Ln(TEST_EPS).compute(x))
    }

    @Test
    fun `scaling by ln2 branch`() {
        val l = Ln(TEST_EPS)
        assertEquals(ln(64.0), l.compute(64.0)!!, REF_TOLERANCE)
    }

    companion object {
        @JvmStatic
        fun positiveArgs() = listOf(
            Arguments.of(0.25),
            Arguments.of(1.5),
            Arguments.of(E),
            Arguments.of(100.0),
        )
    }
}
