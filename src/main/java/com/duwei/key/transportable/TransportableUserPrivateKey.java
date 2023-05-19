package com.duwei.key.transportable;

import com.duwei.key.UserPrivateKey;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.key
 * @Author: duwei
 * @Date: 2023/5/9 16:44
 * @Description: 可传输的用户属性密钥
 */
@Data
@NoArgsConstructor
public class TransportableUserPrivateKey implements Serializable {
    private byte[] K;
    private byte[] K1;
    private byte[] K2;
    private byte[] K3;
    private byte[] K4;
    private byte[] K5;
    private Map<String, byte[]> Ki_map = new HashMap<>();

    public TransportableUserPrivateKey(
        UserPrivateKey userPrivateKey
    ){
        this.K = userPrivateKey.getK().toBytes();
        this.K1 = userPrivateKey.getK1().toBytes();
        this.K2 = userPrivateKey.getK2().toBytes();
        this.K3 = userPrivateKey.getK3().toBytes();
        this.K4 = userPrivateKey.getK4().toBytes();
        this.K5 = userPrivateKey.getK5().toBytes();
        userPrivateKey.getKi_map().forEach((attributeName,attributePrivateKey) -> {
            this.Ki_map.put(attributeName,attributePrivateKey.toBytes());
        });
    }

    @Override
    public String toString() {
        return "TransportableAttributeKey{" +
                "\n\tK=" + Arrays.toString(K) +
                ", \n\tK1=" + Arrays.toString(K1) +
                ", \n\tK2=" + Arrays.toString(K2) +
                ", \n\tK3=" + Arrays.toString(K3) +
                ", \n\tK4=" + Arrays.toString(K4) +
                ", \n\tKi_map=" + Ki_map +
                '}';
    }
}
