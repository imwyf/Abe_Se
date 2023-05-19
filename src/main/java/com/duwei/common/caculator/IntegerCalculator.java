package com.duwei.common.caculator;

import java.util.HashMap;
import java.util.Map;
/**
 * 支持整数类型的计算器
 */
public class IntegerCalculator extends AbstractCalculator<Integer> {
    private static final Map<String, Operator<Integer>> operators;

    static {
        operators = new HashMap<>();
        operators.put("&", BinaryOperator.AND);
        operators.put("|", BinaryOperator.OR);
        operators.put("^", BinaryOperator.XOR);
    }

    public IntegerCalculator(Evaluator<Integer> evaluator) {
        super(operators, evaluator);
    }


    @Override
    public Integer parserOperand(String op) {
        return Integer.parseInt(op);
    }
}