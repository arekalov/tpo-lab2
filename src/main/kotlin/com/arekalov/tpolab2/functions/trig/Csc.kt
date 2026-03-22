package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.functions.FunctionModule

/**
 * **csc(x) = 1 / sin(x)** — косеканс через [sin].
 *
 * `sin` вернул `null` → `null`. **sin = 0** (полюс csc) → `null`.
 */
class Csc(
    private val sin: FunctionModule,
) : FunctionModule {

    override val moduleId = "csc"

    override fun compute(x: Double): Double? {
        val s = sin.compute(x) ?: return null
        if (s == 0.0) {
            return null
        }
        return 1.0 / s
    }
}
