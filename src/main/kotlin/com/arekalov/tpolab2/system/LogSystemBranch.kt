package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule

/**
 * Логарифмическая ветка кусочной системы при **x > 0**: запись формулы из варианта **без алгебраического упрощения**
 * (скобки и повторные вызовы тех же модулей сохраняются, как в задании).
 *
 * **Что считается** (при успешных вычислениях подвыражений):
 * ```
 * numerator = ((log2(x) - log2(x)) + log2(x) * log10(x)) * log3(x) - log3(x) * ln(x)
 * result = numerator / log3(x)
 * ```
 *
 * **null:** любой из вызовов `log2`, `log10`, `log3`, `ln` вернул `null`, либо знаменатель `log3(x) == 0`
 * (деление на ноль в смысле задачи — «не определено»).
 */
class LogSystemBranch(
    private val log2: FunctionModule,
    private val log10: FunctionModule,
    private val log3: FunctionModule,
    private val ln: FunctionModule,
) : FunctionModule {

    override val moduleId = "logBranch"

    override fun compute(x: Double): Double? {
        if (x <= 0) return null
        val l2a = log2.compute(x) ?: return null
        val l2b = log2.compute(x) ?: return null
        val l2c = log2.compute(x) ?: return null
        val l10 = log10.compute(x) ?: return null
        val inner = (l2a - l2b) + l2c * l10
        val l3a = log3.compute(x) ?: return null
        val left = inner * l3a
        val l3b = log3.compute(x) ?: return null
        val lnx = ln.compute(x) ?: return null
        val numerator = left - l3b * lnx
        val l3den = log3.compute(x) ?: return null
        if (l3den == 0.0) {
            return null
        }
        return numerator / l3den
    }
}
