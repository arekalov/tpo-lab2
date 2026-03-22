package com.arekalov.tpolab2.functions.log

import com.arekalov.tpolab2.functions.FunctionModule

/** log_base(x) = ln(x) / ln(base), x > 0, base > 0, base ≠ 1. */
class LogBase(
    private val ln: FunctionModule,
    private val base: Double,
) : FunctionModule {

    private val lnBase: Double

    init {
        require(base > 0.0 && base != 1.0) { "base must be positive and not 1" }
        val lb = ln.compute(base)
            ?: throw IllegalArgumentException("ln(base) is undefined for base=$base")
        require(lb != 0.0) { "ln(base) must be non-zero" }
        lnBase = lb
    }

    override fun compute(x: Double): Double? {
        val lnx = ln.compute(x) ?: return null
        return lnx / lnBase
    }
}
