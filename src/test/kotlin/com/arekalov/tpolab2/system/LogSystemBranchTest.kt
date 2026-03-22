package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.mockReturningAt
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
    fun `hand stub values at x0`() {
        val x0 = 2.0
        val log2 = mockReturningAt("log2", x0, 1.0)
        val log10 = mockReturningAt("log10", x0, 2.0)
        val log3 = mockReturningAt("log3", x0, 0.5)
        val ln = mockReturningAt("ln", x0, 1.0)
        val branch = LogSystemBranch(log2, log10, log3, ln)
        val inner = (1.0 - 1.0) + 1.0 * 2.0
        val left = inner * 0.5
        val numerator = left - 0.5 * 1.0
        val expected = numerator / 0.5
        assertEquals(expected, branch.compute(x0)!!, 1e-12)
    }

    @Test
    fun `undefined when log3 denominator zero`() {
        val x0 = 3.0
        val stub = mockReturningAt("stub", x0, 1.0)
        val zeroLog3 = mock<FunctionModule>()
        whenever(zeroLog3.moduleId).thenReturn("zeroLog3")
        whenever(zeroLog3.compute(any())).thenAnswer { inv ->
            if (inv.getArgument<Double>(0) == x0) 0.0 else null
        }
        val branch = LogSystemBranch(stub, stub, zeroLog3, stub)
        assertNull(branch.compute(x0))
    }

    @Test
    fun `undefined when any dependency null`() {
        val x0 = 2.5
        val ok = mockReturningAt("ok", x0, 1.0)
        val bad = mock<FunctionModule>()
        whenever(bad.moduleId).thenReturn("bad")
        whenever(bad.compute(any())).thenReturn(null)
        assertNull(LogSystemBranch(bad, ok, ok, ok).compute(x0))
        assertNull(LogSystemBranch(ok, bad, ok, ok).compute(x0))
        assertNull(LogSystemBranch(ok, ok, bad, ok).compute(x0))
        assertNull(LogSystemBranch(ok, ok, ok, bad).compute(x0))
    }
}
