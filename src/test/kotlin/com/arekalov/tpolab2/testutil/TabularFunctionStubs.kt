package com.arekalov.tpolab2.testutil

import com.arekalov.tpolab2.functions.FunctionModule
import kotlin.math.PI
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

internal fun moduleFromFiniteTable(moduleId: String, table: Map<Double, Double?>): FunctionModule {
    val nullable: Map<Double, Double?> = table.entries.associate { (k, v) -> k to v }
    return moduleFromNullableTable(moduleId, nullable)
}

/**
 * Все табличные данные и Mockito-стабы для тестов (узлы — только [mapOf] / [listOf]).
 *
 * - `*.TABLE` — ответ мока; для Cos и Ln (x>0) — эталон ряда в CosTest/LnTest; в [Ln.TABLE] при x≤0 для мока — null.
 * - Sin/Sec/Tan/Csc: те же узлы, что у [Cos.TABLE], значения согласованы с цепочкой sin/sec/tan/csc от табличного cos (узлы без sec/tan при cos=0 и без csc при sin=0 опущены).
 * - [PiecewiseSystem] — как раньше.
 */
object StubTables {

    object Cos {
        val TABLE: Map<Double, Double> = mapOf(
            0.0 to 1.0, // граничный случай: cos(0)=1; CosTest, SinTest/SecTest/TanTest/CscTest, узел около нуля
            0.3 to 0.955336489125606, // x=0.3: CosTest, SinTest/SecTest/TanTest/CscTest; промежуточный угол
            0.5235987755982988 to 0.8660254037844387, // x=π/6: CosTest, SinTest/SecTest/TanTest/CscTest
            -1.1 to 0.4535961214255773, // x=-1.1: CosTest, SinTest/SecTest/TanTest/CscTest; отрицательный аргумент
            1.5607966601082315 to 0.009999500037496774, // граничный случай: cos≈0⁺; CosTest, SinTest/SecTest/TanTest/CscTest, Sec/Tan null рядом
            -1.5607966601082315 to 0.009999500037496774, // x≈−π/2: симметрия; CosTest, SinTest/SecTest/TanTest/CscTest
            2.356194490192345 to -0.7071067811865475, // x=3π/4: CosTest, SinTest/SecTest/TanTest/CscTest
            1e-15 to 1.0, // граничный случай: очень малый |x|; CosTest, SinTest/SecTest/TanTest/CscTest
            -1.5 to 0.0707372016677029, // x=-1.5: MockedBranchesSystemTest, Sec/Sin/Tan/Csc; |x|>1, не −0.5/−1
            -0.7 to 0.7648421872844885, // x=-0.7: MockedBranchesSystemTest, Sec/Sin/Tan/Csc; между −1 и −0.5
            -0.4 to 0.9210609940028851, // x=-0.4: MockedBranchesSystemTest, Sec/Sin/Tan/Csc; близко к 0⁻
            -0.35 to 0.9393727128473789, // x=-0.35: SystemIntegrationTest (ветка со стабом cos), SinTest/SecTest/TanTest/CscTest
            -1.0 to 0.5403023058681398, // x=-1.0: TrigSystemBranchTest, SystemIntegrationTest; cos(−1)
            -0.5 to 0.8775825618903728, // x=-0.5: TrigSystemBranchTest; cos(−0.5), в Tan стаб 0 → полюс ветки
            -0.2 to 0.9800665778412416, // x=-0.2: TrigSystemBranchTest (AlwaysNull), SystemIntegrationTest
            PI / 2 to 0.0, // граничный случай: cos(π/2)=0; SinTest/SecTest/TanTest/CscTest Sec/Tan → null
            2.0 * PI to 1.0, // x=2π: CosTest, SinTest/SecTest/TanTest/CscTest; cos(2π), приведение угла
            -5.0 to 0.28366218546322625, // x=-5: CosTest, TrigAngleReductionTest; остаток по 2π, ветка reduce
        )

        val module: FunctionModule = moduleFromFiniteTable("cos", TABLE)
    }

    /**
     * 1/cos на тех же x, что [Cos.TABLE], кроме x=π/2 (полюс при cos=0); [com.arekalov.tpolab2.functions.trig.Sec] над [Cos.module].
     */
    object Sec {
        val TABLE: Map<Double, Double> = mapOf(
            0.0 to 1.0, // x=0: sec(0)=1; SecTest, узел около нуля
            0.3 to 1.0467516015380856, // x=0.3: SecTest; промежуточный угол
            0.5235987755982988 to 1.1547005383792515, // x=π/6: SecTest
            -1.1 to 2.2046043887173594, // x=-1.1: SecTest; отрицательный аргумент
            1.5607966601082315 to 100.00499987500726, // cos≈0⁺; SecTest
            -1.5607966601082315 to 100.00499987500726, // x≈−π/2: симметрия; SecTest
            2.356194490192345 to -1.4142135623730951, // x=3π/4: SecTest
            1e-15 to 1.0, // очень малый |x|; SecTest
            -1.5 to 14.136832902969903, // x=-1.5: MockedBranchesSystemTest, SecTest
            -0.7 to 1.3074592597335937, // x=-0.7: MockedBranchesSystemTest, SecTest
            -0.4 to 1.0857044283832387, // x=-0.4: MockedBranchesSystemTest, SecTest
            -0.35 to 1.064540183383495, // x=-0.35: SystemIntegrationTest, SecTest
            -1.0 to 1.8508157176809255, // x=-1.0: TrigSystemBranchTest, SystemIntegrationTest, SecTest
            -0.5 to 1.139493927324549, // x=-0.5: TrigSystemBranchTest, SecTest
            -0.2 to 1.0203388449411928, // x=-0.2: TrigSystemBranchTest, SystemIntegrationTest, SecTest
            2.0 * PI to 1.0, // x=2π: SecTest; приведение угла
            -5.0 to 3.5253200858160887, // x=-5: SecTest, TrigAngleReductionTest
        )
        val module: FunctionModule = moduleFromFiniteTable("sec", TABLE)
    }

    /**
     * Ожидаемый sin при том же x, что [Cos.TABLE], если cos берётся из [Cos.module] (цепочка [com.arekalov.tpolab2.functions.trig.Sin]).
     */
    object Sin {
        val TABLE: Map<Double, Double> = mapOf(
            0.0 to 0.0, // x=0: sin(0)=0; SinTest, узел около нуля
            0.3 to 0.29552020666133966, // x=0.3: SinTest; промежуточный угол
            0.5235987755982988 to 0.5, // x=π/6: SinTest
            -1.1 to -0.8912073600614354, // x=-1.1: SinTest; отрицательный аргумент
            1.5607966601082315 to 0.9999500037496876, // cos≈0⁺; SinTest
            -1.5607966601082315 to -0.9999500037496876, // x≈−π/2: симметрия; SinTest
            2.356194490192345 to 0.7071067811865476, // x=3π/4: SinTest
            1e-15 to 0.0, // очень малый |x|; SinTest
            -1.5 to -0.9974949866040544, // x=-1.5: MockedBranchesSystemTest, SinTest
            -0.7 to -0.644217687237691, // x=-0.7: MockedBranchesSystemTest, SinTest
            -0.4 to -0.38941834230865047, // x=-0.4: MockedBranchesSystemTest, SinTest
            -0.35 to -0.3428978074554514, // x=-0.35: SystemIntegrationTest, SinTest
            -1.0 to -0.8414709848078964, // x=-1.0: TrigSystemBranchTest, SystemIntegrationTest, SinTest
            -0.5 to -0.47942553860420295, // x=-0.5: TrigSystemBranchTest, SinTest
            -0.2 to -0.1986693307950612, // x=-0.2: TrigSystemBranchTest, SystemIntegrationTest, SinTest
            PI / 2 to 1.0, // x=π/2: sin(π/2)=1; SinTest
            2.0 * PI to 0.0, // x=2π: SinTest; приведение угла
            -5.0 to 0.9589242746631385, // x=-5: SinTest, TrigAngleReductionTest
        )
        val module: FunctionModule = moduleFromFiniteTable("sin", TABLE)
    }

    /**
     * 1/sin на тех же x, что [Sin.TABLE], без узлов с sin=0 (полюс csc); согласовано с [Cos.module].
     */
    object Csc {
        val TABLE: Map<Double, Double> = mapOf(
            0.3 to 3.3838633618241216, // x=0.3: CscTest
            0.5235987755982988 to 2.0, // x=π/6: CscTest
            -1.1 to -1.1220733185272, // x=-1.1: CscTest
            1.5607966601082315 to 1.0000499987500624, // cos≈0⁺; CscTest
            -1.5607966601082315 to -1.0000499987500624, // x≈−π/2: CscTest
            2.356194490192345 to 1.414213562373095, // x=3π/4: CscTest
            -1.5 to -1.0025113042467249, // x=-1.5: MockedBranchesSystemTest, CscTest
            -0.7 to -1.552270326957104, // x=-0.7: MockedBranchesSystemTest, CscTest
            -0.4 to -2.5679324555477834, // x=-0.4: MockedBranchesSystemTest, CscTest
            -0.35 to -2.916320776212365, // x=-0.35: SystemIntegrationTest, CscTest
            -1.0 to -1.1883951057781215, // x=-1.0: TrigSystemBranchTest, SystemIntegrationTest, CscTest
            -0.5 to -2.0858296429334886, // x=-0.5: CscTest
            -0.2 to -5.033489547672345, // x=-0.2: TrigSystemBranchTest, CscTest
            PI / 2 to 1.0, // x=π/2: csc(π/2)=1; CscTest
            -5.0 to 1.0428352127714058, // x=-5: CscTest
        )
        val module: FunctionModule = moduleFromFiniteTable("csc", TABLE)
    }

    /**
     * sin/cos на узлах [Cos.TABLE]; без x=π/2 (tan не определён при cos=0 в [com.arekalov.tpolab2.functions.trig.Tan]).
     */
    object Tan {
        val TABLE: Map<Double, Double> = mapOf(
            0.0 to 0.0, // x=0: TanTest
            0.3 to 0.3093362496096233, // x=0.3: TanTest
            0.5235987755982988 to 0.5773502691896256, // x=π/6: TanTest
            -1.1 to -1.9647596572486525, // x=-1.1: TanTest
            1.5607966601082315 to 100.00000000000101, // cos≈0⁺; TanTest
            -1.5607966601082315 to -100.00000000000101, // x≈−π/2: TanTest
            2.356194490192345 to -1.0000000000000002, // x=3π/4: TanTest
            1e-15 to 0.0, // очень малый |x|; TanTest
            -1.5 to -14.10141994717172, // x=-1.5: MockedBranchesSystemTest, TanTest
            -0.7 to -0.8422883804630793, // x=-0.7: MockedBranchesSystemTest, TanTest
            -0.4 to -0.4227932187381617, // x=-0.4: MockedBranchesSystemTest, TanTest
            -0.35 to -0.3650284948304246, // x=-0.35: SystemIntegrationTest, TanTest
            -1.0 to -1.5574077246549018, // x=-1.0: TrigSystemBranchTest, SystemIntegrationTest, TanTest
            -0.5 to -0.5463024898437905, // x=-0.5: TanTest
            -0.2 to -0.20271003550867245, // x=-0.2: TrigSystemBranchTest, TanTest
            2.0 * PI to 0.0, // x=2π: TanTest
            -5.0 to 3.380515006246586, // x=-5: TanTest
        )
        val module: FunctionModule = moduleFromFiniteTable("tan", TABLE)
    }

    object Ln {
        val TABLE: Map<Double, Double?> = mapOf(
            0.0 to null, // граничный случай: x≤0 вне ОДЗ ln; мок null для веток
            1.0 to 0.0, // ln(1)=0; LnTest, LogBaseTest log₂1
            1.5 to 0.4054651081081644, // PiecewiseSystem.LOG_X[0], LnTest, LogSystemBranch, MockedBranchesSystemTest
            2.0 to 0.6931471805599453, // основание LogBase log₂; ln(2)
            3.0 to 1.0986122886681098, // основание log₃; ln(3)
            4.0 to 1.3862943611198906, // PiecewiseSystem.LOG_X[1], LogSystemBranch, MockedBranchesSystemTest
            5.5 to 1.7047480922384423, // LogBaseTest log₃5.5; ln(5.5)
            6.0 to 1.791759469228055, // PiecewiseSystem.LOG_X[2], MockedBranchesSystemTest
            8.0 to 2.0794415416798357, // ln(8); LogBaseTest log₂8
            9.0 to 2.1972245773362196, // ln(9); LogBaseTest log₃9
            10.0 to 2.302585092994046, // основание log₁₀; ln(10)
            27.0 to 3.295836866004329, // ln(27); LogBaseTest log₃27
            100.0 to 4.605170185988092, // ln(100); LnTest, LogBaseTest log₁₀100
            1000.0 to 6.907755278982137, // ln(1000); LogBaseTest log₁₀1000
            2.5 to 0.9162907318741551, // LogSystemBranchTest; между узлами PiecewiseSystem
            2.6 to 0.9555114450274363, // LogSystemBranchTest при подмене модуля на AlwaysNull
            0.25 to -1.3862943611198906, // LnTest; интервал (0,1)
            2.718281828459045 to 1.0, // x=e; LnTest
            64.0 to 4.1588830833596715, // LnTest; масштабирование через ln2
            0.001 to -6.907755278982137, // LnTest; близко к 0⁺
            1000000.0 to 13.81551055, // LogSystemBranchTest,
            100.0 to 4.605170185988092,
            1.0E-7 to -16.11809565095832,
            4.93341 to 1.5960304325217474,
        )

        val module: FunctionModule = moduleFromNullableTable("ln", TABLE)

        /** ln(x)/ln(base) по моку — для [com.arekalov.tpolab2.functions.log.LogBaseTest]. */
        fun logBaseExpected(base: Double, x: Double): Double? {
            val a = TABLE.getValue(x)?: return null
            val b = TABLE.getValue(base)?: return null
            return a/ b
        }
    }


    object Log2 {
        val TABLE: Map<Double, Double?> = mapOf(
            0.0 to null, // x=0: LogBranch.TABLE; до ln стаб отрабатывает, ln → null
            1.0 to 0.0, // x=1: LogBranch.TABLE; согласовано с log10/log3=0 на 1
            1.5 to 0.5849625007211562, // x=1.5: PiecewiseSystem.LOG_X[0], MockedBranchesSystemTest
            2.0 to 1.0, // x=2.0: LogBaseTest, LogSystemBranch; мок log₂(2)=1 при ln-стабе
            2.5 to 1.32, // x=2.5: LogSystemBranchTest; между узлами PiecewiseSystem
            2.6 to 1.0, // x=2.6: LogSystemBranchTest AlwaysNull для log2
            3.0 to 1.0, // x=3.0: сценарий log3=0 в стабе; LogSystemBranch
            4.0 to 2.0, // x=4.0: PiecewiseSystem.LOG_X[1], MockedBranchesSystemTest
            6.0 to 2.584962500721156, // x=6.0: PiecewiseSystem.LOG_X[2], MockedBranchesSystemTest
            1000000.0 to 19.93156856,
            100.0 to 6.643856189774725,
            1.0E-7 to -23.25349666421154,
            4.93341 to 2.3025851901069925,
        )
        val module: FunctionModule = moduleFromFiniteTable("log2", TABLE)
    }


    object Log3 {
        val TABLE: Map<Double, Double?> = mapOf(
            0.0 to null, // x=0: LogBranch.TABLE; ненулевой знаменатель до обрыва на ln
            1.0 to 1.0, // x=1: LogBranch.TABLE; ветка 0 при ln(1)=0 и log2/log10=0
            1.5 to 0.3690702464285425, // x=1.5: формула лог-ветки, MockedBranchesSystemTest
            2.0 to 0.5, // x=2.0: LogSystemBranch; общая сетка с Log2/Log10
            2.5 to 0.83, // x=2.5: LogSystemBranchTest
            2.6 to 1.0, // x=2.6: LogSystemBranchTest подмена модуля
            3.0 to 0.0, // граничный случай: log3=0 → знаменатель null в LogSystemBranchTest
            4.0 to 1.2618595071429148, // x=4.0: PiecewiseSystem.LOG_X[1]
            6.0 to 1.6309297535714573, // x=6.0: PiecewiseSystem.LOG_X[2]
            1000000.0 to 12.57541964,
            100.0 to 4.19180654857877,
            1.0E-7 to -14.671322920025695,
            4.93341 to 1.4527695065714923,
        )
        val module: FunctionModule = moduleFromFiniteTable("log3", TABLE)
    }

    object Log10 {
        val TABLE: Map<Double, Double?> = mapOf(
            0.0 to null, // x=0: LogBranch.TABLE
            1.0 to 0.0, // x=1: LogBranch.TABLE
            1.5 to 0.17609125905568124, // x=1.5: общая лог-сетка, MockedBranchesSystemTest
            2.0 to 2.0, // x=2.0: LogSystemBranch; стаб согласован с формулой ветки
            2.5 to 0.40, // x=2.5: LogSystemBranchTest
            2.6 to 1.0, // x=2.6: LogSystemBranchTest подмена соседнего модуля
            3.0 to 1.0, // x=3.0: узел log3=0; проверка null лог-ветки
            4.0 to 0.6020599913279623, // x=4.0: PiecewiseSystem.LOG_X[1]
            6.0 to 0.7781512503836435, // x=6.0: PiecewiseSystem.LOG_X[2]
            1000000.0 to 6.0,
            100.0 to 2.0,
            1.0E-7 to -7.0,
            4.93341 to 0.6931472097938551,
        )
        val module: FunctionModule = moduleFromFiniteTable("log10", TABLE)
    }

    object LogBranch {
        val TABLE: Map<Double, Double?> = mapOf(
            0.000_000_1 to 178.8925723,
            0.0 to null, // ln(0)=null в стабе
            1.0 to 0.0, // log2/log10=0, ln(1)=0, log3(1)=1 → ветка 0
            3.0 to null, // log3(3)=0 в стабе → знаменатель 0, ветка null
            4.93341 to 0.0,
            100.0 to 8.68254219356,
            1_000_000.0 to 105.773900858,
        )
        val module: FunctionModule = moduleFromFiniteTable("trigBranch", TABLE)
    }

    /**
     * Сетка и ожидания кусочной системы на стабах
     * ([com.arekalov.tpolab2.integration.MockedBranchesSystemTest]).
     */
    object PiecewiseSystem {
        val TRIG_X: List<Double> = listOf(
            -1.5, // x=-1.5: MockedBranchesSystemTest; |x|>1, не −0.5 и не −1 (полюса/другие тесты)
            -0.7, // x=-0.7: MockedBranchesSystemTest; между −1 и −0.5, без tan=0
            -0.4, // x=-0.4: MockedBranchesSystemTest; близко к 0⁻, иной масштаб
        )

        val LOG_X: List<Double> = listOf(
            1.5, // x=1.5: MockedBranchesSystemTest; первая точка лог-ветки, узлы Ln/Log*
            4.0, // x=4.0: MockedBranchesSystemTest; вторая точка сетки
            6.0, // x=6.0: MockedBranchesSystemTest; третья точка сетки
        )

        val TRIG_EXPECTED: Map<Double, Double> = mapOf(
            -1.5 to 0.14165204597035536, // x=-1.5: ожидание SystemFunction при подстановке стабов триг-ветки
            -0.7 to 1.9520840194111682, // x=-0.7: то же
            -0.4 to 3.2862834140419954, // x=-0.4: то же (дрейф double после уточнения sin/csc в стабах)
        )

        val LOG_EXPECTED: Map<Double, Double> = mapOf(
            1.5 to -0.3024583248558162, // x=1.5: ожидание SystemFunction на лог-стабах
            4.0 to -0.182174378463966, // x=4.0: то же
            6.0 to 0.21973233290294256, // x=6.0: то же
        )
    }

    /** Мок всегда null — обрыв цепочки в TrigSystemBranch / LogSystemBranch. */
    object AlwaysNull {
        val module: FunctionModule = run {
            val m = mock<FunctionModule>()
            whenever(m.moduleId).thenReturn("bad")
            whenever(m.compute(any())).thenReturn(null)
            m
        }
    }
}
