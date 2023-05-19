package com.duwei.text;

import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text
 * @Author: duwei
 * @Date: 2023/5/9 17:38
 * @Description: 由数据拥有者计算的中间密文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntermediateCiphertext {
    private Element s;
    private Element C0;
    private Element C1;
    private Map<String,Element> Ci_pie_map;
    private Map<String,Element> Di_pie_map;
}
