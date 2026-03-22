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
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.stream.Stream

@DisplayName("TrigSystemBranch: формула ветки, особые точки")
class TrigSystemBranchTest {

    val branch = TrigSystemBranch(
        StubTables.Sec.module,
        StubTables.Sin.module,
        StubTables.Cos.module,
        StubTables.Csc.module,
        StubTables.Tan.module,
    )

    companion object {
        /** Строки [StubTables.LogBranch.TABLE]: `expected == null` — ждём `null` от ветки (JUnit не подставляет null в `double`). */
        @JvmStatic
        fun trigBranchTableRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.TrigBranch.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }


    @DisplayName("Параметризованные тесты на ветку trig")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("trigBranchTableRows")
    fun `hand stub values at x0`(x: Double, expected: Double?) {
        if (expected == null) {
            assertNull(branch.compute(x))
        } else {
            assertEquals(expected, branch.compute(x)!!, REF_TOLERANCE)
        }
    }

    @Test
    @DisplayName("Если tan(x) = 0 (знаменатель), результат не определён — null")
    fun `undefined when tan is zero`() {
        val x = -0.5
        val tanZero = mock<FunctionModule>()
        whenever(tanZero.moduleId).thenReturn("tan")
        whenever(tanZero.compute(eq(x))).thenReturn(0.0)
        val branch = TrigSystemBranch(
            StubTables.Sec.module,
            StubTables.Sin.module,
            StubTables.Cos.module,
            StubTables.Csc.module,
            tanZero,
        )
        assertNull(branch.compute(x))
    }

    @Test
    @DisplayName("Если любой из модулей возвращает null, ветка возвращает null")
    fun `undefined when any dependency null`() {
        val x = -0.2
        val okSec = StubTables.Sec.module
        val okSin = StubTables.Sin.module
        val okCos = StubTables.Cos.module
        val okCsc = StubTables.Csc.module
        val okTan = StubTables.Tan.module
        val bad = StubTables.AlwaysNull.module
        assertNull(TrigSystemBranch(bad, okSin, okCos, okCsc, okTan).compute(x))
        assertNull(TrigSystemBranch(okSec, bad, okCos, okCsc, okTan).compute(x))
        assertNull(TrigSystemBranch(okSec, okSin, bad, okCsc, okTan).compute(x))
        assertNull(TrigSystemBranch(okSec, okSin, okCos, bad, okTan).compute(x))
        assertNull(TrigSystemBranch(okSec, okSin, okCos, okCsc, bad).compute(x))
    }

    @Test
    @DisplayName("null при втором вызове sec.compute")
    fun `null when second sec call returns null`() {
        val sec = mock<FunctionModule>()
        whenever(sec.moduleId).thenReturn("sec")
        whenever(sec.compute(any())).thenReturn(1.0).thenReturn(null)
        val stub = StubTables.Sin.module
        assertNull(TrigSystemBranch(sec, stub, stub, stub, stub).compute(0.0))
    }

    @Test
    @DisplayName("null при третьем вызове sec.compute")
    fun `null when third sec call returns null`() {
        val sec = mock<FunctionModule>()
        whenever(sec.moduleId).thenReturn("sec")
        whenever(sec.compute(any())).thenReturn(1.0).thenReturn(1.0).thenReturn(null)
        val stub = StubTables.Sin.module
        assertNull(TrigSystemBranch(sec, stub, stub, stub, stub).compute(0.0))
    }

    @Test
    @DisplayName("null при втором вызове sin.compute")
    fun `null when second sin call returns null`() {
        val sec = mock<FunctionModule>()
        val sin = mock<FunctionModule>()
        val cos = mock<FunctionModule>()
        whenever(sec.moduleId).thenReturn("sec")
        whenever(sin.moduleId).thenReturn("sin")
        whenever(cos.moduleId).thenReturn("cos")
        whenever(sec.compute(any())).thenReturn(1.0)
        whenever(sin.compute(any())).thenReturn(1.0).thenReturn(null)
        whenever(cos.compute(any())).thenReturn(1.0)
        val unused = mock<FunctionModule>()
        assertNull(TrigSystemBranch(sec, sin, cos, unused, unused).compute(0.0))
    }
}
