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
        EdgeNode edgeNode = new EdgeNode();

        // 向TA注册
        edgeNode.tcp.sendObj(7070,"localhost",1);
        // 接收公共参数
        TransportablePublicParams transportablePublicParams= (TransportablePublicParams) edgeNode.tcp.receiveObj(8080);

        //9.边缘节点判断是否满足属性
        edgeNode.buildPublicParams(transportablePublicParams);
        while(true) {
            //首先重构密文和转化密钥
            TransportableConversionKey transportableConversionKey = (TransportableConversionKey) edgeNode.tcp.receiveObj(8088);
            TransportableFinalCiphertext transportableFinalCiphertext1 = (TransportableFinalCiphertext) edgeNode.tcp.receiveObj(8098);
            ConversionKey conversionKey = ConversionKey.rebuild(transportableConversionKey, edgeNode.getPublicParams());
            FinalCiphertext finalCiphertext = FinalCiphertext.rebuild(transportableFinalCiphertext1, edgeNode.getPublicParams());
            boolean isSatisfyAccessPolicy = edgeNode.isSatisfyAccessPolicy(finalCiphertext, conversionKey);
            if (!isSatisfyAccessPolicy) {
                System.out.println("属性匹配失败");
                return;
            }
            System.out.println("----------->9.边缘节点判断是否满足属性ok");
            //10.边缘节点进行部分解密，生成部分解密密文传输到数据使用者
            TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext = edgeNode.partialDec(finalCiphertext, conversionKey);
            edgeNode.tcp.sendObj(9090,"localhost",transportableIntermediateDecCiphertext);
            System.out.println("---------->10.边缘节点进行部分解密，生成部分解密密文传输到数据使用者ok");
        }
    }
}
