package com.duwei.key;

import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.param.PublicParams;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;

;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.key
 * @Author: duwei
 * @Date: 2023/5/10 15:43
 * @Description: 为边缘节点生成的转换密钥
 */
@Data
@NoArgsConstructor
public class ConversionKey {
    private Element K6;
    private Element K5;
    private Map<String,Element> attributeMapKi;

    public ConversionKey(Element k6, Element k5, Map<String, Element> attributeMapKi) {
        K6 = k6;
        K5 = k5;
        this.attributeMapKi = attributeMapKi;
    }

    public static ConversionKey rebuild(TransportableConversionKey transportableConversionKey, PublicParams publicParams){
        Field G0 = publicParams.getCurveElementParams().getG0();
        ConversionKey conversionKey = new ConversionKey();
        conversionKey.setK5(G0.newElementFromBytes(transportableConversionKey.getK5()).getImmutable());
        conversionKey.setK6(G0.newElementFromBytes(transportableConversionKey.getK6()).getImmutable());
        Map<String,Element> Ki_map = new HashMap<>();
        transportableConversionKey.getAttributeMapKi().forEach((attributeName,Ki) -> {
            Ki_map.put(attributeName,G0.newElementFromBytes(Ki).getImmutable());
        });
        conversionKey.setAttributeMapKi(Ki_map);
        return conversionKey;
    }
}
