package com.arekalov.tpolab2

import com.arekalov.tpolab2.csv.CsvRange
import com.arekalov.tpolab2.csv.exportModuleCsv
import com.arekalov.tpolab2.system.allModules
import com.arekalov.tpolab2.system.buildSystemFunction
import com.arekalov.tpolab2.system.wireModules
import java.io.File

fun main() {
    val value = buildSystemFunction(EPSILON).compute(X)
    println(value?.toString() ?: "undefined")

    val wired = wireModules(EPSILON)
    val range = CsvRange(
        xFrom = CSV_X_FROM,
        xTo = CSV_X_TO,
        step = CSV_STEP,
        delimiter = CSV_DELIMITER,
    )
    val outDir = File(CSV_OUTPUT_DIR)
    val modules = wired.allModules()
    for (module in modules) {
        exportModuleCsv(
            module = module,
            target = outDir.resolve("${module.moduleId}.csv"),
            range = range,
        )
    }
    val names = modules.map { it.moduleId }.sorted().joinToString()
    val graphsDir = outDir.resolve("graphs")
    println("CSV: ${outDir.absolutePath} (${modules.size} файлов: $names)")
    println("Графики: ${graphsDir.absolutePath}")
}

/** Погрешность остановки рядов cos / ln. */
private const val EPSILON = 1e-10

/** Точка, в которой считаем кусочную функцию (x≤0 — триг, x>0 — логи). */
private const val X: Double = -3.0

private const val CSV_X_FROM = -15.0
private const val CSV_X_TO = 15.0
private const val CSV_STEP = 0.01
private const val CSV_DELIMITER = ';'

/** Каталог для CSV; PNG графики (Kandy / Lets-Plot) — в подкаталоге `graphs/`. */
private const val CSV_OUTPUT_DIR = "build/csv"
