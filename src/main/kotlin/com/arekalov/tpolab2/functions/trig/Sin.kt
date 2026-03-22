package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.reduceToMinusPiPi
import kotlin.math.sqrt

/**
 * **sin(x)** выражается через модуль [cos] (отдельного ряда для sin нет — по условию лабораторной).
 *
 * **Что считается:**
 * 1. Модуль: `|sin x| = √(1 - cos²x)` из `cos.compute(x)`; аргумент тот же `x`, внутри cos снова приводит угол.
 * 2. Знак: по приведённому углу `y = reduceToMinusPiPi(x)` на (−π, π]: положительный полуинтервал → sin ≥ 0,
 *    отрицательный → sin ≤ 0, на границе 0 → 0.
 *
 * `sinSq` при необходимости режется до 0, чтобы из-за погрешности ряда cos не брать sqrt от отрицательного.
 * Очень малый модуль → 0 (шум около нулей sin).
 */
class Sin(
    private val cos: FunctionModule,
) : FunctionModule {

    override val moduleId = "sin"

    override fun compute(x: Double): Double? {
        if (x.isNaN() || x.isInfinite()) {
            return null
        }
        val y = reduceToMinusPiPi(x)
        val c = cos.compute(x) ?: return null
        val sinSq = 1.0 - c * c
        val clipped = if (sinSq > 0.0) sinSq else 0.0
        val mag = sqrt(clipped)
        if (mag < MAG_EPS) {
            return 0.0
        }
        return when {
            y > 0.0 -> mag
            y < 0.0 -> -mag
            else -> 0.0
        }
    }

    companion object {
        private const val MAG_EPS = 1e-14
    }
}
