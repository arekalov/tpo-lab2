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
 * Табличный стаб [StubTables.Cos.module] и эталоны [StubTables.Cos.Derived];
 * плюс null-ветки: не-конечный x (реальный [Cos]), полюса, моки с `null` от зависимости.
 */
@DisplayName("Sin / Sec / Tan / Csc")
class DerivedTrigTest {

    private val cosMod = StubTables.Cos.module
    private val sinMod = Sin(cosMod)
    private val secMod = Sec(cosMod)
    private val tanMod = Tan(sinMod, cosMod)
    private val cscMod = Csc(sinMod)

    private val cosReal = Cos(TEST_EPS)
    private val sinReal = Sin(cosReal)
    private val secReal = Sec(cosReal)
    private val tanReal = Tan(sinReal, cosReal)
    private val cscReal = Csc(sinReal)

    companion object {
        @JvmStatic
        fun sinStubRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Cos.Derived.sinPairs
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )

        @JvmStatic
        fun secStubRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Cos.Derived.secPairs
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )

        @JvmStatic
        fun tanStubRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Cos.Derived.tanPairs
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )

        @JvmStatic
        fun cscStubRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Cos.Derived.cscPairs
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("sin: совпадение с эталоном по узлам [StubTables.Cos.TABLE]")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("sinStubRows")
    fun `sin matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, sinMod.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("sec: совпадение с эталоном по узлам [StubTables.Cos.TABLE]")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("secStubRows")
    fun `sec matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, secMod.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("tan: совпадение с эталоном по узлам [StubTables.Cos.TABLE]")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("tanStubRows")
    fun `tan matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, tanMod.compute(x)!!, REF_TOLERANCE)
    }

    @Test
    @DisplayName("sec(π/2): cos=0 в стабе — null")
    fun `sec pole at pi half`() {
        assertNull(secMod.compute(PI / 2))
    }

    @Test
    @DisplayName("tan(π/2): cos=0 в стабе — null")
    fun `tan pole at pi half`() {
        assertNull(tanMod.compute(PI / 2))
    }

    @Test
    @DisplayName("csc(0): полюс, возвращается null")
    fun `csc pole at zero`() {
        assertNull(cscMod.compute(0.0))
    }

    @DisplayName("csc: совпадение с эталоном (узлы, где sin ≠ 0)")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("cscStubRows")
    fun `csc matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, cscMod.compute(x)!!, REF_TOLERANCE)
    }

    @DisplayName("NaN и ±∞: sin / sec / tan / csc с реальным Cos (не через табличный стаб)")
    @ParameterizedTest(name = "x = {0}")
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `non finite x returns null with real cos chain`(x: Double) {
        assertNull(sinReal.compute(x))
        assertNull(secReal.compute(x))
        assertNull(tanReal.compute(x))
        assertNull(cscReal.compute(x))
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
    @DisplayName("sec: null, если cos вернул null")
    fun `sec null when cos null`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(null)
        assertNull(Sec(cos).compute(1.0))
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

    @Test
    @DisplayName("csc: null, если sin вернул null")
    fun `csc null when sin null`() {
        val sin = mock<FunctionModule>()
        whenever(sin.moduleId).thenReturn("sin")
        whenever(sin.compute(any())).thenReturn(null)
        assertNull(Csc(sin).compute(1.0))
    }
}
