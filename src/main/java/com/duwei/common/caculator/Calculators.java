package com.duwei.common.caculator;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.caculator
 * @Author: duwei
 * @Date: 2023/5/12 11:32
 * @Description: 计算器工具类
 */
public class Calculators {
    /**
     * 获取整数类型的中缀表达式计算器
     * @return
     */
    public static Calculator<Integer> integerInfixCalculator(){
        Evaluator<Integer> evaluator = new InfixEvaluator<>();
        return new IntegerCalculator(evaluator);
    }

    /**
     * 获取double类型的中缀表达式计算器
     * @return
     */
    public static Calculator<Double> doubleInfixCalculator(){
        Evaluator<Double> evaluator = new InfixEvaluator<>();
        return new DoubleCalculator(evaluator);
    }


}
