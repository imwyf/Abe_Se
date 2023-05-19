package com.duwei.common.caculator;

/**
 * 操作符接口
 * @param <T>   类型参数
 */
interface Operator<T> {
    /**
     * 获取操作符优先级
     * @return  整数代表的优先级
     */
    int getPrecedence();

    /**
     * 计算两个操作数的结果
     * @param val1  操作数1
     * @param val2  操作数2
     * @return  计算结果
     */
    T apply(T val1, T val2);
}