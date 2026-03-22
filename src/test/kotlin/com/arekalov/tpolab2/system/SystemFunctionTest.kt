package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@DisplayName("SystemFunction: кусочное ветвление и NaN")
class SystemFunctionTest {

    @Test
    fun `routes to trig for non positive x`() {
        val trig = mock<FunctionModule>()
        val log = mock<FunctionModule>()
        whenever(trig.moduleId).thenReturn("trig")
        whenever(log.moduleId).thenReturn("log")
        whenever(trig.compute(any())).thenAnswer { inv ->
            if (inv.getArgument<Double>(0) <= 0.0) 11.0 else null
        }
        whenever(log.compute(any())).thenReturn(22.0)
        val sys = SystemFunction(trig, log)
        assertEquals(11.0, sys.compute(0.0))
        assertEquals(11.0, sys.compute(-2.0))
    }

    @Test
    fun `routes to log for positive x`() {
        val trig = mock<FunctionModule>()
        val log = mock<FunctionModule>()
        whenever(trig.moduleId).thenReturn("trig")
        whenever(log.moduleId).thenReturn("log")
        whenever(trig.compute(any())).thenReturn(11.0)
        whenever(log.compute(any())).thenReturn(22.0)
        val sys = SystemFunction(trig, log)
        assertEquals(22.0, sys.compute(0.5))
    }

    @ParameterizedTest
    @CsvSource("NaN", "Infinity", "-Infinity")
    fun `non finite x returns null`(token: String) {
        val x = when (token) {
            "NaN" -> Double.NaN
            "Infinity" -> Double.POSITIVE_INFINITY
            else -> Double.NEGATIVE_INFINITY
        }
        val a = mock<FunctionModule>()
        val b = mock<FunctionModule>()
        whenever(a.moduleId).thenReturn("a")
        whenever(b.moduleId).thenReturn("b")
        val sys = SystemFunction(a, b)
        assertNull(sys.compute(x))
    }

    @Test
    fun `branch null propagates`() {
        val a = mock<FunctionModule>()
        val b = mock<FunctionModule>()
        whenever(a.moduleId).thenReturn("a")
        whenever(b.moduleId).thenReturn("b")
        whenever(a.compute(any())).thenReturn(null)
        whenever(b.compute(any())).thenReturn(1.0)
        val sys = SystemFunction(a, b)
        assertNull(sys.compute(-1.0))
    }
}
