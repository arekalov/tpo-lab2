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

@DisplayName("TrigSystemBranch: формула ветки, особые точки")
class TrigSystemBranchTest {

    @Test
    fun `hand stub values at x0`() {
        val x0 = -1.0
        val sec = mockReturningAt("sec", x0, 1.2)
        val sin = mockReturningAt("sin", x0, 0.3)
        val cos = mockReturningAt("cos", x0, 0.4)
        val csc = mockReturningAt("csc", x0, 2.0)
        val tan = mockReturningAt("tan", x0, 0.5)
        val branch = TrigSystemBranch(sec, sin, cos, csc, tan)
        val inner = (1.2 - 1.2) + 1.2 * 0.3
        val numLeft = inner * 0.4
        val numerator = numLeft - 0.3 * 2.0
        val expected = numerator / 0.5
        assertEquals(expected, branch.compute(x0)!!, 1e-12)
    }

    @Test
    fun `undefined when tan is zero`() {
        val x0 = -0.5
        val stub = mockReturningAt("stub", x0, 1.0)
        val zeroTan = mock<FunctionModule>()
        whenever(zeroTan.moduleId).thenReturn("zeroTan")
        whenever(zeroTan.compute(any())).thenAnswer { inv ->
            if (inv.getArgument<Double>(0) == x0) 0.0 else null
        }
        val branch = TrigSystemBranch(stub, stub, stub, stub, zeroTan)
        assertNull(branch.compute(x0))
    }

    @Test
    fun `undefined when any dependency null`() {
        val x0 = -0.2
        val ok = mockReturningAt("ok", x0, 1.0)
        val bad = mock<FunctionModule>()
        whenever(bad.moduleId).thenReturn("bad")
        whenever(bad.compute(any())).thenReturn(null)
        assertNull(TrigSystemBranch(bad, ok, ok, ok, ok).compute(x0))
        assertNull(TrigSystemBranch(ok, bad, ok, ok, ok).compute(x0))
        assertNull(TrigSystemBranch(ok, ok, bad, ok, ok).compute(x0))
        assertNull(TrigSystemBranch(ok, ok, ok, bad, ok).compute(x0))
        assertNull(TrigSystemBranch(ok, ok, ok, ok, bad).compute(x0))
    }
}
