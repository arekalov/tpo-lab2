package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.stream.Stream

@DisplayName("SystemFunction: кусочное ветвление и NaN")
class SystemFunctionTest {

    val system = SystemFunction(
        trigBranch = StubTables.TrigBranch.module,
        logBranch = StubTables.LogBranch.module
    )

    companion object {
        @JvmStatic
        fun systemTableRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.System.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Параметризованные тесты на system")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `hand stub values at x0`(x: Double, expected: Double?) {
        if (expected == null) {
            assertNull(system.compute(x))
        } else {
            assertEquals(expected, system.compute(x)!!, REF_TOLERANCE)
        }
    }

    @DisplayName("Неконечный x (NaN, ±∞): система возвращает null")
    @ParameterizedTest(name = "{0}")
    @CsvSource("NaN", "Infinity", "-Infinity")
    fun `non finite x returns null`(token: String) {
        val x = when (token) {
            "NaN" -> Double.NaN
            "Infinity" -> Double.POSITIVE_INFINITY
            else -> Double.NEGATIVE_INFINITY
        }
        val a = mock<FunctionModule>()
        val b = mock<FunctionModule>()
        whenever(a.moduleId).thenReturn("a")
        whenever(b.moduleId).thenReturn("b")
        val sys = SystemFunction(a, b)
        assertNull(sys.compute(x))
    }

    @Test
    @DisplayName("null от выбранной ветки пробрасывается наружу")
    fun `branch null propagates`() {
        val a = mock<FunctionModule>()
        val b = mock<FunctionModule>()
        whenever(a.moduleId).thenReturn("a")
        whenever(b.moduleId).thenReturn("b")
        whenever(a.compute(any())).thenReturn(null)
        whenever(b.compute(any())).thenReturn(1.0)
        val sys = SystemFunction(a, b)
        assertNull(sys.compute(-1.0))
    }
}
