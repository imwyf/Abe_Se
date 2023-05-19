package com.duwei.common.caculator;

/**
 * 二元位操作符
 */
public enum BinaryOperator implements Operator<Integer> {
    AND("&", 2) {
        @Override
        public Integer apply(Integer val1, Integer val2) {
            return val1 & val2;
        }
    },
    OR("|", 1) {
        @Override
        public Integer apply(Integer val1, Integer val2) {
            return val1 | val2;
        }
    },
    XOR("^", 3) {
        @Override
        public Integer apply(Integer val1, Integer val2) {
            return val1 ^ val2;
        }
    };

    private final String symbol;
    private final int precedence;

    BinaryOperator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    @Override
    public int getPrecedence() {
        return this.precedence;
    }

    public String getSymbol() {
        return this.symbol;
    }
}