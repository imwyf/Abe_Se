package com.duwei.common.caculator;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 后缀表达式计算规则
 *
 * @param <T> 类型参数
 */
class InfixEvaluator<T> extends AbstractEvaluator<T> {

    public InfixEvaluator() {
    }

    public T evaluate(String expression) {
        Deque<T> values = new LinkedList<>();
        Deque<String> ops = new LinkedList<>();
        String[] tokens = expression.split("\\s+");
        for (String token : tokens) {
            if (token.equals("(")) {
                ops.push(token);
            } else if (token.equals(")")) {
                while (!ops.peek().equals("(")) {
                    values.push(apply(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            }else if (operators.containsKey(token)){
                while (!ops.isEmpty() && !ops.peek().equals("(") &&
                        operators.get(ops.peek()).getPrecedence() >= operators.get(token).getPrecedence()){
                    values.push(apply(ops.pop(),values.pop(),values.pop()));
                }
                ops.push(token);
            }else {
                values.push(parser(token));
            }
        }
        while (!ops.isEmpty()) {
            values.push(apply(ops.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

}