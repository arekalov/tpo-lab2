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
    fun `wireModules exposes consistent graph`() {
        val w = wireModules(TEST_EPS)
        assertNotNull(w.trigBranch.compute(-0.4))
        assertNotNull(w.logBranch.compute(2.0))
    }

    @Test
    fun `buildSystemFunction matches wired branches`() {
        val w = wireModules(TEST_EPS)
        val sys = buildSystemFunction(TEST_EPS)
        val xTrig = -0.7
        val xLog = 1.2
        assertEquals(w.trigBranch.compute(xTrig), sys.compute(xTrig))
        assertEquals(w.logBranch.compute(xLog), sys.compute(xLog))
    }

    @Test
    fun `all wired modules have unique moduleId`() {
        val modules = wireModules(TEST_EPS).allModules()
        assertEquals(modules.size, modules.map { it.moduleId }.distinct().size)
    }

    @Test
    fun `wireModules rejects bad epsilon`() {
        assertThrows<IllegalArgumentException> { wireModules(0.0) }
    }
}
