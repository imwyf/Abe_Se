package com.duwei.entity;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.access.lsss.Policies;
import com.duwei.key.ConversionKey;
import com.duwei.text.FinalCiphertext;
import com.duwei.text.IntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.util.ComputeUtils;
import com.duwei.util.MathUtils;
import com.duwei.util.TCPUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.*;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.entity
 * @Author: duwei
 * @Date: 2023/5/10 16:09
 * @Description: 边缘节点
 */
public class EdgeNode extends AbstractEntity {
    public TCPUtils tcp = new TCPUtils(); // tcp通信模块

    /**
     * 跟看v密文和转换密钥进行部分解密,判断能否满足访问控制策略
     * 如果返回null则代表不满足访问策略
     *
     * @param ciphertext    密文
     * @param conversionKey 转换密钥
     * @return 是否满足访问策略
     */
    public TransportableIntermediateDecCiphertext partialDec(FinalCiphertext ciphertext, ConversionKey conversionKey) {
        AccessPolicy accessPolicy = ciphertext.getAccessPolicy();
        Set<String> userAttributes = conversionKey.getAttributeMapKi().keySet();
        SortedSet<Integer> satisfyAccessControlMatrixIndex = satisfyAccessControlMatrixIndex(accessPolicy, userAttributes);
        Element PCT = generatePartialCiphertext(satisfyAccessControlMatrixIndex, accessPolicy, userAttributes, ciphertext, conversionKey);
        Element C = ciphertext.getC();
        Element C1 = ciphertext.getC1();
        return new TransportableIntermediateDecCiphertext(new IntermediateDecCiphertext(C, C1, PCT));
    }

    public boolean isSatisfyAccessPolicy(FinalCiphertext ciphertext, ConversionKey conversionKey) {
        return Policies.isSatisfyAccessPolicy(ciphertext.getAccessPolicy().getAccessPolicyExpression(), conversionKey.getAttributeMapKi().keySet(), false);
    }

    public boolean isSatisfyAccessPolicy(AccessPolicy accessPolicy, Set<String> attributeNames) {
        return Policies.isSatisfyAccessPolicy(accessPolicy.getAccessPolicyExpression(), attributeNames, false);
    }

    /**
     * 判断用户满足访问控制矩阵的哪些行
     *
     * @param accessPolicy   访问控制策略
     * @param attributeNames 用户属性集
     * @return 满足矩阵中的索引
     */
    private SortedSet<Integer> satisfyAccessControlMatrixIndex(AccessPolicy accessPolicy, Set<String> attributeNames) {
        SortedSet<Integer> set = new TreeSet<>();
        accessPolicy.getRowMapAttribute().forEach((index, attributeName) -> {
            if (attributeNames.contains(attributeName)) {
                set.add(index);
            }
        });
        return set;
    }

    /**
     * 计算密文PCT
     *
     * @param satisfyIndex    属性满足访问控制矩阵的行索引集合
     * @param accessPolicy    访问控制策略
     * @param attributeNames  用户属性
     * @param finalCiphertext 密文
     * @param conversionKey   转换密钥
     * @return PCT
     */
    private Element generatePartialCiphertext(SortedSet<Integer> satisfyIndex, AccessPolicy accessPolicy,
                                              Set<String> attributeNames, FinalCiphertext finalCiphertext,
                                              ConversionKey conversionKey) {
        Pairing pairing = getPublicParams().getCurveElementParams().getPairing();
        Field GT = getPublicParams().getCurveElementParams().getGT();
        Element C1 = finalCiphertext.getC1();
        Element K6 = conversionKey.getK6();
        Element K5 = conversionKey.getK5();

        Map<String, Element> solveOmega = solveOmega(satisfyIndex, accessPolicy);
        Element up = pairing.pairing(C1, K6).getImmutable();

        Set<String> accessAttributes = finalCiphertext.getC_map().keySet();
        List<Element> accList = new ArrayList<>();
        for (String attribute : attributeNames) {
            if (accessAttributes.contains(attribute)) {
                Element Ci = finalCiphertext.getC_map().get(attribute);
                Element Di = finalCiphertext.getD_map().get(attribute);
                Element Wi = solveOmega.get(attribute);
                Element Ki = conversionKey.getAttributeMapKi().get(attribute);
                Element left = pairing.pairing(Ci, K5).getImmutable();
                Element right = pairing.pairing(Ki, Di).getImmutable();
                Element down = (left.mul(right).getImmutable()).powZn(Wi).getImmutable();
                accList.add(down);
            }
        }
        Element down = ComputeUtils.mul(accList);
        return up.div(down).getImmutable();
    }

    private Map<String, Element> solveOmega(SortedSet<Integer> satisfyIndex, AccessPolicy accessPolicy) {
        int[][] matrix = accessPolicy.getMatrix();
        Map<Integer, String> rowMapAttribute = accessPolicy.getRowMapAttribute();
        int m = matrix.length;
        int n = matrix[0].length;
        int l = satisfyIndex.size();
        int[][] coefficients = new int[n][l];
        int next = 0;
        for (Integer index : satisfyIndex) {
            for (int i = 0; i < n; i++) {
                coefficients[i][next] = matrix[index][i];
            }
            next++;
        }
        int[] constants = new int[n];
        constants[0] = 1;
        Arrays.fill(constants, 1, constants.length, 0);
        Element[] solver = MathUtils.solverOnField(coefficients, constants, getPublicParams().getCurveElementParams().getZ());
        next = 0;
        Map<String, Element> w_map = new HashMap<>();
        for (Integer integer : satisfyIndex) {
            w_map.put(rowMapAttribute.get(integer), solver[next++]);
        }
        return w_map;
    }


}
