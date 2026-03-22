package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.absoluteValue

/**
 * Базовая тригонометрическая функция варианта: **cos(x)** через ряд Тейлора (без `kotlin.math.cos`).
 *
 * **Что считается:** для приведённого `t = reduceToMinusPiPi(x)` сумма
 * `cos(t) = Σ (-1)ⁿ · t^(2n) / (2n)!` в стандартном итеративном виде: накапливается `term`,
 * каждый следующий получается из предыдущего множителем `-t² / ((2n-1)(2n))`.
 *
 * **Остановка:** при `n > 0` и `|term| < epsilon`, либо по лимиту [maxTerms] — возвращается накопленная сумма.
 * **ОДЗ:** не-число и бесконечность → `null`.
 */
class Cos(
    private val epsilon: Double,
    private val maxTerms: Int = 10_000,
) : FunctionModule {

    override val moduleId = "cos"

    init {
        require(epsilon > 0.0) { "epsilon must be positive" }
        require(maxTerms > 0) { "maxTerms must be positive" }
    }

    override fun compute(x: Double): Double? {
        if (x.isNaN() || x.isInfinite()) {
            return null
        }
        val t = reduceToMinusPiPi(x)
        var term = 1.0 // n=0: (-1)^0 · t^0 / 0! = 1
        var sum = 0.0
        var n = 0
        while (n < maxTerms) {
            sum += term
            if (n > 0 && term.absoluteValue < epsilon) {
                return sum
            }
            n++
            // переход к слагаемому порядка 2n: term_n = term_{n-1} · (-t²) / ((2n-1)(2n))
            term *= -t * t / ((2 * n - 1) * (2 * n))
        }
        return sum
    }
}
