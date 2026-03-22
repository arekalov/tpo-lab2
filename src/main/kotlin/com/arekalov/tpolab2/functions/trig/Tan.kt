package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.abs

/**
 * **tan(x) = sin(x) / cos(x)** — тангенс через уже собранные [sin] и [cos].
 *
 * Любой из модулей может вернуть `null` → результат `null`. Дополнительно: **cos = 0** даёт полюс tan → `null`.
 */
class Tan(
    private val sin: FunctionModule,
    private val cos: FunctionModule,
) : FunctionModule {

    override val moduleId = "tan"

    override fun compute(x: Double): Double? {
        val s = sin.compute(x) ?: return null
        val c = cos.compute(x) ?: return null
        if (abs(c) < 1e-10) {
            return null
        }
        return s / c
    }
}
