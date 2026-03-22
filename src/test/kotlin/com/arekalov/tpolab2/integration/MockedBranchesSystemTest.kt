package com.arekalov.tpolab2.integration

import com.arekalov.tpolab2.system.LogSystemBranch
import com.arekalov.tpolab2.system.SystemFunction
import com.arekalov.tpolab2.system.TrigSystemBranch
import com.arekalov.tpolab2.testutil.StubTables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Система: триг- и лог-ветка на общих табличных стабах")
class MockedBranchesSystemTest {

    @Test
    @DisplayName("Собранная система: ожидаемые значения веток на выбранных x")
    fun `mocked branches match tabulated expectations`() {
        val g = StubTables.PiecewiseSystem
        val mocked = systemWithStubTables(trigXs = g.TRIG_X, logXs = g.LOG_X)
        for (x in g.TRIG_X) {
            assertEquals(g.TRIG_EXPECTED.getValue(x), mocked.compute(x))
        }
        for (x in g.LOG_X) {
            assertEquals(g.LOG_EXPECTED.getValue(x), mocked.compute(x))
        }
    }

    @Test
    @DisplayName("Сборка падает, если ни одна точка не подходит под сетку триг-ветки")
    fun `build fails when trig points invalid`() {
        assertThrows<IllegalArgumentException> {
            systemWithStubTables(
                trigXs = listOf(1.0, 2.0),
                logXs = StubTables.PiecewiseSystem.LOG_X,
            )
        }
    }
}

private fun systemWithStubTables(trigXs: List<Double>, logXs: List<Double>): SystemFunction {
    val g = StubTables.PiecewiseSystem
    val trigOk = trigXs.distinct().filter { it <= 0.0 && g.TRIG_EXPECTED.containsKey(it) }
    val logOk = logXs.distinct().filter { it > 0.0 && g.LOG_EXPECTED.containsKey(it) }
    require(trigOk.isNotEmpty()) {
        "ни одна точка из trigXs не подошла под сетку триг-ветки"
    }
    require(logOk.isNotEmpty()) {
        "ни одна точка из logXs не подошла под сетку лог-ветки"
    }

    return SystemFunction(
        TrigSystemBranch(
            StubTables.Sec.module,
            StubTables.Sin.module,
            StubTables.Cos.module,
            StubTables.Csc.module,
            StubTables.Tan.module,
        ),
        LogSystemBranch(
            StubTables.Log2.module,
            StubTables.Log10.module,
            StubTables.Log3.module,
            StubTables.Ln.module,
        ),
    )
}
