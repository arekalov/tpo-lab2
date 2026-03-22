package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule

/** Кусочная система функций: x ≤ 0 — тригонометрия, x > 0 — логарифмы. */
class SystemFunction(
    private val trigBranch: FunctionModule,
    private val logBranch: FunctionModule,
) : FunctionModule {

    override fun compute(x: Double): Double? {
        if (x.isNaN() || x.isInfinite()) {
            return null
        }
        return if (x <= 0.0) {
            trigBranch.compute(x)
        } else {
            logBranch.compute(x)
        }
    }
}
