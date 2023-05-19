package com.duwei.common.caculator;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.caculator
 * @Author: duwei
 * @Date: 2023/5/12 10:56
 * @Description: TODO
 */
public class Test {
    public static void main(String[] args) {
        Evaluator<Integer> evaluator = new InfixEvaluator<>();
        Calculator<Integer> calculator = new IntegerCalculator(evaluator);
        String express = "(1 & 0) & (0 & 1)";
        System.out.println(calculator.calculate(express));

    }
}
