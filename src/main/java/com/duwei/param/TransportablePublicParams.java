package com.duwei.param;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class TransportablePublicParams implements Serializable {
    private CurveMetaProperties curveMetaProperties;
    private byte[] generator;
    private byte[] eggAlpha;
    private byte[] gBeta;
    private byte[] gMul;
    private Map<String, byte[]> attributePublicKey;

    public TransportablePublicParams( PublicParams publicParams)  {
        this.curveMetaProperties = publicParams.getCurveMetaProperties();
        this.generator = publicParams.getGenerator().toBytes();
        this.eggAlpha = publicParams.getEggAlpha().toBytes();
        this.gBeta = publicParams.getGBeta().toBytes();
        this.gMul = publicParams.getGMul().toBytes();
        this.attributePublicKey = new HashMap<>();
        publicParams.getAttributePublicKey().forEach((attributeName, attributePublicKey) -> {
            this.attributePublicKey.put(attributeName, attributePublicKey.toBytes());
        });
    }
}
