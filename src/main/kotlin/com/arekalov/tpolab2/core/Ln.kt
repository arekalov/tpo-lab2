package com.arekalov.tpolab2.core

import kotlin.math.absoluteValue

/**
 * Базовый модуль ln(x) для x > 0: приведение к множителю из [1, 2) степенями двойки и ряд
 * ln(x) = 2*atanh((x-1)/(x+1)); ln(2) тем же рядом при (1+u)/(1-u)=2 (u = 1/3).
 */
class Ln(
    private val epsilon: Double,
) : FunctionModule {

    init {
        require(epsilon > 0.0) { "epsilon must be positive" }
    }

    private val ln2: Double by lazy(LazyThreadSafetyMode.NONE) {
        ln2ViaAtanhSeries(minOf(epsilon, LN2_SERIES_EPS))
    }

    override fun compute(x: Double): Double? {
        if (x <= 0.0 || x.isNaN() || x.isInfinite()) {
            return null
        }
        if (x == 1.0) {
            return 0.0
        }
        var y = x
        var e = 0
        while (y >= 2.0) {
            y /= 2.0
            e++
        }
        while (y < 1.0) {
            y *= 2.0
            e--
        }
        val lnY = lnViaAtanhX(y, epsilon)
        return lnY + e * ln2
    }

    companion object {
        private const val LN2_SERIES_EPS = 1e-15
        private const val MAX_TERMS = 100_000

        private fun ln2ViaAtanhSeries(eps: Double): Double {
            val u = 1.0 / 3.0
            return twoAtanhSeries(u, eps)
        }

        private fun lnViaAtanhX(x: Double, eps: Double): Double {
            val t = (x - 1.0) / (x + 1.0)
            return twoAtanhSeries(t, eps)
        }

        private fun twoAtanhSeries(t: Double, eps: Double): Double {
            var power = t
            var sum = 0.0
            var k = 0
            while (k < MAX_TERMS) {
                val term = power / (2 * k + 1)
                sum += term
                if (term.absoluteValue < eps) {
                    return 2.0 * sum
                }
                k++
                power *= t * t
                if (power.isNaN() || power.isInfinite()) {
                    return 2.0 * sum
                }
            }
            return 2.0 * sum
        }
    }
}
