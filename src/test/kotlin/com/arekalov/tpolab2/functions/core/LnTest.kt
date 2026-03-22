package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Проверяется реализация [Ln] (ряд). Эталон — [StubTables.Ln.REFERENCE] (см. описание [StubTables]).
 */
@DisplayName("Ln: ряд atanh, масштабирование, ОДЗ")
class LnTest {

    companion object {
        @JvmStatic
        fun lnSeriesReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Ln.REFERENCE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения ln по эталону StubTables.Ln.REFERENCE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("lnSeriesReferenceRows")
    fun `ln series matches reference table`(x: Double, expected: Double) {
        val l = Ln(TEST_EPS)
        assertEquals(expected, l.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("Вне ОДЗ и для не-числа возвращается null")
    @ParameterizedTest(name = "x = {0}")
    @ValueSource(doubles = [0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY])
    fun `domain and nan return null`(x: Double) {
        assertNull(Ln(TEST_EPS).compute(x))
    }
}
