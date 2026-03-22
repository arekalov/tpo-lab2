package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.testutil.StubTables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

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
}
