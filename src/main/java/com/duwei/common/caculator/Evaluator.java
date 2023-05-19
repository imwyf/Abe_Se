package com.duwei.common.caculator;

/**
 * 对标准化的表达式按照一定规则进行处理
 *
 * @param <T> 类型参数
 */
public interface Evaluator<T> {
    /**
     * 表达式计算
     *
     * @param expression 字符串表达式
     * @return 计算结果
     */
    T evaluate(String expression);

    /**
     * 获取操作符对应的枚举示例
     *
     * @param op 字符串操作符
     * @return 对应的枚举示例
     */
    Operator<T> getOperator(String op);

    T apply(String op, T val1, T val2);

    void setCalculator(Calculator<T> calculator);

}