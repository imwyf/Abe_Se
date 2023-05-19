package com.duwei.key;

import com.duwei.key.transportable.TransportableUserPrivateKey;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.common.key
 * @Author: duwei
 * @Date: 2023/5/9 16:40
 * @Description: 属性密钥
 */
@Data
@NoArgsConstructor
public class UserPrivateKey {
    private Element K;
    private Element K1;
    private Element K2;
    private Element K3;
    private Element K4;
    private Element K5;
    private Map<String, Element> Ki_map = new HashMap<>();

    public UserPrivateKey(Element k, Element k1, Element k2, Element k3, Element k4, Element k5, Map<String, Element> ki_map) {
        K = k;
        K1 = k1;
        K2 = k2;
        K3 = k3;
        K4 = k4;
        K5 = k5;
        Ki_map = ki_map;
    }

    public static UserPrivateKey reBuild(TransportableUserPrivateKey transportableUserPrivateKey, Field Zp, Field G0) {
        UserPrivateKey userPrivateKey = new UserPrivateKey();
        userPrivateKey.setK(G0.newElementFromBytes(transportableUserPrivateKey.getK()).getImmutable());
        userPrivateKey.setK1(G0.newElementFromBytes(transportableUserPrivateKey.getK1()).getImmutable());
        userPrivateKey.setK2(Zp.newElementFromBytes(transportableUserPrivateKey.getK2()).getImmutable());
        userPrivateKey.setK3(G0.newElementFromBytes(transportableUserPrivateKey.getK3()).getImmutable());
        userPrivateKey.setK4(G0.newElementFromBytes(transportableUserPrivateKey.getK4()).getImmutable());
        userPrivateKey.setK5(G0.newElementFromBytes(transportableUserPrivateKey.getK5()).getImmutable());
        userPrivateKey.setKi_map(new HashMap<>());
        transportableUserPrivateKey.getKi_map().forEach(
                (attributeName, attributePrivateKey)
                        -> userPrivateKey.getKi_map().put(attributeName, G0.newElementFromBytes(attributePrivateKey).getImmutable())
        );
        return userPrivateKey;
    }


    @Override
    public String toString() {
        return "AttributeKey{" +
                "\n\tK=" + K +
                ", \n\tK1=" + K1 +
                ", \n\tK2=" + K2 +
                ", \n\tK3=" + K3 +
                ", \n\tK4=" + K4 +
                ", \n\tKi_map=" + Ki_map +
                '}';
    }
}
