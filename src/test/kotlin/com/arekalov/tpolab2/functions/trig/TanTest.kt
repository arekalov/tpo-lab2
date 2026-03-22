package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import kotlin.math.PI
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
 * Проверяется [Tan] через [Sin] и cos. Эталон узлов — [StubTables.Tan.TABLE] при табличных стабах.
 */
@DisplayName("Tan: через sin и cos")
class TanTest {

    private val cosMod = StubTables.Cos.module
    private val sinMod = Sin(cosMod)
    private val tanOverStub = Tan(sinMod, cosMod)

    private val cosReal = Cos(TEST_EPS)
    private val sinReal = Sin(cosReal)
    private val tanOverReal = Tan(sinReal, cosReal)

    companion object {
        @JvmStatic
        fun tanReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Tan.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения tan по эталону StubTables.Tan.TABLE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("tanReferenceRows")
    fun `tan matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, tanOverStub.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("Для NaN и бесконечностей возвращается null (реальный Cos)")
    @ParameterizedTest(name = "аргумент: {0}")
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `non finite x returns null`(x: Double) {
        assertNull(tanOverReal.compute(x))
    }

    @Test
    @DisplayName("tan(π/2): cos=0 в стабе — null")
    fun `tan pole at pi half`() {
        assertNull(tanOverStub.compute(PI / 2))
    }

    @Test
    @DisplayName("tan: null, если sin вернул null")
    fun `tan null when sin null`() {
        val sin = mock<FunctionModule>()
        val cos = mock<FunctionModule>()
        whenever(sin.moduleId).thenReturn("sin")
        whenever(cos.moduleId).thenReturn("cos")
        whenever(sin.compute(any())).thenReturn(null)
        whenever(cos.compute(any())).thenReturn(1.0)
        assertNull(Tan(sin, cos).compute(0.5))
    }

    @Test
    @DisplayName("tan: null, если cos вернул null")
    fun `tan null when cos null`() {
        val sin = mock<FunctionModule>()
        val cos = mock<FunctionModule>()
        whenever(sin.moduleId).thenReturn("sin")
        whenever(cos.moduleId).thenReturn("cos")
        whenever(sin.compute(any())).thenReturn(0.5)
        whenever(cos.compute(any())).thenReturn(null)
        assertNull(Tan(sin, cos).compute(0.5))
    }
}
