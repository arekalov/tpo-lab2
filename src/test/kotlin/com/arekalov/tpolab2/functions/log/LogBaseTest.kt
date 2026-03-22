package com.arekalov.tpolab2.functions.log

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@DisplayName("LogBase: инициализация и log_base(x), ln — мок")
class LogBaseTest {

    private val lnModule: FunctionModule = StubTables.Ln.module

    private fun assertLogBase(base: Double, x: Double) {
        val logB = LogBase(lnModule, base, "log${base.toInt()}")
        val value = StubTables.Ln.logBaseExpected(base, x)
        if (value == null) {
            assertNull(logB.compute(x))
        } else {
            assertEquals(value, logB.compute(x)!!, 1e-5)
        }
    }

    companion object {
        /** Узлы x — как в [StubTables.Log2.TABLE]; ожидание ln(x)/ln(2) по [StubTables.Ln.TABLE]. */
        @JvmStatic
        fun log2Nodes(): Stream<Arguments> =
            Stream.of(
                *StubTables.Log2.TABLE.keys
                    .sorted()
                    .map { x -> Arguments.of(x) }
                    .toTypedArray(),
            )

        /** Узлы x — как в [StubTables.Log10.TABLE]. */
        @JvmStatic
        fun log10Nodes(): Stream<Arguments> =
            Stream.of(
                *StubTables.Log10.TABLE.keys
                    .sorted()
                    .map { x -> Arguments.of(x) }
                    .toTypedArray(),
            )

        /** Узлы x — как в [StubTables.Log3.TABLE]. */
        @JvmStatic
        fun log3Nodes(): Stream<Arguments> =
            Stream.of(
                *StubTables.Log3.TABLE.keys
                    .sorted()
                    .map { x -> Arguments.of(x) }
                    .toTypedArray(),
            )
    }

    @DisplayName("log₂: все узлы сетки Log2 (x из StubTables.Log2.TABLE)")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("log2Nodes")
    fun `log2 matches ln ratio on Log2 grid`(x: Double) {
        assertLogBase(2.0, x)
    }

    @DisplayName("log₁₀: все узлы сетки Log10")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("log10Nodes")
    fun `log10 matches ln ratio on Log10 grid`(x: Double) {
        assertLogBase(10.0, x)
    }

    @DisplayName("log₃: все узлы сетки Log3")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("log3Nodes")
    fun `log3 matches ln ratio on Log3 grid`(x: Double) {
        assertLogBase(3.0, x)
    }

    @Test
    @DisplayName("При невычислимом ln(x) (например x = 0) возвращается null")
    fun `undefined when ln x undefined`() {
        val log2 = LogBase(lnModule, 2.0, "log2")
        assertNull(log2.compute(0.0))
    }

    @Test
    @DisplayName("Конструктор отклоняет недопустимое основание (1 или отрицательное)")
    fun `init rejects bad base`() {
        assertThrows<IllegalArgumentException> { LogBase(lnModule, 1.0, "log1") }
        assertThrows<IllegalArgumentException> { LogBase(lnModule, -2.0, "log-2") }
    }

    @Test
    @DisplayName("Инициализация падает, если ln не вычисляет значение на основании")
    fun `init fails when ln base is null`() {
        val badLn = mock<FunctionModule>()
        whenever(badLn.moduleId).thenReturn("badLn")
        whenever(badLn.compute(any())).thenReturn(null)
        assertThrows<IllegalArgumentException> { LogBase(badLn, 2.0, "log2") }
    }

    @Test
    @DisplayName("Инициализация падает, если ln(основание) = 0")
    fun `init fails when ln of base is zero`() {
        val badLn = mock<FunctionModule>()
        whenever(badLn.moduleId).thenReturn("badLn")
        whenever(badLn.compute(any())).thenAnswer { inv ->
            if (inv.getArgument<Double>(0) == 2.0) 0.0 else 1.0
        }
        assertThrows<IllegalArgumentException> { LogBase(badLn, 2.0, "log2") }
    }
}
