package com.duwei.common.caculator;

/**
 * 表达式规则化
 */
class ExpressionNormalizer {
    // 所有有效的操作符符号
    private static final String OPERATORS = "&|^+-*/()**";
    private static final ExpressionNormalizer INSTANCE = new ExpressionNormalizer();

    public static ExpressionNormalizer getInstance(){
        return INSTANCE;
    }

    // 将用户的输入表达式标准化
    public String normalize(String expression) {
        StringBuilder normalized = new StringBuilder();

        int len = expression.length();
        for (int i = 0; i < len; i++) {
            char c = expression.charAt(i);
            if (OPERATORS.indexOf(c) != -1) {
                // 当前字符是操作符
                if (c == '*' && i < len - 1 && expression.charAt(i + 1) == '*') {
                    // '**'操作符
                    normalized.append(" ** ");
                    i++; // 跳过下一个'*'
                } else {
                    // 其他操作符
                    normalized.append(' ').append(c).append(' ');
                }
            } else {
                // 当前字符不是操作符
                normalized.append(c);
            }
        }

        return normalized.toString().trim().replaceAll("\\s+", " ");
    }

}
