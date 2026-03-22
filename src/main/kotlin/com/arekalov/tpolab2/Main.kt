package com.arekalov.tpolab2

import com.arekalov.tpolab2.system.buildSystemFunction

fun main() {
    val value = buildSystemFunction(EPSILON).compute(X)
    println(value?.toString() ?: "undefined")
}

/** Погрешность остановки рядов cos / ln. */
private const val EPSILON = 1e-10

/** Точка, в которой считаем кусочную функцию (x≤0 — триг, x>0 — логи). */
private const val X: Double = -3.0
