package com.duwei;

import com.duwei.entity.DataConsumer;
import com.duwei.entity.TA;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟DataConsumer
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_DataConsumer {
    public static void main(String[] args) {
        System.out.println("-----------> 我是DataConsumer");


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

        // 接收公共参数和密钥
        TransportablePublicParams transportablePublicParams= (TransportablePublicParams) dataConsumer.getTcp().receiveObj(8080);
        TransportableUserPrivateKey transportableUserPrivateKey= (TransportableUserPrivateKey) dataConsumer.getTcp().receiveObj(8084);
        //根据传输过来的公共参数和密钥构建对象
        dataConsumer.buildPublicParams(transportablePublicParams);
        dataConsumer.buildUserPrivateKey(transportableUserPrivateKey);

        //6.数据使用者查询关键字对应的密文
        //关键字集合
        Set<String> keyWordSearch = new HashSet<>();
        keyWordSearch.add("fed");
        keyWordSearch.add("ieee");
        //生成可传输的搜索陷门传输到云服务器
        TransportableSearchTrapdoor transportableSearchTrapdoor = dataConsumer.generateTransportableSearchTrapdoor(keyWordSearch);
        dataConsumer.getTcp().sendObj(8086,"localhost",transportableSearchTrapdoor);
        System.out.println("------>6.数据使用者查询关键字对应的密文ok");

        //8.搜索到之后，数据使用者生成可传输的转化密钥传送到边缘节点
        //同时将服务器发回来的密文也传输到边缘节点
        TransportableConversionKey transportableConversionKey = dataConsumer.generateTransportableConversionKey();
        TransportableFinalCiphertext transportableFinalCiphertext1 = (TransportableFinalCiphertext) dataConsumer.getTcp().receiveObj(8087);
        dataConsumer.getTcp().sendObj(8088,"localhost",transportableConversionKey);
        dataConsumer.getTcp().sendObj(8098,"localhost",transportableFinalCiphertext1);
        System.out.println("------->8.搜索到之后，数据使用者生成可传输的转化密钥传送到边缘节点ok");
        //11.数据使用者进行本地解密
        TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext = (TransportableIntermediateDecCiphertext) dataConsumer.getTcp().receiveObj(8088);
        String decrypt = dataConsumer.decrypt(transportableIntermediateDecCiphertext);
        System.out.println("解密消息：" + decrypt);
    }
}
