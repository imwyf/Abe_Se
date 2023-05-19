package com.duwei;

import com.duwei.entity.CloudServer;
import com.duwei.entity.EdgeNode;
import com.duwei.key.ConversionKey;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.FinalCiphertext;
import com.duwei.text.SearchTrapdoor;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟EdgeNode
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_EdgeNode {
    public static void main(String[] args) {
        System.out.println("-----------> 我是EdgeNode");
        // tcp传输
        // TA -> 8080
        // DataConsumer -> 8081
        // DataOwner -> 8082
        // CloudServer -> 8083
        // EdgeNode -> 8084

        //9.边缘节点判断是否满足属性
        EdgeNode edgeNode = new EdgeNode();
        TransportablePublicParams transportablePublicParams = (TransportablePublicParams) edgeNode.tcp.receiveObj(8083);
        edgeNode.buildPublicParams(transportablePublicParams);
        //首先重构密文和转化密钥
        TransportableConversionKey transportableConversionKey = (TransportableConversionKey) edgeNode.tcp.receiveObj(8088);
        TransportableFinalCiphertext transportableFinalCiphertext1 = (TransportableFinalCiphertext) edgeNode.tcp.receiveObj(8098);
        System.out.println("----------->9.边缘节点判断是否满足属性ok");
        ConversionKey conversionKey = ConversionKey.rebuild(transportableConversionKey, edgeNode.getPublicParams());
        FinalCiphertext finalCiphertext = FinalCiphertext.rebuild(transportableFinalCiphertext1, edgeNode.getPublicParams());
        boolean isSatisfyAccessPolicy = edgeNode.isSatisfyAccessPolicy(finalCiphertext, conversionKey);
        if (!isSatisfyAccessPolicy) {
            System.out.println("属性匹配失败");
            return;
        }
        //10.边缘节点进行部分解密，生成部分解密密文传输到数据使用者
        TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext = edgeNode.partialDec(finalCiphertext, conversionKey);
        edgeNode.tcp.sendObj(8088,"localhost",transportableIntermediateDecCiphertext);
        System.out.println("---------->10.边缘节点进行部分解密，生成部分解密密文传输到数据使用者ok");
    }
}
