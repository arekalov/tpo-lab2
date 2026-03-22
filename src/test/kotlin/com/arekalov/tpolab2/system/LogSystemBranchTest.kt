package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@DisplayName("LogSystemBranch: формула ветки, знаменатель log3")
class LogSystemBranchTest {

    @Test
    @DisplayName("Формула ветки на ручных значениях табличных заглушек в одной точке")
    fun `hand stub values at x0`() {
        val x = 2.0
        val branch = LogSystemBranch(
            StubTables.Log2.module,
            StubTables.Log10.module,
            StubTables.Log3.module,
            StubTables.Ln.module,
        )
        val l2 = StubTables.Log2.TABLE.getValue(x)
        val l10 = StubTables.Log10.TABLE.getValue(x)
        val l3 = StubTables.Log3.TABLE.getValue(x)
        val ln = StubTables.Ln.TABLE.getValue(x)!!
        val inner = (l2 - l2) + l2 * l10
        val expected = (inner * l3 - l3 * ln) / l3
        assertEquals(expected, branch.compute(x)!!, 1e-12)
    }

    @Test
    @DisplayName("Если log3(x) = 0 (знаменатель), результат не определён — null")
    fun `undefined when log3 denominator zero`() {
        val x = 3.0
        val branch = LogSystemBranch(
            StubTables.Log2.module,
            StubTables.Log10.module,
            StubTables.Log3.module,
            StubTables.Ln.module,
        )
        assertNull(branch.compute(x))
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
