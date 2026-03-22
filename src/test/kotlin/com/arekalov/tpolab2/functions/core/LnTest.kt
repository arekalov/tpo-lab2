package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Проверяется реализация [Ln] (ряд). Эталон — положительные узлы [StubTables.Ln.TABLE] (см. [StubTables]).
 */
@DisplayName("Ln: ряд atanh, масштабирование, ОДЗ")
class LnTest {

    companion object {
        @JvmStatic
        fun lnSeriesReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Ln.TABLE.entries
                    .filter { it.value != null && it.key > 0.0 }
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected!!) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения ln по эталону StubTables.Ln.TABLE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("lnSeriesReferenceRows")
    fun `ln series matches reference table`(x: Double, expected: Double) {
        val l = Ln(TEST_EPS)
        assertEquals(expected, l.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("x ≤ 0: вне ОДЗ — null")
    @ParameterizedTest(name = "x = {0}")
    @ValueSource(doubles = [0.0, -1.0])
    fun `non positive x returns null`(x: Double) {
        assertNull(Ln(TEST_EPS).compute(x))
    }

    @Test
    @DisplayName("x = NaN: null")
    fun `nan x returns null`() {
        assertNull(Ln(TEST_EPS).compute(Double.NaN))
    }

    @DisplayName("x = ±∞: null")
    @ParameterizedTest(name = "x = {0}")
    @ValueSource(doubles = [Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `infinite x returns null`(x: Double) {
        assertNull(Ln(TEST_EPS).compute(x))
    }

    @Test
    @DisplayName("Конструктор отклоняет неположительный epsilon")
    fun `init rejects non positive epsilon`() {
        assertThrows<IllegalArgumentException> { Ln(0.0) }
        assertThrows<IllegalArgumentException> { Ln(-1.0) }
    }

    @Test
    @DisplayName("Конструктор отклоняет неположительный maxTerms")
    fun `init rejects non positive maxTerms`() {
        assertThrows<IllegalArgumentException> { Ln(epsilon = TEST_EPS, maxTerms = 0) }
        assertThrows<IllegalArgumentException> { Ln(epsilon = TEST_EPS, maxTerms = -1) }
    }

    @Test
    @DisplayName("При очень малым epsilon и лимите членов ряда результат остаётся конечным")
    fun `max terms fallback returns partial sum`() {
        val y = Ln(epsilon = 1e-30, maxTerms = 4).compute(1.5)!!
        assertTrue(y.isFinite())
    }
}
