package com.duwei.text;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.param.PublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text
 * @Author: duwei
 * @Date: 2023/5/9 17:50
 * @Description: 最终密文
 */
@Data
@NoArgsConstructor
public class FinalCiphertext {
    private AccessPolicy accessPolicy;
    private Element C;
    private Element C1;
    private Map<String, Element> C_map = new HashMap<>();
    private Map<String, Element> D_map = new HashMap<>();


    public FinalCiphertext(AccessPolicy accessPolicy, Element c, Element c1, Map<String, Element> c_map, Map<String, Element> d_map) {
        this.accessPolicy = accessPolicy;
        C = c;
        C1 = c1;
        C_map = c_map;
        D_map = d_map;
    }

    public static FinalCiphertext rebuild(TransportableFinalCiphertext transportableFinalCiphertext, PublicParams publicParams) {
        Field G0 = publicParams.getCurveElementParams().getG0();
        Field GT = publicParams.getCurveElementParams().getGT();
        FinalCiphertext finalCiphertext = new FinalCiphertext();
        finalCiphertext.setAccessPolicy(transportableFinalCiphertext.getAccessPolicy());
        finalCiphertext.setC(GT.newElementFromBytes(transportableFinalCiphertext.getC()).getImmutable());
        finalCiphertext.setC1(G0.newElementFromBytes(transportableFinalCiphertext.getC1()).getImmutable());
        transportableFinalCiphertext.getC_map().forEach((attributeName, Ci) -> {
            finalCiphertext.getC_map().put(attributeName, G0.newElementFromBytes(transportableFinalCiphertext.getC_map().get(attributeName)).getImmutable());
            finalCiphertext.getD_map().put(attributeName, G0.newElementFromBytes(transportableFinalCiphertext.getD_map().get(attributeName)).getImmutable());

        });
        return finalCiphertext;
    }
}
