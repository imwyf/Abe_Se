package com.duwei.key.transportable;

import com.duwei.key.ConversionKey;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.key
 * @Author: duwei
 * @Date: 2023/5/10 15:46
 * @Description: 可传输的转换密钥
 */
@Data
@NoArgsConstructor
public class TransportableConversionKey implements Serializable {
    private byte[] K6;
    private byte[] K5;
    private Map<String,byte[]> attributeMapKi;

    public TransportableConversionKey(ConversionKey conversionKey){
        this.K6 = conversionKey.getK6().toBytes();
        this.K5 = conversionKey.getK5().toBytes();
        this.attributeMapKi = new HashMap<>();
        conversionKey.getAttributeMapKi().forEach((attributeName,Ki) ->{
            attributeMapKi.put(attributeName,Ki.toBytes());
        });
    }
}
