package com.duwei.common.caculator;

import java.util.Map;

/**
 * 计算器接口
 * @param <T>  类型参数
 */
public interface Calculator<T> {
    T calculate(String expression);

    //对字符串操作数进行解析
    T parserOperand(String  op);

    Map<String,Operator<T>> getOperators();
}
