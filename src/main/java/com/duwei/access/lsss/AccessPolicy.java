package com.duwei.access.lsss;



import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text
 * @Author: duwei
 * @Date: 2023/5/10 16:24
 * @Description: 访问策略
 */
@Data
@NoArgsConstructor
public class AccessPolicy implements Serializable {
    private String accessPolicyExpression;
    private int[][] matrix;
    Map<Integer, String> rowMapAttribute;

    public AccessPolicy( String accessPolicyExpression,int[][] matrix, Map<Integer, String> rowMapAttribute) {
        this.accessPolicyExpression = accessPolicyExpression;
        this.matrix = matrix;
        this.rowMapAttribute = rowMapAttribute;
    }
}

