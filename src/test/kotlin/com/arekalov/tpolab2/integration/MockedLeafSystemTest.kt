package com.arekalov.tpolab2.integration

import com.arekalov.tpolab2.TEST_EPS
import com.arekalov.tpolab2.mockEchoing
import com.arekalov.tpolab2.system.LogSystemBranch
import com.arekalov.tpolab2.system.SystemFunction
import com.arekalov.tpolab2.system.TrigSystemBranch
import com.arekalov.tpolab2.system.wireModules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Система: листья — Mockito, значения с реального графа на сетке")
class MockedLeafSystemTest {

    @Test
    fun `mocked leaves match real on tabulated points`() {
        val eps = TEST_EPS
        val real = wireModules(eps)
        val mocked = buildSystemWithMockedLeaves(
            epsilon = eps,
            trigXs = listOf(-1.5, -0.7, -0.4),
            logXs = listOf(1.5, 4.0, 8.0),
        )
        for (x in listOf(-1.5, -0.7, -0.4)) {
            assertEquals(real.trigBranch.compute(x), mocked.compute(x))
        }
        for (x in listOf(1.5, 4.0, 8.0)) {
            assertEquals(real.logBranch.compute(x), mocked.compute(x))
        }
    }

    @Test
    fun `build fails when trig points invalid`() {
        assertThrows<IllegalArgumentException> {
            buildSystemWithMockedLeaves(
                TEST_EPS,
                trigXs = listOf(1.0, 2.0),
                logXs = listOf(1.5),
            )
        }
    }
}

private fun buildSystemWithMockedLeaves(
    epsilon: Double,
    trigXs: List<Double>,
    logXs: List<Double>,
): SystemFunction {
    val real = wireModules(epsilon)
    val trigOk = trigXs.distinct().filter { it <= 0.0 && real.trigBranch.compute(it) != null }
    val logOk = logXs.distinct().filter { it > 0.0 && real.logBranch.compute(it) != null }
    require(trigOk.isNotEmpty()) {
        "ни одна точка из trigXs не подошла для тригонометрической ветки — расширь список или смени x"
    }
    require(logOk.isNotEmpty()) {
        "ни одна точка из logXs не подошла для лог-ветки — расширь список или смени x"
    }

    val cos = mockEchoing("cos", trigOk) { real.cos.compute(it) }
    val sin = mockEchoing("sin", trigOk) { real.sin.compute(it) }
    val sec = mockEchoing("sec", trigOk) { real.sec.compute(it) }
    val tan = mockEchoing("tan", trigOk) { real.tan.compute(it) }
    val csc = mockEchoing("csc", trigOk) { real.csc.compute(it) }

    val ln = mockEchoing("ln", logOk) { real.ln.compute(it) }
    val log2 = mockEchoing("log2", logOk) { real.log2.compute(it) }
    val log3 = mockEchoing("log3", logOk) { real.log3.compute(it) }
    val log10 = mockEchoing("log10", logOk) { real.log10.compute(it) }

    return SystemFunction(
        TrigSystemBranch(sec, sin, cos, csc, tan),
        LogSystemBranch(log2, log10, log3, ln),
    )
}
