package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.functions.core.Ln
import com.arekalov.tpolab2.functions.log.LogBase
import com.arekalov.tpolab2.functions.trig.Csc
import com.arekalov.tpolab2.functions.trig.Sec
import com.arekalov.tpolab2.functions.trig.Sin
import com.arekalov.tpolab2.functions.trig.Tan
import com.arekalov.tpolab2.testutil.StubTables
import com.arekalov.tpolab2.testutil.StubTables.Log2
import com.arekalov.tpolab2.testutil.StubTables.LogBranch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.stream.Stream
import kotlin.math.ln

@DisplayName("SystemFunction: интеграционные тесты")
class SystemFunctionTest {
    // LogBranch
    val lnStub = StubTables.Ln.module

    val log2Stub = StubTables.Log2.module
    val log3Stub = StubTables.Log3.module
    val log10Stub = StubTables.Log10.module

    val logBranchStub = StubTables.LogBranch.module


    // TrigBranch
    val cosStub = StubTables.Cos.module

    val sinStub = StubTables.Sin.module
    val secStub = StubTables.Sec.module

    val cscStub = StubTables.Csc.module
    val tanStub = StubTables.Tan.module

    val trigBranchStub = StubTables.TrigBranch.module


    companion object {
        @JvmStatic
        fun systemTableRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.System.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("0 уровень интеграции: система реальная, остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `0 level`(x: Double, expected: Double?) {
        val system = SystemFunction(trigBranch = trigBranchStub, logBranch = logBranchStub)
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    // Trig branch
    @DisplayName("1 уровень интеграции: система, trig ветка реальные, остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `1 level trig`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = TrigSystemBranch(
                sec = secStub,
                cos = cosStub,
                tan = tanStub,
                csc = cscStub,
                sin = sinStub,
            ),
            logBranch = logBranchStub,
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("1 уровень интеграции: система, trig и log ветки реальные, остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `1 level trig, log`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = TrigSystemBranch(
                sec = secStub,
                cos = cosStub,
                tan = tanStub,
                csc = cscStub,
                sin = sinStub,
            ),
            logBranch = LogSystemBranch(
                log2 = log2Stub,
                log3 = log3Stub,
                log10 = log10Stub,
                ln = lnStub,
            )
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("2 уровень интеграции: система, trigBranch, csc, tan  остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `2 level trigBranch, csc, tan`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = TrigSystemBranch(
                sec = secStub,
                cos = cosStub,
                tan = Tan(sin = sinStub, cos = cosStub),
                csc = Csc(sin = sinStub),
                sin = sinStub,
            ),
            logBranch = logBranchStub,
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("3 уровень интеграции: система, trigBranch, csc, tan, sec, sin  остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `3 level trigBranch, csc, tan, sec, sin`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = TrigSystemBranch(
                sec = Sec(cos = cosStub),
                cos = cosStub,
                tan = Tan(sin = sinStub, cos = cosStub),
                csc = Csc(sin = sinStub),
                sin = Sin(cos = cosStub),
            ),
            logBranch = logBranchStub,
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("4 уровень интеграции: вся trigBranch ветка реальная")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `4 level trigBranch, csc, tan, sec, sin ,cos`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = TrigSystemBranch(
                sec = Sec(cos = cosStub),
                cos = Cos(epsilon = TEST_EPS),
                tan = Tan(sin = sinStub, cos = cosStub),
                csc = Csc(sin = sinStub),
                sin = Sin(cos = cosStub),
            ),
            logBranch = logBranchStub,
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    // Log branch
    @DisplayName("1 уровень интеграции: система, log ветка реальные, остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `1 level log`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = trigBranchStub,
            logBranch = LogSystemBranch(
                log2 = log2Stub,
                log3 = log3Stub,
                log10 = log10Stub,
                ln = lnStub,
            )
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("2 уровень интеграции: система, log ветка, log2, log3, log10 реальные, остальное - моки")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `2 level log branch`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = trigBranchStub,
            logBranch = LogSystemBranch(
                log2 = LogBase(ln = lnStub, base = 2.0, moduleId = "log2"),
                log3 = LogBase(ln = lnStub, base = 3.0, moduleId = "log3"),
                log10 = LogBase(ln = lnStub, base = 10.0, moduleId = "log10"),
                ln = lnStub,
            )
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("3 уровень интеграции: вся log ветка реальная")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `3 level log branch`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = trigBranchStub,
            logBranch = LogSystemBranch(
                log2 = LogBase(ln = lnStub, base = 2.0, moduleId = "log2"),
                log3 = LogBase(ln = lnStub, base = 3.0, moduleId = "log3"),
                log10 = LogBase(ln = lnStub, base = 10.0, moduleId = "log10"),
                ln = Ln(epsilon = TEST_EPS),
            )
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }

    @DisplayName("Вся система реальная")
    @ParameterizedTest(name = "x = {0}, expected = {1}")
    @MethodSource("systemTableRows")
    fun `all system`(x: Double, expected: Double?) {
        val system = SystemFunction(
            trigBranch = TrigSystemBranch(
                sec = Sec(cos = cosStub),
                cos = Cos(epsilon = TEST_EPS),
                tan = Tan(sin = sinStub, cos = cosStub),
                csc = Csc(sin = sinStub),
                sin = Sin(cos = cosStub),
            ),
            logBranch = LogSystemBranch(
                log2 = LogBase(ln = lnStub, base = 2.0, moduleId = "log2"),
                log3 = LogBase(ln = lnStub, base = 3.0, moduleId = "log3"),
                log10 = LogBase(ln = lnStub, base = 10.0, moduleId = "log10"),
                ln = Ln(epsilon = TEST_EPS),
            )
        )
        val result = system.compute(x)
        if (expected == null) {
            assertNull(result)
        } else {
            assertEquals(expected, result!!, REF_TOLERANCE)
        }
    }


    @DisplayName("Неконечный x (NaN, ±∞): система возвращает null")
    @ParameterizedTest(name = "{0}")
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

}
