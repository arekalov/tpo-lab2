package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.absoluteValue

/**
 * Базовый модуль cos(x): ряд Тейлора, остановка по модулю слагаемого [epsilon].
 * Аргумент приводится к [-π, π] по периоду 2π (без kotlin.math.cos).
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
        var term = 1.0
        var sum = 0.0
        var n = 0
        while (n < maxTerms) {
            sum += term
            if (n > 0 && term.absoluteValue < epsilon) {
                return sum
            }
            n++
            term *= -t * t / ((2 * n - 1) * (2 * n))
        }
        return sum
    }
}
