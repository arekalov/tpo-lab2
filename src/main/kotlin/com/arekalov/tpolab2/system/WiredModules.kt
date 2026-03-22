package com.arekalov.tpolab2.system

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.core.Cos
import com.arekalov.tpolab2.functions.core.Ln
import com.arekalov.tpolab2.functions.log.LogBase
import com.arekalov.tpolab2.functions.trig.Csc
import com.arekalov.tpolab2.functions.trig.Sec
import com.arekalov.tpolab2.functions.trig.Sin
import com.arekalov.tpolab2.functions.trig.Tan

/** Граф реальных модулей (для сборки системы и снятия эталонных значений в таблицы заглушек). */
data class WiredModules(
    val cos: Cos,
    val ln: Ln,
    val sin: Sin,
    val sec: Sec,
    val tan: Tan,
    val csc: Csc,
    val log2: LogBase,
    val log3: LogBase,
    val log10: LogBase,
    val trigBranch: TrigSystemBranch,
    val logBranch: LogSystemBranch,
)

/** Все модули с уникальными [FunctionModule.moduleId] (для CSV и выбора по имени). */
fun WiredModules.allModules(): List<FunctionModule> {
    val system = SystemFunction(trigBranch, logBranch)
    return listOf(cos, ln, sin, sec, tan, csc, log2, log3, log10, trigBranch, logBranch, system)
}

fun wireModules(epsilon: Double): WiredModules {
    require(epsilon > 0.0) { "epsilon must be positive" }
    val cos = Cos(epsilon)
    val ln = Ln(epsilon)
    val sin = Sin(cos)
    val sec = Sec(cos)
    val tan = Tan(sin, cos)
    val csc = Csc(sin)
    val log2 = LogBase(ln, 2.0, "log2")
    val log3 = LogBase(ln, 3.0, "log3")
    val log10 = LogBase(ln, 10.0, "log10")
    val trigBranch = TrigSystemBranch(sec, sin, cos, csc, tan)
    val logBranch = LogSystemBranch(log2, log10, log3, ln)
    return WiredModules(cos, ln, sin, sec, tan, csc, log2, log3, log10, trigBranch, logBranch)
}
