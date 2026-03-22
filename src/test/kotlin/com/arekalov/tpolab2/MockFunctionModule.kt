package com.arekalov.tpolab2

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.abs
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private const val X_MATCH = 1e-12

/**
 * Мок [FunctionModule]: на точке [x] возвращает [value], на остальных аргументах — [null]
 * (аналог «одной строки таблицы»).
 */
internal fun mockReturningAt(moduleId: String, x: Double, value: Double): FunctionModule {
    val m = mock<FunctionModule>()
    whenever(m.moduleId).thenReturn(moduleId)
    whenever(m.compute(any())).thenAnswer { inv ->
        val arg = inv.getArgument<Double>(0)
        if (abs(arg - x) <= X_MATCH) value else null
    }
    return m
}

/**
 * Мок: на каждом x из [xs] значение [cell](x), иначе null.
 */
internal fun mockEchoing(
    moduleId: String,
    xs: List<Double>,
    cell: (Double) -> Double?,
): FunctionModule {
    val m = mock<FunctionModule>()
    whenever(m.moduleId).thenReturn(moduleId)
    whenever(m.compute(any())).thenAnswer { inv ->
        val arg = inv.getArgument<Double>(0)
        xs.find { abs(it - arg) <= X_MATCH }?.let { cell(it) }
    }
    return m
}
