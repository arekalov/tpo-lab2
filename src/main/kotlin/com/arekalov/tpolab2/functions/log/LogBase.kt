package com.arekalov.tpolab2.functions.log

import com.arekalov.tpolab2.functions.FunctionModule

/**
 * Логарифм по произвольному основанию **log_base(x)** только через модуль [ln] (по условию лабораторной).
 *
 * **Формула:** `log_base(x) = ln(x) / ln(base)` при x > 0, base > 0, base ≠ 1.
 *
 * **Конструктор:** один раз считает `ln(base)` и сохраняет в знаменатель [lnBase]. Если `ln(base)` не определён
 * (`null`) или равен нулю — исключение: иначе деление в [compute] было бы некорректным.
 *
 * **compute:** `ln.compute(x)`; при `null` (x вне ОДЗ ln) возвращает `null`, иначе `lnx / lnBase`.
 */
class LogBase(
    private val ln: FunctionModule,
    private val base: Double,
    override val moduleId: String,
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
