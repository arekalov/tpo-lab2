package com.arekalov.tpolab2.functions.log

import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Ln
import kotlin.math.ln
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@DisplayName("LogBase: инициализация и log_base(x)")
class LogBaseTest {

    private val lnModule = Ln(TEST_EPS)

    @ParameterizedTest
    @CsvSource(
        "2, 8, 3",
        "3, 9, 2",
        "10, 100, 2",
    )
    fun `log base identity`(baseStr: String, xStr: String, expectedStr: String) {
        val base = baseStr.toDouble()
        val x = xStr.toDouble()
        val expected = expectedStr.toDouble()
        val logB = LogBase(lnModule, base, "log${base.toInt()}")
        assertEquals(expected, logB.compute(x)!!, 1e-5)
    }

    @Test
    fun `undefined when ln x undefined`() {
        val log2 = LogBase(lnModule, 2.0, "log2")
        assertNull(log2.compute(0.0))
    }

    @Test
    fun `init rejects bad base`() {
        assertThrows<IllegalArgumentException> { LogBase(lnModule, 1.0, "log1") }
        assertThrows<IllegalArgumentException> { LogBase(lnModule, -2.0, "log-2") }
    }

    @Test
    fun `init fails when ln base is null`() {
        val badLn = mock<FunctionModule>()
        whenever(badLn.moduleId).thenReturn("badLn")
        whenever(badLn.compute(any())).thenReturn(null)
        assertThrows<IllegalArgumentException> { LogBase(badLn, 2.0, "log2") }
    }

    @Test
    fun `init fails when ln of base is zero`() {
        val badLn = mock<FunctionModule>()
        whenever(badLn.moduleId).thenReturn("badLn")
        whenever(badLn.compute(any())).thenAnswer { inv ->
            if (inv.getArgument<Double>(0) == 2.0) 0.0 else 1.0
        }
        assertThrows<IllegalArgumentException> { LogBase(badLn, 2.0, "log2") }
    }

    @Test
    fun `matches ln ratio`() {
        val x = 5.5
        val log3 = LogBase(lnModule, 3.0, "log3")
        assertEquals(ln(x) / ln(3.0), log3.compute(x)!!, 1e-5)
    }
}
