package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.functions.core.Ln
import com.arekalov.tpolab2.functions.log.LogBase
import com.arekalov.tpolab2.functions.trig.Csc
import com.arekalov.tpolab2.functions.trig.Sec
import com.arekalov.tpolab2.functions.trig.Sin
import com.arekalov.tpolab2.functions.trig.Tan

/** Собирает кусочную систему из реальных модулей с общей погрешностью рядов. */
fun buildSystemFunction(epsilon: Double): SystemFunction {
    require(epsilon > 0.0) { "epsilon must be positive" }
    val cos = Cos(epsilon)
    val ln = Ln(epsilon)
    val sin = Sin(cos)
    val sec = Sec(cos)
    val tan = Tan(sin, cos)
    val csc = Csc(sin)
    val log2 = LogBase(ln, 2.0)
    val log3 = LogBase(ln, 3.0)
    val log10 = LogBase(ln, 10.0)
    val trigBranch = TrigSystemBranch(sec, sin, cos, csc, tan)
    val logBranch = LogSystemBranch(log2, log10, log3, ln)
    return SystemFunction(trigBranch, logBranch)
}
