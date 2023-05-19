package com.duwei.util;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.util.List;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.util
 * @Author: duwei
 * @Date: 2023/5/10 11:22
 * @Description: 一些通用计算功能
 */
public class ComputeUtils {

    //求解C_n_m
    public static int permutationNumber(int n, int m) {
        if (n < 1 || m < 0 || m > n) {
            throw new IllegalArgumentException();
        }
        if (m == 0 || m == n) {
            return 1;
        }
        int up = 1;
        int down = 1;
        int temp;
        for (int i = n, j = m; j >= 1; i--, j--) {
            up *= i;
            down *= j;
            temp = gcd(up, down);
            up /= temp;
            down /= temp;
        }
        return up / down;
    }

    /**
     * 欧几里得算法求最大公约数
     *
     * @param a 第一个元素
     * @param b 第二个元素,要求小于a
     * @return 最大公约数
     */
    public static int gcd(int a, int b) {
        int temp;
        while (b != 0) {
            temp = a;
            a = b;
            b = temp % b;
        }
        return a;
    }


    /**
     * 计算所有元素的乘积
     * @param elements
     * @return
     */
    public static Element mul(List<Element> elements) {
        if (elements == null || elements.size() == 0) {
            throw new IllegalArgumentException();
        }
        Field field = elements.get(0).getField();
        Element result = field.newOneElement();
        for (Element element : elements) {
            result.mul(element);
        }
        return result.getImmutable();
    }



}
