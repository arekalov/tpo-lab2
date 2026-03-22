package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.REF_TOLERANCE
import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.testutil.StubTables
import java.util.stream.Stream
import kotlin.math.PI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Проверяется [Sec] через модуль cos. Эталон узлов — [StubTables.Sec.TABLE] при [StubTables.Cos.module].
 */
@DisplayName("Sec: через cos")
class SecTest {

    private val sec = Sec(StubTables.Cos.module)

    companion object {
        @JvmStatic
        fun secReferenceRows(): Stream<Arguments> =
            Stream.of(
                *StubTables.Sec.TABLE.entries
                    .sortedBy { it.key }
                    .map { (x, expected) -> Arguments.of(x, expected) }
                    .toTypedArray(),
            )
    }

    @DisplayName("Значения sec по эталону StubTables.Sec.TABLE")
    @ParameterizedTest(name = "x = {0}")
    @MethodSource("secReferenceRows")
    fun `sec matches reference table`(x: Double, expected: Double) {
        assertEquals(expected, sec.compute(x)!!, REF_TOLERANCE)
    }

    @Test
    @DisplayName("sec(π/2): cos=0 в стабе — null")
    fun `sec pole at pi half`() {
        assertNull(sec.compute(PI / 2))
    }

    @Test
    @DisplayName("sec: null, если cos вернул null")
    fun `sec null when cos null`() {
        val cos = mock<FunctionModule>()
        whenever(cos.moduleId).thenReturn("cos")
        whenever(cos.compute(any())).thenReturn(null)
        assertNull(Sec(cos).compute(1.0))
    }
}
