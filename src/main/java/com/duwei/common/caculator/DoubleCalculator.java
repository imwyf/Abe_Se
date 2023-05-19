package com.duwei.common.caculator;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持小数和整数类型的计算器
 */
public class DoubleCalculator extends AbstractCalculator<Double> {
    private static final Map<String, Operator<Double>> operators;

    static {
        operators = new HashMap<>();
        operators.put("+", ArithmeticOperator.ADD);
        operators.put("-", ArithmeticOperator.SUB);
        operators.put("*", ArithmeticOperator.MUL);
        operators.put("/", ArithmeticOperator.DIV);
        operators.put("**", ArithmeticOperator.POW);
    }

    public DoubleCalculator(Evaluator<Double> evaluator) {
        super(operators, evaluator);
    }


    @Override
    public Double calculate(String expression) {
        return evaluator.evaluate(normalizer.normalize(expression));
    }

    @Override
    public Double parserOperand(String op) {
        return Double.parseDouble(op);
    }
}
