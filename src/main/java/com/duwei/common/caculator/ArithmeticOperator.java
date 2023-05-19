package com.duwei.common.caculator;

/**
 * 算数操作符
 */
enum ArithmeticOperator implements Operator<Double> {
    ADD("+", 4) {
        @Override
        public Double apply(Double val1, Double val2) {
            return val1 + val2;
        }
    },
    SUB("-", 4) {
        @Override
        public Double apply(Double val1, Double val2) {
            return val1 - val2;
        }
    },
    MUL("*", 5) {
        @Override
        public Double apply(Double val1, Double val2) {
            return val1 * val2;
        }
    },
    DIV("/", 5) {
        @Override
        public Double apply(Double val1, Double val2) {
            if (val2 == 0) {
                throw new IllegalArgumentException("Division by zero");
            }
            return val1 / val2;
        }
    },
    POW("**", 6) {
        @Override
        public Double apply(Double val1, Double val2) {
            return Math.pow(val1, val2);
        }
    };

    private final String symbol;
    private final int precedence;

    ArithmeticOperator(String symbol, int precedence ) {
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