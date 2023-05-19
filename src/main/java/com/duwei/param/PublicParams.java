package com.duwei.param;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.param
 * @Author: duwei
 * @Date: 2023/5/9 15:38
 * @Description: 系统的公共参数
 */
@Data
public class PublicParams {
    private CurveMetaProperties curveMetaProperties;
    private CurveElementParams curveElementParams;
    private Element generator;
    private Map<String, Element> attributeMapElement;
    private Map<String, Element> attributePublicKey;
    private Element alpha;
    private Element beta;
    private Element mul;
    private Element eggAlpha;
    private Element gBeta;
    private Element gMul;

    @Override
    public String toString() {
        return "PublicParams{" +
                "\n\tcurveMetaProperties=" + curveMetaProperties +
                ", \n\tgenerator=" + generator +
                ", \n\tattributeMapElement=" + attributeMapElement +
                ", \n\tattributePublicKey=" + attributePublicKey +
                ", \n\teggAlpha=" + eggAlpha +
                ", \n\tgBeta=" + gBeta +
                ", \n\tgMul=" + gMul +
                "\n}";
    }

    private PublicParams(){

    }

    public PublicParams(Set<String> attributeSet) {
        this(new CurveMetaProperties(), attributeSet);
    }


    public PublicParams(CurveMetaProperties curveMetaProperties, Set<String> attributeSet) {
        setup(curveMetaProperties,attributeSet);
    }


    public void setup(CurveMetaProperties curveMetaProperties, Set<String> attributeSet) {
        this.curveMetaProperties = curveMetaProperties;
        this.curveElementParams = CurveElementParams.getInstance(curveMetaProperties);
        Field G0 = curveElementParams.getG0();
        Field Zp = curveElementParams.getZ();
        Pairing pairing = curveElementParams.getPairing();
        this.generator = G0.newRandomElement().getImmutable();
        attributeMapElement = new HashMap<>();
        attributePublicKey = new HashMap<>();
        attributeSet.forEach(attributeName -> {
            Element a_i = Zp.newRandomElement().getImmutable();
            attributeMapElement.put(attributeName, a_i);
            attributePublicKey.put(attributeName, generator.powZn(a_i).getImmutable());
        });
        this.alpha = Zp.newRandomElement().getImmutable();
        this.beta = Zp.newRandomElement().getImmutable();
        this.mul = Zp.newRandomElement().getImmutable();
        this.eggAlpha = pairing.pairing(generator, generator).powZn(alpha).getImmutable();
        this.gBeta = generator.powZn(beta).getImmutable();
        this.gMul = generator.powZn(mul).getImmutable();
    }


    public static PublicParams reBuild(TransportablePublicParams transportablePublicParams){
        PublicParams publicParams = new PublicParams();
        publicParams.curveMetaProperties = transportablePublicParams.getCurveMetaProperties();
        CurveElementParams curveElementParams = CurveElementParams.getInstance(publicParams.getCurveMetaProperties());
        publicParams.curveElementParams = curveElementParams;
        Field G0 = curveElementParams.getG0();
        Field GT = curveElementParams.getGT();
        publicParams.generator = G0.newElementFromBytes(transportablePublicParams.getGenerator()).getImmutable();
        publicParams.eggAlpha = GT.newElementFromBytes(transportablePublicParams.getEggAlpha()).getImmutable();
        publicParams.gMul = G0.newElementFromBytes(transportablePublicParams.getGMul()).getImmutable();
        publicParams.gBeta = G0.newElementFromBytes(transportablePublicParams.getGBeta()).getImmutable();
        Map<String,Element > attributeMap = new HashMap<>();
        transportablePublicParams.getAttributePublicKey().forEach((attributeName,attributePublicKey) -> {
            attributeMap.put(attributeName,G0.newElementFromBytes(attributePublicKey).getImmutable());
        });
        publicParams.setAttributePublicKey(attributeMap);
        return publicParams;
    }


}
