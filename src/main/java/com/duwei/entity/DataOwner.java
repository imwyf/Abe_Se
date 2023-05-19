package com.duwei.entity;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.param.PublicParams;
import com.duwei.text.FinalCiphertext;
import com.duwei.text.IndexCiphertext;
import com.duwei.text.IntermediateCiphertext;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import com.duwei.util.ElementUtils;
import com.duwei.util.TCPUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.entity
 * @Author: duwei
 * @Date: 2023/5/9 17:34
 * @Description: 数据拥有者
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataOwner extends AbstractEntity {
    private IntermediateCiphertext intermediateCiphertext;
    private AccessPolicy accessPolicy;
    TCPUtils tcp = new TCPUtils(); // tcp通信模块

    /**
     * 数据拥有者离线计算中间密文
     */
    public void offlineEnc() {
        PublicParams publicParams = getPublicParams();
        Field Zp = publicParams.getCurveElementParams().getZ();
        Element generator = publicParams.getGenerator();
        Element eggAlpha = publicParams.getEggAlpha();
        Map<String, Element> attributePublicKey = publicParams.getAttributePublicKey();
        Element s = Zp.newRandomElement().getImmutable();
        Element C0 = eggAlpha.powZn(s).getImmutable();
        Element C1 = generator.powZn(s).getImmutable();
        Map<String, Element> Ci_pie_map = new HashMap<>();
        Map<String, Element> Di_pie_map = new HashMap<>();
        attributePublicKey.forEach((attributeName, attributeTi) -> {
            Element ri = Zp.newRandomElement().getImmutable();
            Element Ci_pie = attributeTi.powZn(Zp.newZeroElement().sub(ri)).getImmutable();
            Element Di_pie = generator.powZn(ri).getImmutable();
            Ci_pie_map.put(attributeName, Ci_pie);
            Di_pie_map.put(attributeName, Di_pie);
        });
        intermediateCiphertext = new IntermediateCiphertext(s, C0, C1, Ci_pie_map, Di_pie_map);
    }


    public TransportableFinalCiphertext msgEncToTransportableFinalCiphertext(String message) {
        return new TransportableFinalCiphertext(msgEnc(message));
    }

    /**
     * 对原始数据进行加密
     *
     * @param message 原始消息
     * @return 加密后的最终密文
     */
    public FinalCiphertext msgEnc(String message) {
        Field Zp = getPublicParams().getCurveElementParams().getZ();
        Field GT = getPublicParams().getCurveElementParams().getGT();
        Element gMul = getPublicParams().getGMul();
        Element C0 = getIntermediateCiphertext().getC0();
        Element C1 = getIntermediateCiphertext().getC1();
        Map<String, Element> Ci_pie_map = getIntermediateCiphertext().getCi_pie_map();
        Map<String, Element> Di_pie_map = getIntermediateCiphertext().getDi_pie_map();
        int[][] accessMatrix = this.accessPolicy.getMatrix();
        Map<Integer, String> rowMapAttribute = this.accessPolicy.getRowMapAttribute();
        int l = accessMatrix.length;
        int n = accessMatrix[0].length;

        //初始化v向量
        Element[] vector = new Element[n];
        vector[0] = getIntermediateCiphertext().getS();
        for (int i = 1; i < n; i++) {
            vector[i] = Zp.newRandomElement().getImmutable();
        }

        //计算C
        Element m = ElementUtils.messageToField(message, GT);
        Element C = C0.mul(m).getImmutable();

        //计算Ci,Di
        Map<String, Element> C_map = new HashMap<>();
        Map<String, Element> D_map = new HashMap<>();

        for (int i = 0; i < l; i++) {
            Element lambda_i = ElementUtils.vectorMul(accessMatrix[i], vector);
            String attributeName = rowMapAttribute.get(i);
            Element Ci_pie = Ci_pie_map.get(attributeName);
            Element Di_pie = Di_pie_map.get(attributeName);
            Element Di = Di_pie.duplicate().getImmutable();
            Element Ci = gMul.powZn(lambda_i).getImmutable().mul(Ci_pie).getImmutable();
            D_map.put(attributeName, Di);
            C_map.put(attributeName, Ci);
        }
        return new FinalCiphertext(accessPolicy, C, C1, C_map, D_map);
    }


    /**
     * 生成可以传输的加密索引
     *
     * @param keywords 关键词集合
     * @return 可传输的索引密文
     */
    public TransportableIndexCiphertext keywordEncToTransportableIndexCiphertext(Set<String> keywords) {
        return new TransportableIndexCiphertext(keywordEnc(keywords));
    }

    /**
     * 生成加密索引
     *
     * @param keywords 关键字集合
     * @return 加密索引
     */
    private IndexCiphertext keywordEnc(Set<String> keywords) {
        Field Zp = getPublicParams().getCurveElementParams().getZ();
        Element generator = getPublicParams().getGenerator();
        Element gBeta = getPublicParams().getGBeta();
        Element gMul = getPublicParams().getGMul();
        Element r = Zp.newRandomElement().getImmutable();
        Element r1 = Zp.newRandomElement().getImmutable();
        Element W1 = generator.powZn(r1).getImmutable();
        Element W2 = gBeta.powZn(r1).getImmutable();
        Element W3 = gMul.powZn(r).getImmutable();
        List<Element> W_list = new ArrayList<>();
        Element gBeta_r = gBeta.powZn(r).getImmutable();
        for (String keyword : keywords) {
            Element Wj = gBeta_r.powZn(ElementUtils.hashMapToField(keyword, Zp)).getImmutable();
            W_list.add(Wj);
        }
        return new IndexCiphertext(W_list, W1, W2, W3);
    }

}
