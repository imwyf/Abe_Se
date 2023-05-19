package com.duwei.common.caculator;

import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.caculator
 * @Author: duwei
 * @Date: 2023/5/12 10:26
 * @Description: 抽象的表达式计算规则
 */
public abstract class AbstractEvaluator<T> implements Evaluator<T> {
    protected  Map<String, Operator<T>> operators;
    protected Calculator<T> calculator;

    public AbstractEvaluator() {

    }

    @Override
    public void setCalculator(Calculator<T> calculator) {
        this.calculator = calculator;
        this.operators = calculator.getOperators();
    }

    @Override
    public T apply(String op, T b, T a) {
        return operators.get(op).apply(a, b);
    }


    @Override
    public Operator<T> getOperator(String op) {
        return operators.get(op);
    }

    protected T parser(String op){
        return calculator.parserOperand(op);
    }
}
