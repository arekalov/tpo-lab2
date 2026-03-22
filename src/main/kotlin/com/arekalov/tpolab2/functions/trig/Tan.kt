package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.functions.FunctionModule

/** tan(x) = sin(x) / cos(x). */
class Tan(
    private val sin: FunctionModule,
    private val cos: FunctionModule,
) : FunctionModule {

    override val moduleId = "tan"

    override fun compute(x: Double): Double? {
        val s = sin.compute(x) ?: return null
        val c = cos.compute(x) ?: return null
        if (c == 0.0) {
            return null
        }
        return s / c
    }
}
