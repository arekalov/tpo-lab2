//package com.arekalov.tpolab2.integration
//
//import com.arekalov.tpolab2.REF_TOLERANCE
//import com.arekalov.tpolab2.TEST_EPS
//import com.arekalov.tpolab2.functions.log.LogBase
//import com.arekalov.tpolab2.functions.trig.Csc
//import com.arekalov.tpolab2.functions.trig.Sec
//import com.arekalov.tpolab2.functions.trig.Sin
//import com.arekalov.tpolab2.functions.trig.Tan
//import com.arekalov.tpolab2.system.LogSystemBranch
//import com.arekalov.tpolab2.system.SystemFunction
//import com.arekalov.tpolab2.system.TrigSystemBranch
//import com.arekalov.tpolab2.system.wireModules
//import com.arekalov.tpolab2.testutil.StubTables
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito.clearInvocations
//import org.mockito.kotlin.eq
//import org.mockito.kotlin.times
//import org.mockito.kotlin.verify
//
//@DisplayName("Интеграция: табличные стабы StubTables")
//class SystemIntegrationTest {
//
//    /** Общие моки из [StubTables] живут весь JVM; сброс счётчиков, иначе [verify] видит чужие тесты. */
//    @BeforeEach
//    fun clearSharedStubInvocations() {
//        clearInvocations(
//            StubTables.Sec.module,
//            StubTables.Sin.module,
//            StubTables.Cos.module,
//            StubTables.Csc.module,
//            StubTables.Tan.module,
//            StubTables.Log2.module,
//            StubTables.Log10.module,
//            StubTables.Log3.module,
//            StubTables.Ln.module,
//        )
//    }
//
//    @Test
//    @DisplayName("Тригонометрическая ветка: все моки, проверка результата и числа вызовов")
//    fun `trig branch all mocks verifies calls and result`() {
//        val x = -1.0
//        val branch = TrigSystemBranch(
//            StubTables.Sec.module,
//            StubTables.Sin.module,
//            StubTables.Cos.module,
//            StubTables.Csc.module,
//            StubTables.Tan.module,
//        )
//        val sec = StubTables.Sec.TABLE.getValue(x)
//        val sin = StubTables.Sin.TABLE.getValue(x)
//        val cos = StubTables.Cos.TABLE.getValue(x)
//        val csc = StubTables.Csc.TABLE.getValue(x)
//        val tan = StubTables.Tan.TABLE.getValue(x)
//        val inner = (sec - sec) + sec * sin
//        val expected = (inner * cos - sin * csc) / tan
//        assertEquals(expected, branch.compute(x)!!, REF_TOLERANCE)
//
//        verify(StubTables.Sec.module, times(3)).compute(eq(x))
//        verify(StubTables.Sin.module, times(2)).compute(eq(x))
//        verify(StubTables.Cos.module, times(1)).compute(eq(x))
//        verify(StubTables.Csc.module, times(1)).compute(eq(x))
//        verify(StubTables.Tan.module, times(1)).compute(eq(x))
//    }
//
//    @Test
//    @DisplayName("Логарифмическая ветка: все моки, проверка результата и числа вызовов")
//    fun `log branch all mocks verifies calls`() {
//        val x = 2.0
//        val branch = LogSystemBranch(
//            StubTables.Log2.module,
//            StubTables.Log10.module,
//            StubTables.Log3.module,
//            StubTables.Ln.module,
//        )
//        val l2 = StubTables.Log2.TABLE.getValue(x)
//        val l10 = StubTables.Log10.TABLE.getValue(x)
//        val l3 = StubTables.Log3.TABLE.getValue(x)
//        val ln = StubTables.Ln.TABLE.getValue(x)!!
//        val inner = (l2 - l2) + l2 * l10
//        val expected = (inner * l3 - l3 * ln) / l3
//        assertEquals(expected, branch.compute(x)!!, REF_TOLERANCE)
//
//        verify(StubTables.Log2.module, times(3)).compute(eq(x))
//        verify(StubTables.Log10.module, times(1)).compute(eq(x))
//        verify(StubTables.Log3.module, times(3)).compute(eq(x))
//        verify(StubTables.Ln.module, times(1)).compute(eq(x))
//    }
//
//    @Test
//    @DisplayName("Тригонометрическая ветка: табличный cos, остальные из тех же таблиц")
//    fun `trig with stub cos only mocks unchanged formula`() {
//        val x = -0.35
//        val branch = TrigSystemBranch(
//            StubTables.Sec.module,
//            StubTables.Sin.module,
//            StubTables.Cos.module,
//            StubTables.Csc.module,
//            StubTables.Tan.module,
//        )
//        val sec = StubTables.Sec.TABLE.getValue(x)
//        val sin = StubTables.Sin.TABLE.getValue(x)
//        val cos = StubTables.Cos.TABLE.getValue(x)
//        val csc = StubTables.Csc.TABLE.getValue(x)
//        val tan = StubTables.Tan.TABLE.getValue(x)
//        val inner = (sec - sec) + sec * sin
//        val expected = (inner * cos - sin * csc) / tan
//        assertEquals(expected, branch.compute(x)!!, 1e-9)
//    }
//
//    @Test
//    @DisplayName("Логарифмическая ветка: табличный ln, остальные из тех же таблиц")
//    fun `log with stub ln only`() {
//        val x = 2.5
//        val branch = LogSystemBranch(
//            StubTables.Log2.module,
//            StubTables.Log10.module,
//            StubTables.Log3.module,
//            StubTables.Ln.module,
//        )
//        val l2 = StubTables.Log2.TABLE.getValue(x)
//        val l10 = StubTables.Log10.TABLE.getValue(x)
//        val l3 = StubTables.Log3.TABLE.getValue(x)
//        val ln = StubTables.Ln.TABLE.getValue(x)!!
//        val inner = (l2 - l2) + l2 * l10
//        val expected = (inner * l3 - l3 * ln) / l3
//        assertEquals(expected, branch.compute(x)!!, 1e-9)
//    }
//
//    @Test
//    @DisplayName("Полная сборка тригонометрической ветки совпадает с пошаговой из тех же модулей")
//    fun `full real wiring equals incremental real stack for trig`() {
//        val w = wireModules(TEST_EPS)
//        val x = -0.55
//        val built = TrigSystemBranch(
//            Sec(w.cos),
//            Sin(w.cos),
//            w.cos,
//            Csc(Sin(w.cos)),
//            Tan(Sin(w.cos), w.cos),
//        )
//        assertEquals(w.trigBranch.compute(x)!!, built.compute(x)!!, 1e-8)
//    }
//
//    @Test
//    @DisplayName("Полная сборка лог-ветки совпадает с пошаговой из LogBase и ln")
//    fun `full real wiring equals incremental real stack for log`() {
//        val w = wireModules(TEST_EPS)
//        val x = 3.3
//        val ln = w.ln
//        val built = LogSystemBranch(
//            LogBase(ln, 2.0, "log2"),
//            LogBase(ln, 10.0, "log10"),
//            LogBase(ln, 3.0, "log3"),
//            ln,
//        )
//        assertEquals(w.logBranch.compute(x)!!, built.compute(x)!!, 1e-8)
//    }
//
//    @Test
//    @DisplayName("Кусочная система: отрицательный x → триг, положительный → лог, как у wireModules")
//    fun `piecewise system uses both branches like wired`() {
//        val w = wireModules(TEST_EPS)
//        val sys = SystemFunction(w.trigBranch, w.logBranch)
//        assertEquals(w.trigBranch.compute(-0.2), sys.compute(-0.2))
//        assertEquals(w.logBranch.compute(4.0), sys.compute(4.0))
//    }
//}
