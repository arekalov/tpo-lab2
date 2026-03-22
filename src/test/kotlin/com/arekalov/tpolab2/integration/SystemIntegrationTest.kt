package com.arekalov.tpolab2.integration

import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.functions.core.Ln
import com.arekalov.tpolab2.functions.log.LogBase
import com.arekalov.tpolab2.functions.trig.Csc
import com.arekalov.tpolab2.functions.trig.Sec
import com.arekalov.tpolab2.functions.trig.Sin
import com.arekalov.tpolab2.functions.trig.Tan
import com.arekalov.tpolab2.system.LogSystemBranch
import com.arekalov.tpolab2.system.SystemFunction
import com.arekalov.tpolab2.system.TrigSystemBranch
import com.arekalov.tpolab2.system.wireModules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Значения для Mockito — фиксированные «с графика» (табличные), не из kotlin.math в thenReturn.
 */
@DisplayName("Интеграция: Mockito, подстановка реальных cos/ln")
@ExtendWith(MockitoExtension::class)
class SystemIntegrationTest {

    @Mock
    lateinit var mockSec: FunctionModule

    @Mock
    lateinit var mockSin: FunctionModule

    @Mock
    lateinit var mockCos: FunctionModule

    @Mock
    lateinit var mockCsc: FunctionModule

    @Mock
    lateinit var mockTan: FunctionModule

    @Mock
    lateinit var mockLog2: FunctionModule

    @Mock
    lateinit var mockLog10: FunctionModule

    @Mock
    lateinit var mockLog3: FunctionModule

    @Mock
    lateinit var mockLn: FunctionModule

    @Test
    fun `trig branch all mocks verifies calls and result`() {
        val x0 = -1.0
        whenever(mockSec.compute(eq(x0))).thenReturn(1.2)
        whenever(mockSin.compute(eq(x0))).thenReturn(0.3)
        whenever(mockCos.compute(eq(x0))).thenReturn(0.4)
        whenever(mockCsc.compute(eq(x0))).thenReturn(2.0)
        whenever(mockTan.compute(eq(x0))).thenReturn(0.5)

        val branch = TrigSystemBranch(mockSec, mockSin, mockCos, mockCsc, mockTan)
        val inner = (1.2 - 1.2) + 1.2 * 0.3
        val expected = (inner * 0.4 - 0.3 * 2.0) / 0.5
        assertEquals(expected, branch.compute(x0)!!, 1e-12)

        verify(mockSec, times(3)).compute(eq(x0))
        verify(mockSin, times(2)).compute(eq(x0))
        verify(mockCos, times(1)).compute(eq(x0))
        verify(mockCsc, times(1)).compute(eq(x0))
        verify(mockTan, times(1)).compute(eq(x0))
    }

    @Test
    fun `log branch all mocks verifies calls`() {
        val x0 = 2.0
        whenever(mockLog2.compute(eq(x0))).thenReturn(1.0)
        whenever(mockLog10.compute(eq(x0))).thenReturn(2.0)
        whenever(mockLog3.compute(eq(x0))).thenReturn(0.5)
        whenever(mockLn.compute(eq(x0))).thenReturn(1.0)

        val branch = LogSystemBranch(mockLog2, mockLog10, mockLog3, mockLn)
        val inner = (1.0 - 1.0) + 1.0 * 2.0
        val expected = (inner * 0.5 - 0.5 * 1.0) / 0.5
        assertEquals(expected, branch.compute(x0)!!, 1e-12)

        verify(mockLog2, times(3)).compute(eq(x0))
        verify(mockLog10, times(1)).compute(eq(x0))
        verify(mockLog3, times(3)).compute(eq(x0))
        verify(mockLn, times(1)).compute(eq(x0))
    }

    @Test
    fun `trig with real cos only mocks unchanged formula`() {
        val x0 = -0.35
        val realCos = Cos(TEST_EPS)
        val cosAt = realCos.compute(x0)!!
        /* табличные значения согласованы с реальным cos(x0) для проверки подстановки одного модуля */
        whenever(mockSec.compute(eq(x0))).thenReturn(1.0 / cosAt)
        whenever(mockSin.compute(eq(x0))).thenReturn(0.41)
        whenever(mockCsc.compute(eq(x0))).thenReturn(2.44)
        whenever(mockTan.compute(eq(x0))).thenReturn(0.43)

        val branch = TrigSystemBranch(mockSec, mockSin, realCos, mockCsc, mockTan)
        val secV = 1.0 / cosAt
        val sinV = 0.41
        val inner = (secV - secV) + secV * sinV
        val numLeft = inner * cosAt
        val numerator = numLeft - sinV * 2.44
        val expected = numerator / 0.43
        assertEquals(expected, branch.compute(x0)!!, 1e-9)
    }

    @Test
    fun `log with real ln only`() {
        val x0 = 2.5
        val realLn = Ln(TEST_EPS)
        val lnAt = realLn.compute(x0)!!
        whenever(mockLog2.compute(eq(x0))).thenReturn(1.32)
        whenever(mockLog10.compute(eq(x0))).thenReturn(0.40)
        whenever(mockLog3.compute(eq(x0))).thenReturn(0.83)

        val branch = LogSystemBranch(mockLog2, mockLog10, mockLog3, realLn)
        val l2 = 1.32
        val inner = (l2 - l2) + l2 * 0.40
        val l3 = 0.83
        val expected = (inner * l3 - l3 * lnAt) / l3
        assertEquals(expected, branch.compute(x0)!!, 1e-9)
    }

    @Test
    fun `full real wiring equals incremental real stack for trig`() {
        val w = wireModules(TEST_EPS)
        val x = -0.55
        val built = TrigSystemBranch(
            Sec(w.cos),
            Sin(w.cos),
            w.cos,
            Csc(Sin(w.cos)),
            Tan(Sin(w.cos), w.cos),
        )
        assertEquals(w.trigBranch.compute(x)!!, built.compute(x)!!, 1e-8)
    }

    @Test
    fun `full real wiring equals incremental real stack for log`() {
        val w = wireModules(TEST_EPS)
        val x = 3.3
        val ln = w.ln
        val built = LogSystemBranch(
            LogBase(ln, 2.0, "log2"),
            LogBase(ln, 10.0, "log10"),
            LogBase(ln, 3.0, "log3"),
            ln,
        )
        assertEquals(w.logBranch.compute(x)!!, built.compute(x)!!, 1e-8)
    }

    @Test
    fun `piecewise system uses both branches like wired`() {
        val w = wireModules(TEST_EPS)
        val sys = SystemFunction(w.trigBranch, w.logBranch)
        assertEquals(w.trigBranch.compute(-0.2), sys.compute(-0.2))
        assertEquals(w.logBranch.compute(4.0), sys.compute(4.0))
    }
}
