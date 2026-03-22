package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
import com.arekalov.tpolab2.testutil.StubTables.Log10
import com.arekalov.tpolab2.testutil.StubTables.Log2
import com.arekalov.tpolab2.testutil.StubTables.Log3
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.jvm.JvmStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@DisplayName("LogSystemBranch: формула ветки, знаменатель log3")
class LogSystemBranchTest {
    val l2 = Log2.module
    val l10 = Log10.module
    val l3 = Log3.module
    val ln = StubTables.Ln.module

    val logBranch = LogSystemBranch(l2, l10, l3, ln)

    companion object {
        /** Строки [StubTables.LogBranch.TABLE]: `expected == null` — ждём `null` от ветки (JUnit не подставляет null в `double`). */
        @JvmStatic
        fun logBranchTableRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.LogBranch.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Ветка vs [StubTables.LogBranch.TABLE] (null — не определено)")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("logBranchTableRows")
    fun `branch formula matches tables on grid`(x: Double, expected: Double?) {
        val actual = logBranch.compute(x)
        if (expected == null) {
            assertNull(actual)
        } else {
            assertEquals(expected, actual!!, REF_TOLERANCE)
        }
    }

    @Test
    @DisplayName("Если любой из модулей возвращает null, ветка возвращает null")
    fun `undefined when any dependency null`() {
        val x = 2.6
        val bad = StubTables.AlwaysNull.module
        assertNull(LogSystemBranch(bad, StubTables.Log10.module, StubTables.Log3.module, StubTables.Ln.module).compute(x))
        assertNull(LogSystemBranch(StubTables.Log2.module, bad, StubTables.Log3.module, StubTables.Ln.module).compute(x))
        assertNull(LogSystemBranch(StubTables.Log2.module, StubTables.Log10.module, bad, StubTables.Ln.module).compute(x))
        assertNull(LogSystemBranch(StubTables.Log2.module, StubTables.Log10.module, StubTables.Log3.module, bad).compute(x))
    }

    @Test
    @DisplayName("null при втором вызове log2.compute")
    fun `null when second log2 call returns null`() {
        val log2 = mock<FunctionModule>()
        whenever(log2.moduleId).thenReturn("log2")
        whenever(log2.compute(any())).thenReturn(1.0).thenReturn(null)
        val one = mockOne("m")
        assertNull(LogSystemBranch(log2, one, one, one).compute(1.0))
    }

    @Test
    @DisplayName("null при третьем вызове log2.compute")
    fun `null when third log2 call returns null`() {
        val log2 = mock<FunctionModule>()
        whenever(log2.moduleId).thenReturn("log2")
        whenever(log2.compute(any())).thenReturn(1.0).thenReturn(1.0).thenReturn(null)
        val one = mockOne("m")
        assertNull(LogSystemBranch(log2, one, one, one).compute(1.0))
    }

    @Test
    @DisplayName("null при первом вызове log3.compute (l3a)")
    fun `null when first log3 call returns null`() {
        val log2 = mockOne("log2")
        val log10 = mockOne("log10")
        val log3 = mock<FunctionModule>()
        whenever(log3.moduleId).thenReturn("log3")
        whenever(log3.compute(any())).thenReturn(null)
        val ln = mockOne("ln")
        assertNull(LogSystemBranch(log2, log10, log3, ln).compute(1.0))
    }

    @Test
    @DisplayName("null при втором вызове log3.compute (l3b)")
    fun `null when second log3 call returns null`() {
        val log2 = mockOne("log2")
        val log10 = mockOne("log10")
        val log3 = mock<FunctionModule>()
        whenever(log3.moduleId).thenReturn("log3")
        whenever(log3.compute(any())).thenReturn(1.0).thenReturn(null)
        val ln = mockOne("ln")
        assertNull(LogSystemBranch(log2, log10, log3, ln).compute(1.0))
    }

    @Test
    @DisplayName("null при третьем вызове log3.compute (l3den)")
    fun `null when third log3 call returns null`() {
        val log2 = mockOne("log2")
        val log10 = mockOne("log10")
        val log3 = mock<FunctionModule>()
        whenever(log3.moduleId).thenReturn("log3")
        whenever(log3.compute(any())).thenReturn(1.0).thenReturn(1.0).thenReturn(null)
        val ln = mockOne("ln")
        assertNull(LogSystemBranch(log2, log10, log3, ln).compute(1.0))
    }

    @Test
    @DisplayName("знаменатель log3 = 0: null (явный мок третьего вызова)")
    fun `null when log3 denominator is zero via mock`() {
        val log2 = mockOne("log2")
        val log10 = mockOne("log10")
        val log3 = mock<FunctionModule>()
        whenever(log3.moduleId).thenReturn("log3")
        whenever(log3.compute(any())).thenReturn(1.0).thenReturn(1.0).thenReturn(0.0)
        val ln = mockOne("ln")
        assertNull(LogSystemBranch(log2, log10, log3, ln).compute(1.0))
    }

    private fun mockOne(id: String): FunctionModule {
        val m = mock<FunctionModule>()
        whenever(m.moduleId).thenReturn(id)
        whenever(m.compute(any())).thenReturn(1.0)
        return m
    }
}
