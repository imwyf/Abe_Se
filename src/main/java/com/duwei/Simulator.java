package com.duwei;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.access.lsss.Policies;
import com.duwei.entity.*;
import com.duwei.key.ConversionKey;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.FinalCiphertext;
import com.duwei.text.SearchTrapdoor;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;


import java.util.HashSet;
import java.util.Set;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei
 * @Author: duwei
 * @Date: 2023/5/9 17:23
 * @Description: 本地进行验证
 */
public class Simulator {
    public static void main(String[] args) {
        //1.Ta根据全局属性集合，设置公共参数
        TA ta = new TA();
        //全局属性集合
        Set<String> attributes = new HashSet<>();
        attributes.add("A");
        attributes.add("B");
        attributes.add("C");
        attributes.add("D");
        attributes.add("E");
        attributes.add("F");
        attributes.add("G");
        attributes.add("H");
        //TA进行初始化
        ta.setUp(attributes);

        //2.TA将公共参数传输到其他实体，由于公共参数的类不能直接传输，所以需要转换
        //可传输的公共参数，调用TA的getTransportablePublicParams()获取可传输的公共参数
        TransportablePublicParams transportablePublicParams = ta.getTransportablePublicParams();

        //3.ta为数据使用者生成属性私钥，需要先初始化用户的属性
        //创建用户
        DataConsumer dataConsumer = new DataConsumer();
        //用户属性集合
        Set<String> userAttributes = new HashSet<>();
        userAttributes.add("D");
        userAttributes.add("B");
        userAttributes.add("A");
        userAttributes.add("C");
        userAttributes.add("F");
        userAttributes.add("H");
        //调用TA的keyGenTransportable()生成可以传输的用户密钥，然后将其传输到用户
        TransportableUserPrivateKey transportableUserPrivateKey = ta.keyGenTransportable(userAttributes);
        //根据传输过来的公共参数和密钥构建对象
        dataConsumer.buildPublicParams(transportablePublicParams);
        dataConsumer.buildUserPrivateKey(transportableUserPrivateKey);

        //4. 数据拥有者本地构建访问策略，加密数据
        DataOwner dataOwner = new DataOwner();
        //首先构建公共参数
        dataOwner.buildPublicParams(transportablePublicParams);
        //消息以及关键字关键字
        String message = "deepl deepl student ieee sci abcd public private";
        System.out.println("原始消息：" + message);
        Set<String> keywords = new HashSet<>();
        keywords.add("deepl");
        keywords.add("fed");
        keywords.add("student");
        keywords.add("sci");
        keywords.add("ieee");
        //先进行离线计算
        dataOwner.offlineEnc();
        //接下来加密索引，生成可以传输的索引密文
        TransportableIndexCiphertext transportableIndexCiphertext = dataOwner.keywordEncToTransportableIndexCiphertext(keywords);
        //构建策略表达式
        String accessExpression = "( A and B and C ) and ( D or E ) and ( G or H )";
        //根据策略表达式生成访问策略
        AccessPolicy accessPolicy = Policies.getAccessPolicy(accessExpression);
        //数据拥有者设置自己的访问策略
        dataOwner.setAccessPolicy(accessPolicy);
        //根据设置的访问策略对消息加密，生成可以传输的密文
        TransportableFinalCiphertext transportableFinalCiphertext = dataOwner.msgEncToTransportableFinalCiphertext(message);
        //todo:数据使用将可传输的密文索引和可传输的密文发送到云存储服务器

        //5.初始化云存储服务器，存储数据拥有者发来的索引和密文
        //首先需要将可传输的索引和密文变为正常的索引和密文
        CloudServer cloudServer = new CloudServer();
        cloudServer.buildPublicParams(transportablePublicParams);
        cloudServer.store(transportableIndexCiphertext, transportableFinalCiphertext);

        //6.数据使用者查询关键字对应的密文
        //关键字集合
        Set<String> keyWordSearch = new HashSet<>();
        keyWordSearch.add("fed");
        keyWordSearch.add("ieee");
        //keyWordSearch.add("3333");
        //todo:生成可传输的搜索陷门传输到云服务器
        TransportableSearchTrapdoor transportableSearchTrapdoor = dataConsumer.generateTransportableSearchTrapdoor(keyWordSearch);

        //7.云服务器首先重构搜索陷门
        SearchTrapdoor searchTrapdoor = SearchTrapdoor.rebuild(transportableSearchTrapdoor, cloudServer.getPublicParams());
        //云服务器遍历自己所存储的索引密文，看是否能够搜索到
        TransportableFinalCiphertext transportableFinalCiphertext1 = cloudServer.checkSearchTrapdoor(searchTrapdoor);
        if (transportableFinalCiphertext1 == null) {
            System.out.println("没有匹配的关键字");
            return;
        }
        //todo:搜索成功后云服务器将对应的密文拿出来传输给用户


        //8.搜索到之后，数据使用者生成可传输的转化密钥传送到边缘节点
        //同时将服务器发回来的密文也传输到边缘节点
        //todo:传送过程
        TransportableConversionKey transportableConversionKey = dataConsumer.generateTransportableConversionKey();

        //9.边缘节点判断是否满足属性
        EdgeNode edgeNode = new EdgeNode();
        edgeNode.buildPublicParams(transportablePublicParams);
        //首先重构密文和转化密钥
        ConversionKey conversionKey = ConversionKey.rebuild(transportableConversionKey, edgeNode.getPublicParams());
        FinalCiphertext finalCiphertext = FinalCiphertext.rebuild(transportableFinalCiphertext1, edgeNode.getPublicParams());
        boolean isSatisfyAccessPolicy = edgeNode.isSatisfyAccessPolicy(finalCiphertext, conversionKey);
        if (!isSatisfyAccessPolicy) {
            System.out.println("属性匹配失败");
            return;
        }
        //10.边缘节点进行部分解密，生成部分解密密文传输到数据使用者
        //todo:传送
        TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext = edgeNode.partialDec(finalCiphertext, conversionKey);

        //11.数据使用者进行本地解密
        String decrypt = dataConsumer.decrypt(transportableIntermediateDecCiphertext);
        System.out.println("解密消息：" + decrypt);
    }

}
