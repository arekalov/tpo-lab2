package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule

/**
 * Тригонометрическая ветка из варианта (x ≤ 0), без алгебраического упрощения скобок.
 */
class TrigSystemBranch(
    private val sec: FunctionModule,
    private val sin: FunctionModule,
    private val cos: FunctionModule,
    private val csc: FunctionModule,
    private val tan: FunctionModule,
) : FunctionModule {

    override val moduleId = "trigBranch"

    override fun compute(x: Double): Double? {
        val sec1 = sec.compute(x) ?: return null
        val sec2 = sec.compute(x) ?: return null
        val sec3 = sec.compute(x) ?: return null
        val sin1 = sin.compute(x) ?: return null
        val inner = (sec1 - sec2) + sec3 * sin1
        val cosV = cos.compute(x) ?: return null
        val numLeft = inner * cosV
        val sin2 = sin.compute(x) ?: return null
        val cscV = csc.compute(x) ?: return null
        val numerator = numLeft - sin2 * cscV
        val tanV = tan.compute(x) ?: return null
        if (tanV == 0.0) {
            return null
        }
        return numerator / tanV
    }
}
