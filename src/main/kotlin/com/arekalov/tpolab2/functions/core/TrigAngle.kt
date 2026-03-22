package com.arekalov.tpolab2.functions.core

import kotlin.math.PI

/**
 * Приведение угла к интервалу (−π, π] для cos/sin.
 *
 * **Зачем:** cos и sin периодичны с периодом 2π; ряд Тейлора для cos считается по приведённому углу —
 * меньше |угол|, стабильнее сходимость. Для [com.arekalov.tpolab2.functions.trig.Sin] тот же угол задаёт знак sin.
 *
 * **Как:** остаток `x % 2π`, затем при необходимости сдвиг на ±2π, если оказались выше π или ниже −π.
 */
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
