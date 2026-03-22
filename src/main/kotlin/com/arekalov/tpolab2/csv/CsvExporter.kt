package com.arekalov.tpolab2.csv

import com.arekalov.tpolab2.functions.FunctionModule
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y
import org.jetbrains.kotlinx.kandy.util.color.Color
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

private const val MAX_ROWS = 500_000
private const val GRAPHS_SUBDIR = "graphs"

/** Ширина и высота холста PNG в пикселях (Lets-Plot / Kandy `layout.size`). */
private const val PLOT_PIXEL_WIDTH = 1920
private const val PLOT_PIXEL_HEIGHT = 1280

/** Дополнительный масштаб при растеризации PNG (чётче линии и подписи). */
private const val PLOT_SAVE_SCALE = 1.5

/** Полуширина окна по Y на PNG: режет выбросы у tan/sec/csc и кусочной system. */
private const val PLOT_Y_VIEW_RADIUS = 42.0

/** Разрыв линии между соседними точками при скачке по Y (полюса tan/sec/csc). */
private const val LINE_DY_BREAK = 90.0

/** Разрыв при |Δy|/|Δx| выше порога при фиксированном шаге по X (асимптоты). */
private const val LINE_SLOPE_BREAK = 320.0

/**
 * Номера сегментов для [groupBy]: не соединять соседние точки через разрыв кусочной функции (x=0)
 * и через вертикальные асимптоты (огромный скачок Y на маленьком Δx).
 */
private fun lineSegmentIds(xs: List<Double>, ys: List<Double>): List<Int> {
    require(xs.size == ys.size)
    val ids = ArrayList<Int>(xs.size)
    var gid = 0
    for (i in xs.indices) {
        if (i > 0) {
            val xPrev = xs[i - 1]
            val xCur = xs[i]
            val pieceCut = (xPrev <= 0.0) != (xCur <= 0.0)
            val dx = xCur - xPrev
            val dy = kotlin.math.abs(ys[i] - ys[i - 1])
            val slope = if (kotlin.math.abs(dx) > 1e-12) dy / kotlin.math.abs(dx) else Double.POSITIVE_INFINITY
            if (pieceCut || dy >= LINE_DY_BREAK || slope >= LINE_SLOPE_BREAK) {
                gid++
            }
        }
        ids.add(gid)
    }
    return ids
}

private fun xAxisLimits(xs: List<Double>): ClosedFloatingPointRange<Double> {
    val lo = xs.minOrNull() ?: return -1.0..1.0
    val hi = xs.maxOrNull() ?: return -1.0..1.0
    val span = (hi - lo).coerceAtLeast(1e-9)
    val pad = span * 0.02
    return (lo - pad)..(hi + pad)
}

/**
 * Окно по Y: перцентили 15–85 и ограничение [PLOT_Y_VIEW_RADIUS] (асимптоты).
 */
private fun yAxisLimitsFromValues(ys: List<Double>): ClosedFloatingPointRange<Double> {
    val sorted = ys.asSequence().filter { it.isFinite() }.sorted().toList()
    if (sorted.isEmpty()) {
        return -1.0..1.0
    }
    val n = sorted.size
    fun quantile(p: Double): Double {
        val idx = ((n - 1) * p).toInt().coerceIn(0, n - 1)
        return sorted[idx]
    }
    val lo = quantile(0.15)
    val hi = quantile(0.85)
    val center = (lo + hi) / 2.0
    var half = maxOf(center - lo, hi - center, 1e-3)
    half *= 1.1
    if (half > PLOT_Y_VIEW_RADIUS) {
        half = PLOT_Y_VIEW_RADIUS
    }
    return (center - half)..(center + half)
}

/**
 * Для кусочных функций (как system: x≤0 и x>0) общие перцентили по всем Y тянут ось к одной ветке,
 * и вторая обрезается. Считаем окно по Y отдельно для x≤0 и x>0, затем берём объединение.
 */
private fun yAxisLimits(xs: List<Double>, ys: List<Double>): ClosedFloatingPointRange<Double> {
    require(xs.size == ys.size)
    val yNonPos = ArrayList<Double>()
    val yPos = ArrayList<Double>()
    for (i in xs.indices) {
        val y = ys[i]
        if (!y.isFinite()) {
            continue
        }
        if (xs[i] <= 0.0) {
            yNonPos.add(y)
        } else {
            yPos.add(y)
        }
    }
    val wNonPos = if (yNonPos.size >= 2) yAxisLimitsFromValues(yNonPos) else null
    val wPos = if (yPos.size >= 2) yAxisLimitsFromValues(yPos) else null
    return when {
        wNonPos != null && wPos != null -> {
            val lo = minOf(wNonPos.start, wPos.start)
            val hi = maxOf(wNonPos.endInclusive, wPos.endInclusive)
            val span = hi - lo
            val maxSpan = 4.0 * PLOT_Y_VIEW_RADIUS
            if (span > maxSpan) {
                val c = (lo + hi) / 2.0
                val half = maxSpan / 2.0
                (c - half)..(c + half)
            } else {
                lo..hi
            }
        }
        wNonPos != null -> wNonPos
        wPos != null -> wPos
        else -> -1.0..1.0
    }
}

private val kandyPlotErrorShown = AtomicBoolean(false)

private fun warnKandyPlotOnce(message: String) {
    if (!kandyPlotErrorShown.compareAndSet(false, true)) {
        return
    }
    System.err.println("CSV график (Kandy): $message")
}

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
 *
 * Если [exportPng] = true и после пропуска `undefined` осталось не меньше двух точек,
 * строится линейный график Y(X) через [Kandy](https://kotlin.github.io/kandy/) и сохраняется PNG
 * в `<родитель_csv>/graphs/<имя>.png` или [plotTarget].
 * Размер картинки — [PLOT_PIXEL_WIDTH]×[PLOT_PIXEL_HEIGHT] логических пикселей холста и [PLOT_SAVE_SCALE] при экспорте;
 * диапазоны по осям задаются данными ([xAxisLimits], [yAxisLimits]), а не размером файла.
 */
fun exportModuleCsv(
    module: FunctionModule,
    target: File,
    range: CsvRange,
    plotTarget: File? = null,
    exportPng: Boolean = true,
) {
    require(range.step > 0.0) { "step must be positive" }
    require(range.xFrom.isFinite() && range.xTo.isFinite()) { "xFrom and xTo must be finite" }

    val a = minOf(range.xFrom, range.xTo)
    val b = maxOf(range.xFrom, range.xTo)
    val delimiter = range.delimiter

    target.parentFile?.mkdirs()

    val xs = ArrayList<Double>(1024)
    val ys = ArrayList<Double>(1024)

    target.bufferedWriter().use { writer ->
        writer.append('X').append(delimiter).append("Y").append('\n')
        var x = a
        var rows = 0
        while (x <= b + 1e-12 && rows < MAX_ROWS) {
            val y = module.compute(x)
            writer.append(x.toString()).append(delimiter)
                .append(y?.toString() ?: "undefined").append('\n')
            if (y != null) {
                xs.add(x)
                ys.add(y)
            }
            rows++
            x += range.step
        }
    }

    if (!exportPng) {
        return
    }
    val pngOut = plotTarget ?: (target.parentFile ?: File("."))
        .resolve(GRAPHS_SUBDIR)
        .resolve("${target.nameWithoutExtension}.png")
    saveLinePlotPng(
        xs = xs,
        ys = ys,
        pngFile = pngOut,
        title = module.moduleId,
    )
}

private fun saveLinePlotPng(xs: List<Double>, ys: List<Double>, pngFile: File, title: String) {
    if (xs.size < 2 || xs.size != ys.size) {
        return
    }
    pngFile.parentFile?.mkdirs()
    runCatching {
        val table = dataFrameOf(
            "X" to xs,
            "Y" to ys,
            "seg" to lineSegmentIds(xs, ys),
        )
        val plot = plot(table.groupBy("seg")) {
            line {
                x("X")
                y("Y")
                width = 1.5
                color = Color.hex(0x1f77b4)
            }
            layout {
                this.title = title.ifBlank { pngFile.nameWithoutExtension }
                size = PLOT_PIXEL_WIDTH to PLOT_PIXEL_HEIGHT
            }
            val xLim = xAxisLimits(xs)
            val yLim = yAxisLimits(xs, ys)
            x.axis.name = "X"
            x.axis.min = xLim.start
            x.axis.max = xLim.endInclusive
            y.axis.name = "Y"
            y.axis.min = yLim.start
            y.axis.max = yLim.endInclusive
        }
        plot.save(
            pngFile.name,
            scale = PLOT_SAVE_SCALE,
            path = pngFile.parentFile.absolutePath,
        )
    }.onFailure { e ->
        warnKandyPlotOnce("не удалось сохранить PNG (${pngFile.name}): ${e.message ?: e::class.simpleName}")
    }
}
