package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Проверяется [Csc] через [Sin]. Эталон узлов — [StubTables.Csc.TABLE] при табличных стабах.
 */
@DisplayName("Csc: через sin")
class CscTest {
    private val csc = Csc(StubTables.Sin.module)

    companion object {
        @JvmStatic
        fun cscReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Csc.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения csc по эталону StubTables.Csc.TABLE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("cscReferenceRows")
    fun `csc matches reference table`(x: Double, expected: Double?) {
        if (expected == null) {
            assertNull(csc.compute(x))
        } else {
            assertEquals(expected, csc.compute(x)!!, REF_TOLERANCE)
        }
    }

    @Test
    @DisplayName("csc(0): полюс, возвращается null")
    fun `csc pole at zero`() {
        assertNull(csc.compute(0.0))
    }

    @Test
    @DisplayName("csc: null, если sin вернул null")
    fun `csc null when sin null`() {
        val sin = mock<FunctionModule>()
        whenever(sin.moduleId).thenReturn("sin")
        whenever(sin.compute(any())).thenReturn(null)
        assertNull(Csc(sin).compute(1.0))
    }
}
