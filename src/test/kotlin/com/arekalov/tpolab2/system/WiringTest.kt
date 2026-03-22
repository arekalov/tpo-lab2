package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.TEST_EPS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Сборка wireModules, allModules, buildSystemFunction")
class WiringTest {

    @Test
    @DisplayName("wireModules: триг- и лог-ветки считаются на типичных x")
    fun `wireModules exposes consistent graph`() {
        val w = wireModules(TEST_EPS)
        assertNotNull(w.trigBranch.compute(-0.4))
        assertNotNull(w.logBranch.compute(2.0))
    }

    @Test
    @DisplayName("buildSystemFunction даёт те же значения, что и ветки из wireModules")
    fun `buildSystemFunction matches wired branches`() {
        val w = wireModules(TEST_EPS)
        val sys = buildSystemFunction(TEST_EPS)
        val xTrig = -0.7
        val xLog = 1.2
        assertEquals(w.trigBranch.compute(xTrig), sys.compute(xTrig))
        assertEquals(w.logBranch.compute(xLog), sys.compute(xLog))
    }

    @Test
    @DisplayName("У всех собранных модулей уникальный moduleId")
    fun `all wired modules have unique moduleId`() {
        val modules = wireModules(TEST_EPS).allModules()
        assertEquals(modules.size, modules.map { it.moduleId }.distinct().size)
    }

    @Test
    @DisplayName("wireModules отклоняет неположительный epsilon")
    fun `wireModules rejects bad epsilon`() {
        assertThrows<IllegalArgumentException> { wireModules(0.0) }
    }
}
