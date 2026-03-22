package com.arekalov.tpolab2.core

/**
 * Вычисление значения функции в точке [x].
 * [null] — аргумент вне области допустимых значений или неопределённость.
 */
fun interface FunctionModule {
    fun compute(x: Double): Double?
}
