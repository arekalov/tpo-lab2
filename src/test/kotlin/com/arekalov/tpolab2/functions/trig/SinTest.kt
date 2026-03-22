package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import kotlin.math.PI
import kotlin.math.sqrt
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
 * Проверяется [Sin] через модуль cos. Эталон узлов — [StubTables.Sin.TABLE] при [StubTables.Cos.module] (см. [StubTables]).
 */
@DisplayName("Sin: через cos, эталон и ветки")
class SinTest {

    private val sinOverStubCos = Sin(StubTables.Cos.module)
    private val sinOverRealCos = Sin(Cos(TEST_EPS))

    companion object {
        @JvmStatic
        fun sinReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Sin.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения sin по эталону StubTables.Sin.TABLE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("sinReferenceRows")
    fun `sin matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, sinOverStubCos.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("Для NaN и бесконечностей возвращается null (реальный Cos)")
    @ParameterizedTest(name = "аргумент: {0}")
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `non finite x returns null`(x: Double) {
        assertNull(sinOverRealCos.compute(x))
    }

    @Test
    @DisplayName("sin: null, если cos вернул null")
    fun `sin null when cos null`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(null)
        assertNull(Sin(cos).compute(0.0))
    }

    @Test
    @DisplayName("y=0: ветка else → 0 при mag > MAG_EPS (несогласованный cos на границе периода)")
    fun `else branch when reduced angle is zero and magnitude not snapped`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(0.5)
        assertEquals(0.0, Sin(cos).compute(0.0))
    }

    @Test
    @DisplayName("y=0 при x=2π: та же ветка else")
    fun `else branch when x is full period and cos inconsistent`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(0.5)
        assertEquals(0.0, Sin(cos).compute(2.0 * PI))
    }

    @Test
    @DisplayName("y<0: отрицательный sin (−mag)")
    fun `negative reduced angle yields negative sin`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(0.6)
        val c = 0.6
        val mag = sqrt(1.0 - c * c)
        assertEquals(-mag, Sin(cos).compute(-1.0)!!, 1e-15)
    }

    @Test
    @DisplayName("y>0: положительный sin (+mag)")
    fun `positive reduced angle yields positive sin`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(0.6)
        val c = 0.6
        val mag = sqrt(1.0 - c * c)
        assertEquals(mag, Sin(cos).compute(1.0)!!, 1e-15)
    }

    @Test
    @DisplayName("cos² > 1: sinSq обрезается, до ветки знака не доходим")
    fun `clipped sinSq returns zero before sign when cos magnitude exceeds one`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(1.1)
        assertEquals(0.0, Sin(cos).compute(1.0))
    }
}
