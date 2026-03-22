package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Проверяется [Csc] через [Sin]. Эталон узлов — [StubTables.Csc.TABLE] при табличных стабах.
 */
@DisplayName("Csc: через sin")
class CscTest {

    private val sinMod = Sin(StubTables.Cos.module)
    private val cscOverStub = Csc(sinMod)

    private val sinReal = Sin(Cos(TEST_EPS))
    private val cscOverReal = Csc(sinReal)

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
    fun `csc matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, cscOverStub.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("Для NaN и бесконечностей возвращается null (реальный Cos)")
    @ParameterizedTest(name = "аргумент: {0}")
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `non finite x returns null`(x: Double) {
        assertNull(cscOverReal.compute(x))
    }

    @Test
    @DisplayName("csc(0): полюс, возвращается null")
    fun `csc pole at zero`() {
        assertNull(cscOverStub.compute(0.0))
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
