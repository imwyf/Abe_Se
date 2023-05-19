package com.duwei.text.transportable;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.text.FinalCiphertext;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text.transportable
 * @Author: duwei
 * @Date: 2023/5/10 16:33
 * @Description: 可传输的密文
 */
@Data
@NoArgsConstructor
public class TransportableFinalCiphertext implements Serializable {
    private AccessPolicy accessPolicy;
    private byte[] C;
    private byte[] C1;
    private Map<String ,byte[]> C_map = new HashMap<>();
    private Map<String ,byte[]> D_map = new HashMap<>();


    public TransportableFinalCiphertext(FinalCiphertext finalCiphertext){
        this.accessPolicy = finalCiphertext.getAccessPolicy();
        this.C = finalCiphertext.getC().toBytes();
        this.C1 = finalCiphertext.getC1().toBytes();
        Map<String, Element> d_map = finalCiphertext.getD_map();
        finalCiphertext.getC_map().forEach((attributeName,Ci) -> {
            C_map.put(attributeName,Ci.toBytes());
            D_map.put(attributeName,d_map.get(attributeName).toBytes());
        });
    }
}
