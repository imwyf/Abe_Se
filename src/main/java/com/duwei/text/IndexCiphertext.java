package com.duwei.text;

import com.duwei.param.PublicParams;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text
 * @Author: duwei
 * @Date: 2023/5/9 17:50
 * @Description: 加密索引
 */
@Data
@NoArgsConstructor
public class IndexCiphertext {
    private List<Element> W_list;
    private Element W1;
    private Element W2;
    private Element W3;


    public IndexCiphertext(List<Element> w_list, Element w1, Element w2, Element w3) {
        W_list = w_list;
        W1 = w1;
        W2 = w2;
        W3 = w3;
    }

    public static IndexCiphertext reBuild(TransportableIndexCiphertext transportableIndexCiphertext, PublicParams publicParams){
        Field G0 = publicParams.getCurveElementParams().getG0();
        IndexCiphertext indexCiphertext = new IndexCiphertext();
        Element W1 = G0.newElementFromBytes(transportableIndexCiphertext.getW1()).getImmutable();
        Element W2 = G0.newElementFromBytes(transportableIndexCiphertext.getW2()).getImmutable();
        Element W3 = G0.newElementFromBytes(transportableIndexCiphertext.getW3()).getImmutable();
        List<Element> W_list = transportableIndexCiphertext.getW_list().stream().map(bytes -> G0.newElementFromBytes(bytes).getImmutable()).collect(Collectors.toList());
        indexCiphertext.setW1(W1);
        indexCiphertext.setW2(W2);
        indexCiphertext.setW3(W3);
        indexCiphertext.setW_list(W_list);
        return indexCiphertext;
    }
}
