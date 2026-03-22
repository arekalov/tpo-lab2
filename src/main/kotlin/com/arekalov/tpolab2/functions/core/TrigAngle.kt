package com.arekalov.tpolab2.functions.core

import kotlin.math.PI

/** Приведение аргумента к отрезку, согласованному с периодом cos/sin (как в [Cos]). */
internal fun reduceToMinusPiPi(x: Double): Double {
    val twoPi = 2.0 * PI
    var y = x % twoPi
    if (y > PI) {
        y -= twoPi
    }
    if (y < -PI) {
        y += twoPi
    }
    return y
}
