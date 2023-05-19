package com.duwei.text;


import com.duwei.param.PublicParams;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text.transportable
 * @Author: duwei
 * @Date: 2023/5/10 16:50
 * @Description: 中间解密密文
 */
@Data
@NoArgsConstructor
public class IntermediateDecCiphertext {
    private Element C;
    private Element C1;
    private Element PCT;

    public IntermediateDecCiphertext(Element c, Element c1, Element PCT) {
        C = c;
        C1 = c1;
        this.PCT = PCT;
    }

    public static IntermediateDecCiphertext rebuild(TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext,
                                                    PublicParams publicParams) {
        Field G0 = publicParams.getCurveElementParams().getG0();
        Field GT = publicParams.getCurveElementParams().getGT();
        IntermediateDecCiphertext intermediateDecCiphertext = new IntermediateDecCiphertext();
        intermediateDecCiphertext.setC(GT.newElementFromBytes(transportableIntermediateDecCiphertext.getC()).getImmutable());
        intermediateDecCiphertext.setC1(G0.newElementFromBytes(transportableIntermediateDecCiphertext.getC1()).getImmutable());
        intermediateDecCiphertext.setPCT(GT.newElementFromBytes(transportableIntermediateDecCiphertext.getPCT()).getImmutable());
        return intermediateDecCiphertext;
    }
}
