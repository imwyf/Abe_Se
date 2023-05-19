package com.duwei.entity;

import com.duwei.key.MasterKey;
import com.duwei.key.UserPrivateKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.PublicParams;
import com.duwei.param.TransportablePublicParams;
import com.duwei.util.TCPUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.entity
 * @Author: duwei
 * @Date: 2023/5/9 16:31
 * @Description: 可信授权机构
 */
@Data
@NoArgsConstructor
public class TA extends AbstractEntity{
    //系统主密钥
    private MasterKey masterKey;
    TCPUtils tcp = new TCPUtils(); // tcp通信模块

    /**
     * 系统初始化阶段
     *
     * @param attributeSet 属性集合
     */
    public void setUp(Set<String> attributeSet) {
        this.publicParams = new PublicParams(attributeSet);
        MasterKey masterKey = new MasterKey();
        masterKey.setAlpha(publicParams.getAlpha());
        masterKey.setBeta(publicParams.getBeta());
        this.masterKey = masterKey;
    }


    public TransportablePublicParams getTransportablePublicParams(){
        return new TransportablePublicParams(getPublicParams());
    }

    /**
     * 生成可以传输的用户私钥
     * @param userAttributes    用户属性集合
     * @return  可传输的用户属性私钥
     */
    public TransportableUserPrivateKey keyGenTransportable(Set<String > userAttributes){
        return new TransportableUserPrivateKey(keyGen(userAttributes));
    }

    /**
     * 密钥生成
     *
     * @param userAttributes 用户拥有的属性
     * @return 用户的属性密钥
     */
    public UserPrivateKey keyGen(Set<String> userAttributes) {
        Field Zp = publicParams.getCurveElementParams().getZ();
        final Element x = Zp.newRandomElement().getImmutable();
        final Element alpha = this.masterKey.getAlpha();
        final Element beta = this.masterKey.getBeta();
        final Element generator = this.publicParams.getGenerator();
        final Map<String, Element> attributePublicKey = this.publicParams.getAttributePublicKey();

        //开始生成属性密钥
        Element K = generator.powZn(x.mulZn(beta)).getImmutable();
        Element K1 = generator.powZn(x).getImmutable();
        Element K2 = beta.mul(x.invert()).getImmutable();
        Element K3 = generator.powZn(alpha).getImmutable();

        Element y = Zp.newRandomElement().getImmutable();
        Element gMul = this.publicParams.getGMul();
        Element K4_pie = K3.mul(
                gMul.powZn(y)
        ).getImmutable();
        Element K4 = K4_pie.powZn(K2.invert()).getImmutable();


        Element K5_pie = generator.powZn(y).getImmutable();
        Element K5 = K5_pie.powZn(K2.invert()).getImmutable();

        Map<String,Element> Ki_map = new HashMap<>();
        userAttributes.forEach((attributeName) -> {
            Element Ti = attributePublicKey.get(attributeName);
            Element K_i_pie = Ti.powZn(y).getImmutable();
            Element K_i = K_i_pie.powZn(K2.invert()).getImmutable();
            Ki_map.put(attributeName,K_i);
        });
        return new UserPrivateKey(K,K1,K2,K3,K4,K5,Ki_map);
    }

}
