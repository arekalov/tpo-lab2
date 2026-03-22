package com.arekalov.tpolab2.functions.log

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
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
import java.util.stream.Stream

@DisplayName("LogBase: инициализация и log_base(x), ln — мок")
class LogBaseTest {

    private val lnModule: FunctionModule = StubTables.Ln.module


    @DisplayName("log_base(x): целые степени и log_b(1) = 0 (ожидание из таблицы ln-стаба)")
    @ParameterizedTest(name = "основание {0}, x = {1} → {2}")
    @MethodSource("com.arekalov.tpolab2.testutil.StubTables\$Sources#logBaseCases")
    fun `log base identity`(base: Double, x: Double, expected: Double) {
        val logB = LogBase(lnModule, base, "log${base.toInt()}")
        assertEquals(expected, logB.compute(x)!!, 1e-5)
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
