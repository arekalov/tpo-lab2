package com.arekalov.tpolab2.system

/** Собирает кусочную систему из реальных модулей с общей погрешностью рядов. */
fun buildSystemFunction(epsilon: Double): SystemFunction {
    val w = wireModules(epsilon)
    return SystemFunction(w.trigBranch, w.logBranch)
}
