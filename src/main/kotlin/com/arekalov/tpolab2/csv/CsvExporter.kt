package com.arekalov.tpolab2.csv

import com.arekalov.tpolab2.functions.FunctionModule
import java.io.File

private const val MAX_ROWS = 500_000

/** Диапазон по X и разделитель колонок в CSV. */
data class CsvRange(
    val xFrom: Double,
    val xTo: Double,
    val step: Double,
    val delimiter: Char = ';',
)

/**
 * Записывает CSV: две колонки X и Y (значение модуля), строка заголовка.
 * Диапазон обходится от min(xFrom, xTo) до max(xFrom, xTo) с шагом [CsvRange.step] > 0.
 * При [null] из [FunctionModule.compute] в файл пишется строка `undefined`.
 */
fun exportModuleCsv(
    module: FunctionModule,
    target: File,
    range: CsvRange,
) {
    require(range.step > 0.0) { "step must be positive" }
    require(range.xFrom.isFinite() && range.xTo.isFinite()) { "xFrom and xTo must be finite" }

    val a = minOf(range.xFrom, range.xTo)
    val b = maxOf(range.xFrom, range.xTo)
    val delimiter = range.delimiter

    target.parentFile?.mkdirs()
    target.bufferedWriter().use { writer ->
        writer.append('X').append(delimiter).append("Y").append('\n')
        var x = a
        var rows = 0
        while (x <= b + 1e-12 && rows < MAX_ROWS) {
            val y = module.compute(x)
            writer.append(x.toString()).append(delimiter)
                .append(y?.toString() ?: "undefined").append('\n')
            rows++
            x += range.step
        }
    }
}
