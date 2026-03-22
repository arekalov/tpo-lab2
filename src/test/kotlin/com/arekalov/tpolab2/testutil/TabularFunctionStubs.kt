package com.arekalov.tpolab2.testutil

import com.arekalov.tpolab2.functions.FunctionModule
import com.arekalov.tpolab2.functions.trig.Csc
import com.arekalov.tpolab2.functions.trig.Sec
import com.arekalov.tpolab2.functions.trig.Sin
import com.arekalov.tpolab2.functions.trig.Tan
import java.util.stream.Stream
import org.junit.jupiter.params.provider.Arguments
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal fun moduleFromNullableTable(moduleId: String, table: Map<Double, Double?>): FunctionModule {
    val m = mock<FunctionModule>()
    whenever(m.moduleId).thenReturn(moduleId)
    whenever(m.compute(any())).thenAnswer { inv ->
        val x = inv.getArgument<Double>(0)
        if (!table.containsKey(x)) {
            throw IllegalArgumentException("stub '$moduleId': нет табличного значения для x = $x")
        }
        table[x]
    }
    return m
}

internal fun moduleFromFiniteTable(moduleId: String, table: Map<Double, Double>): FunctionModule {
    val nullable: Map<Double, Double?> = table.entries.associate { (k, v) -> k to v as Double? }
    return moduleFromNullableTable(moduleId, nullable)
}

/**
 * Все табличные данные и Mockito-стабы для тестов.
 *
 * - `*.TABLE` — ответ мока `module` (интеграция, ветки).
 * - [Cos.REFERENCE], [Ln.REFERENCE] — эталон для тестов ряда Cos / Ln (где отличается от мока).
 * - [Cos.Derived] — sin/sec/tan/csc от [Cos.module].
 * - [PiecewiseSystem] — сетка и ожидания для собранной системы на стабах.
 */
object StubTables {

    private data class FinitePoint(
        val x: Double,
        /** ответ мока */
        val mock: Double,
        /** эталон для теста ряда Cos; null → совпадает с [mock] */
        val reference: Double? = null,
    ) {
        fun ref(): Double = reference ?: mock
    }

    object Cos {
        private val POINTS: List<FinitePoint> = listOf(
            FinitePoint(0.0, 1.0), // центр ряда, cos(0)=1
            FinitePoint(0.3, 0.955336489125606), // промежуточный положительный угол
            FinitePoint(0.5235987755982988, 0.8660254037844387), // π/6 — типичный угол
            FinitePoint(-1.1, 0.4535961214255773), // отрицательный аргумент, чётность
            FinitePoint(1.5607966601082315, 0.009999500037496774), // около π/2, cos близок к 0
            FinitePoint(-1.5607966601082315, 0.009999500037496774), // симметрия относительно 0
            FinitePoint(2.356194490192345, -0.7071067811865475), // 3π/4 после приведения
            FinitePoint(1e-15, 1.0), // очень малый |x|, ветка малых значений
            FinitePoint(-1.5, 0.0707372016677029), // −1.5: |x|>1 на отрицательной оси; не −1 и не −0.5 (у −0.5 в стабе tan=0 — полюс ветки)
            FinitePoint(-0.7, 0.7648421872844885), // −0.7: строго между −1 и −0.5 — «середина» без полюса и без около нуля
            FinitePoint(-0.4, 0.9210609940028851), // −0.4: близко к 0 слева, но не микроскопический шаг — иной масштаб, чем у −1.5 и −0.7
            FinitePoint(-0.35, 0.9393727128473789), // отрицательный x не из PiecewiseSystem — расширение DerivedTrigTest / интеграции
            FinitePoint(-1.0, 0.4, 0.5403023058681398), // x из TrigSystemBranchTest; мок подставляет не cos(x), REFERENCE — для CosTest
            FinitePoint(-0.5, 1.0, 0.8775825618903728), // в Tan.TABLE здесь 0 — полюс триг-ветки; мок cos тоже «левый», REFERENCE — настоящий cos
            FinitePoint(-0.2, 1.0, 0.9800665778412416), // x для TrigSystemBranchTest (подмена модуля на AlwaysNull); REFERENCE — настоящий cos
        )

        val TABLE: Map<Double, Double> = POINTS.associate { it.x to it.mock }

        /** Эталонные cos(x) для [com.arekalov.tpolab2.functions.core.CosTest]. */
        val REFERENCE: Map<Double, Double> = POINTS.associate { it.x to it.ref() }

        val module: FunctionModule = moduleFromFiniteTable("cos", TABLE)

        /**
         * Ожидаемые sin / sec / tan / csc, если cos берётся из [module] (таблица [TABLE]).
         */
        object Derived {
            private val sinM by lazy { Sin(module) }
            private val secM by lazy { Sec(module) }
            private val tanM by lazy { Tan(sinM, module) }
            private val cscM by lazy { Csc(sinM) }

            private val xs: List<Double> by lazy { TABLE.keys.sorted() }

            val sinPairs: List<Pair<Double, Double>> by lazy { xs.map { x -> x to sinM.compute(x)!! } }
            val secPairs: List<Pair<Double, Double>> by lazy { xs.map { x -> x to secM.compute(x)!! } }
            val tanPairs: List<Pair<Double, Double>> by lazy { xs.map { x -> x to tanM.compute(x)!! } }
            val cscPairs: List<Pair<Double, Double>> by lazy {
                xs.mapNotNull { x -> cscM.compute(x)?.let { x to it } }
            }
        }
    }

    private data class LnPoint(
        val x: Double,
        val mock: Double?,
        /**
         * Ожидание для [com.arekalov.tpolab2.functions.core.LnTest]; null — этот x не гоняем в параметризации ряда
         * (узел только для мока / log-ветки).
         */
        val seriesReference: Double? = null,
    )

    object Ln {
        private val POINTS: List<LnPoint> = listOf(
            LnPoint(0.0, null, null), // граница ОДЗ — вне области, мок null
            LnPoint(1.0, 0.0, 0.0), // ln 1 = 0
            LnPoint(1.5, 0.4054651081081644, 0.4054651081081644), // x из PiecewiseSystem.LOG_X; эталон для LnTest совпадает с моком
            LnPoint(2.0, 1.0, null), // основание 2 в LogBase: в таблице «как будто» ln2=1 для целых log₂
            LnPoint(3.0, 1.0, null), // основание 3 в LogBase: log₃9, log₃27, log₃5.5
            LnPoint(4.0, 1.3862943611198906, null), // вторая точка PiecewiseSystem.LOG_X; настоящий ln4 в моке для согласованности
            LnPoint(5.5, 1.5517285850726805, null), // не степень целых оснований; отдельный кейс log₃5.5 в LogBaseTest
            LnPoint(6.0, 1.791759469228055, null), // третья точка PiecewiseSystem.LOG_X
            LnPoint(8.0, 3.0, null), // аргумент log₂8: при моке ln8=3, ln2=1 получается log₂8=3
            LnPoint(9.0, 2.0, null), // аргумент log₃9: при моке ln9=2, ln3=1 получается log₃9=2
            LnPoint(10.0, 1.0, null), // основание 10: ln10 в знаменателе для log₁₀100 и log₁₀1000
            LnPoint(27.0, 3.0, null), // 3³; аргумент для log₃27=3 при моке ln27=3, ln3=1
            LnPoint(100.0, 2.0, 4.605170185988092), // мок ln100=2 для log₁₀100; REFERENCE — настоящий ln(100) для LnTest
            LnPoint(1000.0, 3.0, null), // аргумент log₁₀1000=3 при моке ln1000=3, ln10=1
            LnPoint(2.5, 0.9162907318741551, null), // промежуточный x>1 рядом с x=2.0 из LogSystemBranchTest; не дублирует узлы PiecewiseSystem
            LnPoint(2.6, 1.0, null), // x из LogSystemBranchTest при подмене одного из модулей на AlwaysNull
            LnPoint(0.25, -1.3862943611198906, -1.3862943611198906), // x∈(0,1): проверка ряда у левой границы ОДЗ (не 0, не 1)
            LnPoint(2.718281828459045, 1.0, 1.0), // граничный эталон ln(e)=1 без «магии» степеней в моке
            LnPoint(64.0, 4.1588830833596715, 4.1588830833596715), // 2⁶: проверка ветки масштабирования через ln2 в реализации Ln
            LnPoint(0.001, -6.907755278982137, -6.907755278982137), // сильное сжатие к 0⁺; устойчивость ряда на малых x
        )

        val TABLE: Map<Double, Double?> = POINTS.associate { it.x to it.mock }

        /** Только узлы, по которым гоняется ряд в LnTest. */
        val REFERENCE: Map<Double, Double> =
            POINTS.mapNotNull { p -> p.seriesReference?.let { p.x to it } }.toMap()

        val module: FunctionModule = moduleFromNullableTable("ln", TABLE)

        /** ln(x)/ln(base) по значениям мока — для LogBaseTest. */
        fun logBaseExpected(base: Double, x: Double): Double = TABLE.getValue(x)!! / TABLE.getValue(base)!!
    }

    /** Фабрики аргументов для JUnit MethodSource; числа берутся из [Ln]. */
    class Sources {
        companion object {
            @JvmStatic
            fun logBaseCases(): Stream<Arguments> =
                Stream.of(
                    Arguments.of(2.0, 8.0, Ln.logBaseExpected(2.0, 8.0)), // log₂8 = 3 при согласованном моке ln
                    Arguments.of(2.0, 1.0, Ln.logBaseExpected(2.0, 1.0)), // log₂1 = 0
                    Arguments.of(3.0, 9.0, Ln.logBaseExpected(3.0, 9.0)), // log₃9 = 2
                    Arguments.of(3.0, 27.0, Ln.logBaseExpected(3.0, 27.0)), // log₃27 = 3
                    Arguments.of(10.0, 100.0, Ln.logBaseExpected(10.0, 100.0)), // log₁₀100 = 2
                    Arguments.of(10.0, 1000.0, Ln.logBaseExpected(10.0, 1000.0)), // log₁₀1000 = 3
                    Arguments.of(3.0, 5.5, Ln.logBaseExpected(3.0, 5.5)), // log₃5.5 по таблице ln
                )
        }
    }

    object Sec {
        val TABLE: Map<Double, Double> = mapOf(
            -1.5 to 14.136832902969903, // sec на −1.5: |x|>1 слева; не −1 и не −0.5 (у −0.5 tan=0 в стабе — полюс ветки)
            -1.0 to 1.2, // x из TrigSystemBranchTest для проверки формулы ветки «вручную» по таблице
            -0.7 to 1.3074592597335937, // −0.7: строго между −1 и −0.5 — без полюса и без около нуля
            -0.4 to 1.0857044283832387, // −0.4: близко к 0 слева, иной масштаб, чем у −1.5 и −0.7
            -0.35 to 1.064540183383495, // x из Cos.TABLE без PiecewiseSystem — интеграция и DerivedTrig на том же графе стабов
            -0.5 to 1.0, // при Tan=0: в формуле ветки деление на tan → null (TrigSystemBranchTest)
            -0.2 to 1.0, // x из TrigSystemBranchTest при подмене одного модуля на AlwaysNull
        )
        val module: FunctionModule = moduleFromFiniteTable("sec", TABLE)
    }

    object Sin {
        val TABLE: Map<Double, Double> = mapOf(
            -1.5 to -0.9974949866040544, // sin на −1.5: |x|>1 слева; не −1 и не −0.5 (полюс tan)
            -1.0 to 0.3, // x из TrigSystemBranchTest; значение стаба не обязано совпадать с sin(x) — проверка формулы ветки на моках
            -0.7 to -0.644217687237691, // −0.7: между −1 и −0.5 — «середина» отрезка
            -0.4 to -0.3894183423086505, // −0.4: близко к 0− — иной масштаб, чем у −1.5 и −0.7
            -0.35 to 0.41, // доп. x≤0 для SystemIntegrationTest и DerivedTrig по узлам Cos.TABLE
            -0.5 to 1.0, // пара с Tan=0: знаменатель триг-ветки; в TrigSystemBranchTest при x=−0.5 ожидается null
            -0.2 to 1.0, // x из TrigSystemBranchTest при подмене модуля на AlwaysNull
        )
        val module: FunctionModule = moduleFromFiniteTable("sin", TABLE)
    }

    object Csc {
        val TABLE: Map<Double, Double> = mapOf(
            -1.5 to -1.0025113042467249, // csc на −1.5: |x|>1 слева; не −1 и не −0.5 (полюс tan в стабе)
            -1.0 to 2.0, // x из TrigSystemBranchTest для ручного пересчёта формулы ветки
            -0.7 to -1.552270326957104, // −0.7: строго между −1 и −0.5 — без полюса и без около нуля
            -0.4 to -2.567932455547783, // −0.4: близко к 0 слева, иной масштаб, чем у −1.5 и −0.7
            -0.35 to 2.44, // согласование с остальными триг-стабами на x из Cos.TABLE
            -0.5 to 1.0, // согласование при Tan=0 и Sec=1 на полюсе ветки
            -0.2 to 1.0, // x для сценария с AlwaysNull в TrigSystemBranchTest
        )
        val module: FunctionModule = moduleFromFiniteTable("csc", TABLE)
    }

    object Tan {
        val TABLE: Map<Double, Double> = mapOf(
            -1.5 to -14.10141994717172, // tan на −1.5: знаменатель ветки; «глубокий» минус, не −1/−0.5
            -1.0 to 0.5, // знаменатель на x из TrigSystemBranchTest
            -0.7 to -0.8422883804630793, // −0.7: между −1 и −0.5
            -0.4 to -0.4227932187381618, // −0.4: близко к 0−, другой масштаб
            -0.35 to 0.43, // знаменатель на доп. x≤0 (Cos.TABLE)
            -0.5 to 0.0, // специально 0: деление на tan в формуле триг-ветки → null (TrigSystemBranchTest)
            -0.2 to 1.0, // ненулевой tan на x для теста с AlwaysNull
        )
        val module: FunctionModule = moduleFromFiniteTable("tan", TABLE)
    }

    object Log2 {
        val TABLE: Map<Double, Double> = mapOf(
            1.5 to 0.5849625007211562, // PiecewiseSystem.LOG_X[0]; участвует в формуле лог-ветки на моках
            2.0 to 1.0, // log₂(2)=1 согласован с моком ln (таблица «как степени двойки»)
            2.5 to 1.32, // x не из PiecewiseSystem; расширяет таблицу для интеграции и ветки между 1.5 и 4
            2.6 to 1.0, // x из LogSystemBranchTest при подмене log2 на AlwaysNull
            3.0 to 1.0, // узел, на котором log3=0 в стабе → отдельный сценарий лог-ветки
            4.0 to 2.0, // PiecewiseSystem.LOG_X[1]; log₂4=2 при согласованной таблице
            6.0 to 2.584962500721156, // PiecewiseSystem.LOG_X[2]; проверка системы и интеграции на x=6
        )
        val module: FunctionModule = moduleFromFiniteTable("log2", TABLE)
    }

    object Log10 {
        val TABLE: Map<Double, Double> = mapOf(
            1.5 to 0.17609125905568124, // тот же x=1.5, что у Log2/Log3 — единая лог-сетка для ветки
            2.0 to 2.0, // значение стаба на основании 2 (не обязано быть log₁₀2); согласование с формулой ветки на x=2
            2.5 to 0.40, // промежуточный x той же сетки, что у Log2/Log3
            2.6 to 1.0, // x для сценария с подменой другого модуля (ветка на 2.6)
            3.0 to 1.0, // x, где log3 стаб = 0 — проверка null лог-ветки
            4.0 to 0.6020599913279623, // вторая точка PiecewiseSystem.LOG_X
            6.0 to 0.7781512503836435, // третья точка PiecewiseSystem.LOG_X
        )
        val module: FunctionModule = moduleFromFiniteTable("log10", TABLE)
    }

    object Log3 {
        val TABLE: Map<Double, Double> = mapOf(
            1.5 to 0.3690702464285425, // знаменатель/множитель в формуле лог-ветки на x=1.5
            2.0 to 0.5, // стаб на x=2 для той же сетки, что Log2/Log10
            2.5 to 0.83, // промежуточный x сетки
            2.6 to 1.0, // x для проверки ветки при подмене соседнего модуля
            3.0 to 0.0, // специально 0: знаменатель в формуле лог-ветки → null (LogSystemBranchTest)
            4.0 to 1.2618595071429148, // вторая точка PiecewiseSystem.LOG_X
            6.0 to 1.6309297535714573, // третья точка PiecewiseSystem.LOG_X
        )
        val module: FunctionModule = moduleFromFiniteTable("log3", TABLE)
    }

    /**
     * Сетка и эталонные значения кусочной системы на общих стабах
     * ([MockedBranchesSystemTest] и согласованные точки в таблицах триг/лог).
     */
    object PiecewiseSystem {
        /**
         * Три точки x≤0 для [MockedBranchesSystemTest]: на каждой проверяется значение кусочной функции на стабах.
         * Числа выбраны так, чтобы не совпасть с «особым» x=−0.5 (tan=0 в стабе → полюс ветки) и с x=−1 из TrigSystemBranchTest.
         */
        val TRIG_X: List<Double> = listOf(
            // −1.5: |x|>1 на отрицательной оси — не −1 и не −0.5; крупнее по модулю, чем −0.7/−0.4, иначе ведут себя sec/tan/csc на стабах
            -1.5,
            // −0.7: строго между −1 и −0.5 — «середина» отрезка без полюса tan и без вырождения около 0
            -0.7,
            // −0.4: близко к 0 слева, но не микроскопический угол — отличается по масштабу от −1.5 и −0.7
            -0.4,
        )

        /** Три разных x>1: совпадают с узлами стабов log* / ln для MockedBranchesSystemTest. */
        val LOG_X: List<Double> = listOf(
            1.5, // между 1 и «круглыми» степенями; первая точка проверки лог-ветки
            4.0, // вторая точка; участвует в LogSystemBranch / интеграции
            6.0, // третья точка; та же сетка, что в таблицах Log2/Log10/Log3
        )

        /** Эталон: подставить таблицы Sec/Sin/Cos/Csc/Tan в формулу триг-ветки и вычислить вручную. */
        val TRIG_EXPECTED: Map<Double, Double> = mapOf(
            -1.5 to 0.14165204597035536, // эталон ветки при x=−1.5: «глубокая» отрицательная точка, не −1 и не −0.5
            -0.7 to 1.9520840194111682, // эталон при −0.7: между −1 и −0.5, без полюса tan
            -0.4 to 3.286283414041996, // эталон при −0.4: близко к 0−, иной масштаб, чем у двух предыдущих
        )

        /** Эталон: подставить таблицы Log2/Log10/Log3/Ln в формулу лог-ветки и вычислить вручную. */
        val LOG_EXPECTED: Map<Double, Double> = mapOf(
            1.5 to -0.3024583248558162, // ожидаемое значение системы при x=1.5 на стабах
            4.0 to -0.182174378463966, // то же при x=4
            6.0 to 0.21973233290294256, // то же при x=6
        )
    }

    /** Мок, всегда null — проверка обрыва цепочки в ветках при подстановке одного из модулей. */
    object AlwaysNull {
        val module: FunctionModule = run {
            val m = mock<FunctionModule>()
            whenever(m.moduleId).thenReturn("bad")
            whenever(m.compute(any())).thenReturn(null)
            m
        }
    }
}
