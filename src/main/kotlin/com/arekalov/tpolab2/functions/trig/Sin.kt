package com.arekalov.tpolab2.functions.trig

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.reduceToMinusPiPi
import kotlin.math.sqrt

/**
 * sin(x) через [cos]: |sin| = sqrt(1 - cos²), знак по приведённому углу y ∈ (-π, π]:
 * y > 0 → sin ≥ 0, y < 0 → sin ≤ 0, y = 0 → 0.
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
