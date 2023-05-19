package com.duwei.entity;

import com.duwei.key.ConversionKey;
import com.duwei.key.UserPrivateKey;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.text.IntermediateDecCiphertext;
import com.duwei.text.SearchTrapdoor;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;
import com.duwei.util.ElementUtils;
import com.duwei.util.TCPUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.entity
 * @Author: duwei
 * @Date: 2023/5/9 17:24
 * @Description: 数据使用者
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataConsumer extends AbstractEntity {
    private UserPrivateKey userPrivateKey;
    private Element K_tao;
    TCPUtils tcp = new TCPUtils(); // tcp通信模块

    /**
     * 数据使用者构建属性密钥
     *
     * @param transportableUserPrivateKey TA传过来的属性密钥
     */
    public void buildUserPrivateKey(TransportableUserPrivateKey transportableUserPrivateKey) {
        this.userPrivateKey = UserPrivateKey.reBuild(
                transportableUserPrivateKey,
                getPublicParams().getCurveElementParams().getZ(),
                getPublicParams().getCurveElementParams().getG0()
        );
    }


    /**
     * 生成可以传输的转换密钥,将其传输到边缘节点
     *
     * @return  可传输的转换密钥
     */
    public TransportableConversionKey generateTransportableConversionKey() {
        return new TransportableConversionKey(generateConversionKey());
    }

    /**
     * 生成转换密钥
     *
     * @return  转换密钥
     */
    private ConversionKey generateConversionKey() {
        Field Zp = getPublicParams().getCurveElementParams().getZ();
        Element generator = getPublicParams().getGenerator();
        Element tao = Zp.newRandomElement().getImmutable();
        Element K_tao = generator.powZn(tao).getImmutable();
        this.K_tao = K_tao;
        Element K4 = getUserPrivateKey().getK4();
        Element K6 = K_tao.mul(K4).getImmutable();
        Element K5 = getUserPrivateKey().getK5().duplicate();
        Map<String, Element> Ki_map = new HashMap<>(getUserPrivateKey().getKi_map());
        return new ConversionKey(K6, K5, Ki_map);
    }

    /**
     * 根据关键字列表生成可传输搜索陷门
     *
     * @param keywordSet    关键字集合
     * @return  可传输的搜索陷门
     */
    public TransportableSearchTrapdoor generateTransportableSearchTrapdoor(Set<String> keywordSet) {
        return new TransportableSearchTrapdoor(generateSearchTrapdoor(keywordSet));
    }

    /**
     * 根据关键字列表生成搜索陷门
     *
     * @param keywordSet 待搜索关键字集合
     * @return  搜索陷门
     */
    private SearchTrapdoor generateSearchTrapdoor(Set<String> keywordSet) {
        if (keywordSet.size() < 1) {
            throw new IllegalArgumentException("The keyword list cannot be null");
        }
        Field Zp = getPublicParams().getCurveElementParams().getZ();
        Field G0 = getPublicParams().getCurveElementParams().getG0();
        Element q = Zp.newRandomElement().getImmutable();

        Element K = getUserPrivateKey().getK();
        Element T1 = K.powZn(q).getImmutable();

        Element K1 = getUserPrivateKey().getK1();
        Element T2 = K1.powZn(q).getImmutable();

        Element T4 = getPublicParams().getGMul().getImmutable();

        Element gBeta = getPublicParams().getGBeta();
        Element T3 = G0.newOneElement();
        for (String keyword : keywordSet) {
            T3.mul(
                    gBeta.powZn(ElementUtils.hashMapToField(keyword, Zp))
            );
        }
        T3 = T3.getImmutable();
        return new SearchTrapdoor(T1, T2, T3, T4, keywordSet.size());
    }

    public String decrypt(TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext){
        IntermediateDecCiphertext intermediateDecCiphertext = IntermediateDecCiphertext.rebuild(transportableIntermediateDecCiphertext,publicParams);
        return decrypt(intermediateDecCiphertext);
    }

    /**
     * 根据生成的中间数据密文对数据进行解密
     *
     * @param intermediateDecCiphertext 中间数据密文
     * @return 解密数据
     */
    private String decrypt(IntermediateDecCiphertext intermediateDecCiphertext) {
        Pairing pairing = getPublicParams().getCurveElementParams().getPairing();
        Element C1 = intermediateDecCiphertext.getC1();
        Element C = intermediateDecCiphertext.getC();
        Element PCT = intermediateDecCiphertext.getPCT();
        Element K_tao = this.K_tao;
        Element K2 = this.userPrivateKey.getK2();
        Element up = C.mul(
                pairing.pairing(C1, K_tao).powZn(K2)
        ).getImmutable();
        Element down = PCT.powZn(K2).getImmutable();
        Element m = up.div(down).getImmutable();
        return ElementUtils.fieldToMessage(m);
    }
}
