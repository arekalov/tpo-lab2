package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.absoluteValue

/** Ряд 2·atanh(t) = 2 Σ t^(2k+1)/(2k+1) для [Ln]; |t| < 1 на всех путях из [Ln.compute]. */
private fun twoAtanhSeries(t: Double, eps: Double, maxTerms: Int): Double {
    var power = t
    var sum = 0.0
    var k = 0
    while (k < maxTerms) {
        val term = power / (2 * k + 1)
        sum += term
        if (term.absoluteValue < eps) {
            return 2.0 * sum
        }
        k++
        power *= t * t
    }
    return 2.0 * sum
}

/**
 * Базовый модуль ln(x) для x > 0: приведение к множителю из [1, 2) степенями двойки и ряд
 * ln(x) = 2*atanh((x-1)/(x+1)); ln(2) тем же рядом при (1+u)/(1-u)=2 (u = 1/3).
 */
class Ln(
    private val epsilon: Double,
    private val maxTerms: Int = DEFAULT_MAX_TERMS,
) : FunctionModule {

    override val moduleId = "ln"

    init {
        require(epsilon > 0.0) { "epsilon must be positive" }
        require(maxTerms > 0) { "maxTerms must be positive" }
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

    private fun ln2ViaAtanhSeries(eps: Double): Double {
        val u = 1.0 / 3.0
        return twoAtanhSeries(u, eps, maxTerms)
    }

    private fun lnViaAtanhX(x: Double, eps: Double): Double {
        val t = (x - 1.0) / (x + 1.0)
        return twoAtanhSeries(t, eps, maxTerms)
    }

    companion object {
        private const val LN2_SERIES_EPS = 1e-15
        private const val DEFAULT_MAX_TERMS = 100_000
    }
}
