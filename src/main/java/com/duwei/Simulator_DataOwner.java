package com.duwei;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.access.lsss.Policies;
import com.duwei.entity.DataOwner;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;

import java.util.*;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟DataOwner
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_DataOwner {
    public static void main(String[] args) {
        System.out.println("-----------> 我是DataOwner");

        //4. 数据拥有者本地构建访问策略，加密数据
        DataOwner dataOwner = new DataOwner();
        // 向TA注册
        dataOwner.getTcp().sendObj(7070,"localhost",1);
        // 接收公共参数
        TransportablePublicParams transportablePublicParams = (TransportablePublicParams) dataOwner.getTcp().receiveObj(8080);
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
        //将可传输的密文索引和可传输的密文发送到云存储服务器
        dataOwner.getTcp().sendObj(8085,"localhost",transportableIndexCiphertext);
        dataOwner.getTcp().sendObj(8095,"localhost",transportableFinalCiphertext);
        System.out.println("------>4. 将可传输的密文索引和可传输的密文发送到云存储服务器ok");

    }
}
