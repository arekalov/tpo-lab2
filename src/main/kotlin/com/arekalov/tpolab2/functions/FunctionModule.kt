package com.arekalov.tpolab2.functions

/**
 * Вычисление значения функции в точке [x].
 * [null] — аргумент вне области допустимых значений или неопределённость.
 *
 * [moduleId] — стабильное имя модуля (CSV, отчёты).
 */
interface FunctionModule {
    val moduleId: String
    fun compute(x: Double): Double?
}
