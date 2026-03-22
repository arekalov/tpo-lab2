package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
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

/** Зависимость cos — [StubTables.Cos.module]; эталоны — [StubTables.Cos.Derived]. */
@DisplayName("Sin / Sec / Tan / Csc через табличный стаб cos")
class DerivedTrigTest {

    private val cosMod = StubTables.Cos.module
    private val sinMod = Sin(cosMod)
    private val secMod = Sec(cosMod)
    private val tanMod = Tan(sinMod, cosMod)
    private val cscMod = Csc(sinMod)

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

    @DisplayName("sin для NaN и бесконечности возвращает null")
    @ParameterizedTest(name = "аргумент: {0}")
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY])
    fun `sin rejects non finite`(x: Double) {
        assertNull(sinMod.compute(x))
    }
}
