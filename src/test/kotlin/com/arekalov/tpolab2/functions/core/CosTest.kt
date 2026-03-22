package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import kotlin.math.PI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Проверяется реализация [Cos] (ряд). Эталон — [StubTables.Cos.REFERENCE] (см. описание [StubTables]).
 */
@DisplayName("Cos: ряд Тейлора, периодичность, ОДЗ")
class CosTest {

    companion object {
        @JvmStatic
        fun cosSeriesReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Cos.REFERENCE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения cos по эталону StubTables.Cos.REFERENCE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("cosSeriesReferenceRows")
    fun `cos series matches reference table`(x: Double, expected: Double) {
        val c = Cos(TEST_EPS)
        val y = c.compute(x)!!
        assertEquals(expected, y, REF_TOLERANCE)
    }

    @Test
    @DisplayName("Периодичность: cos(x) совпадает с cos(x + 4π)")
    fun `periodicity two pi`() {
        val c = Cos(TEST_EPS)
        val x = -1.7
        assertEquals(c.compute(x)!!, c.compute(x + 4 * PI)!!, REF_TOLERANCE)
    }

    @DisplayName("Для NaN и бесконечностей возвращается null")
    @ParameterizedTest(name = "аргумент: {0}")
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `non finite returns null`(x: Double) {
        assertNull(Cos(TEST_EPS).compute(x))
    }

    @Test
    @DisplayName("Конструктор отклоняет неположительный epsilon")
    fun `init rejects non positive epsilon`() {
        assertThrows<IllegalArgumentException> { Cos(0.0) }
        assertThrows<IllegalArgumentException> { Cos(-1.0) }
    }

    @Test
    @DisplayName("Конструктор отклоняет неположительный maxTerms")
    fun `init rejects non positive maxTerms`() {
        assertThrows<IllegalArgumentException> { Cos(epsilon = TEST_EPS, maxTerms = 0) }
        assertThrows<IllegalArgumentException> { Cos(epsilon = TEST_EPS, maxTerms = -1) }
    }

    @Test
    @DisplayName("При очень малым epsilon и лимите членов ряда результат остаётся конечным")
    fun `max terms fallback returns partial sum`() {
        val c = Cos(epsilon = 1e-30, maxTerms = 4)
        val y = c.compute(1.0)!!
        org.junit.jupiter.api.Assertions.assertTrue(y.isFinite())
    }
}
