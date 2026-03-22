package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.functions.FunctionModule

/**
 * **sec(x) = 1 / cos(x)** — секанс через тот же [cos], что и остальная тригонометрия.
 *
 * Если `cos(x)` не определён в смысле модуля (`null`) или **cos = 0** (полюс sec), возвращается `null`.
 */
class Sec(
    private val cos: FunctionModule,
) : FunctionModule {

    override val moduleId = "sec"

    override fun compute(x: Double): Double? {
        val c = cos.compute(x) ?: return null
        if (c == 0.0) {
            return null
        }
        return 1.0 / c
    }
}
