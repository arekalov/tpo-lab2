package com.arekalov.tpolab2.functions.core

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.absoluteValue

/**
 * Часть ряда для **2·atanh(t)** (ряд сходится при |t| меньше 1):
 * `atanh(t) = Σ t^(2k+1)/(2k+1)`, функция возвращает **удвоенную** частичную сумму.
 *
 * Слагаемое `term_k = t^(2k+1)/(2k+1)` накапливается; выход при `|term| < eps` или после [maxTerms] итераций.
 * На путях из [Ln.compute] аргумент `t` всегда по модулю меньше 1, переполнение `power` не ожидается.
 */
private fun twoAtanhSeries(t: Double, eps: Double, maxTerms: Int): Double {
    var power = t // t^(2k+1) на шаге k
    var sum = 0.0
    var k = 0
    while (k < maxTerms) {
        val term = power / (2 * k + 1)
        sum += term
        if (term.absoluteValue < eps) {
            return 2.0 * sum
        }
        k++
        power *= t * t // переход к следующей нечётной степени t
    }
    return 2.0 * sum
}

/**
 * Базовый логарифм **ln(x)** по заданной погрешности [epsilon] (без `kotlin.math.log`).
 *
 * **Идея:** представить `x = 2^e · y`, где `y ∈ [1, 2)` — делением/умножением на 2 считается целое `e` и `y`.
 * Тогда `ln x = e·ln 2 + ln y`.
 *
 * **ln y для y ∈ [1, 2):** `t = (y-1)/(y+1)`, `ln y = 2·atanh(t)`; для таких y величина |t| не превышает 1/3.
 *
 * **ln 2:** то же разложение 2·atanh(u) при `u = 1/3` (из `(1+u)/(1-u) = 2`). Значение ln2 кэшируется лениво.
 *
 * **ОДЗ:** `x ≤ 0`, NaN, ±∞ → `null`; `x == 1` → `0` без ряда.
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

    /** ln(2) через тот же [twoAtanhSeries]; для стабильности eps не грубее [LN2_SERIES_EPS]. */
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
