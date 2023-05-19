package com.duwei.access.lsss;

import com.duwei.access.lsss.lw10.LSSSLW10Engine;
import com.duwei.access.parser.ParserUtils;
import com.duwei.access.parser.PolicySyntaxException;
import com.duwei.common.caculator.Calculators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;



/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.util
 * @Author: duwei
 * @Date: 2023/5/11 10:46
 * @Description: 判断给定的属性是否满足访问策略
 */
public class Policies {

    /**
     * 对客户端传入的策略表达式进行标准化
     */
    private static String policyExpressionNormalize(String accessPolicyExpression) {
        accessPolicyExpression = accessPolicyExpression.replaceAll("\\(", " ( ");
        accessPolicyExpression = accessPolicyExpression.replaceAll("\\)", " ) ");
        return accessPolicyExpression;
    }

    /**
     * 判断属性集合是否满足策略表达式
     */
    public static boolean isSatisfyAccessPolicy(String accessPolicyExpression, Set<String> attributeSet, boolean normalize) {
        if (normalize) {
            accessPolicyExpression = policyExpressionNormalize(accessPolicyExpression);
        }
        String logicExpression = expressionParsing(accessPolicyExpression, attributeSet);
        return Calculators.integerInfixCalculator().calculate(logicExpression) == 1;
    }


    //LSSS方案引擎
    static LSSSLW10Engine lssslw10Engine = LSSSLW10Engine.getInstance();

    /**
     * 根据策略表达式得到访问控制结构
     */
    public static AccessPolicy getAccessPolicy(String policyExpression) {
        try {
            policyExpression = policyExpressionNormalize(policyExpression);
            int[][] accessPolicy = ParserUtils.GenerateAccessPolicy(policyExpression);
            String[] rhos = ParserUtils.GenerateRhos(policyExpression);
            LSSSPolicyParameter lsssPolicyParameter = (LSSSPolicyParameter) lssslw10Engine.generateAccessControl(accessPolicy, rhos);
            AccessPolicy access = new AccessPolicy();
            access.setAccessPolicyExpression(policyExpression);
            access.setMatrix(lsssPolicyParameter.getLSSSMatrix());
            Map<Integer, String> attributeMap = new HashMap<>();
            String[] robs_new = lsssPolicyParameter.getRhos();
            for (int i = 0; i < robs_new.length; i++) {
                attributeMap.put(i, robs_new[i]);
            }
            access.setRowMapAttribute(attributeMap);
            return access;
        } catch (PolicySyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将访问策略和用户属性转化为逻辑表达式
     */
    private static String expressionParsing(String policy, Set<String> attributeSet) {
        StringBuilder express = new StringBuilder();
        for (String str : policy.split("\\s+")) {
            if (str.equals(")") || str.equals("(")) {
                express.append(str);
                continue;
            }
            if (str.equals("or")) {
                express.append("|");
                continue;
            }
            if (str.equals("and")) {
                express.append("&");
                continue;
            }

            if (attributeSet.contains(str)) {
                express.append("1");
            } else {
                express.append("0");
            }
        }
        return express.toString();
    }




}
