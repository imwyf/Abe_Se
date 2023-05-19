package com.duwei.common.caculator;

import java.util.Map;

/**
 * 抽象的计算器类，注意负责完成表达式的标准化
 * @param <T>   类型参数
 */
public abstract class AbstractCalculator<T> implements Calculator<T> {
    protected final ExpressionNormalizer normalizer;
    protected Evaluator<T> evaluator;
    protected Map<String ,Operator<T>> operators;

    public AbstractCalculator(Map<String, Operator<T>> operators,Evaluator<T> evaluator) {
        this.normalizer = ExpressionNormalizer.getInstance();
        this.operators = operators;
        this.evaluator = evaluator;
        evaluator.setCalculator(this);
    }

    @Override
    public T calculate(String expression) {
        String normalized = normalizer.normalize(expression);
        return evaluator.evaluate(normalized);
    }

    @Override
    public Map<String, Operator<T>> getOperators() {
        return operators;
    }
}
