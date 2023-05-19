package com.duwei;

import com.duwei.entity.CloudServer;
import com.duwei.entity.EdgeNode;
import com.duwei.entity.TA;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;

import java.util.HashSet;
import java.util.Set;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟TA
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_TA {
    public static void main(String[] args) {
        System.out.println("-----------> 我是TA");
        // tcp传输
        // 2.TA收到注册请求，TA将公共参数传输到其他实体
        // 其他实体 -> TA : 7070
        // TA -> 其他实体 : 8080
        // 3.TA传用户密钥给dataConsumer
        // 收属性集合：8081
        // TA -> DataConsumer : 8082
        // 4.dataOwner将密文索引和密文发送到CloudServer
        // dataOwner -> CloudServer : 索引8085,密文8095
        // 5.
        // 6.dataConsumer生成搜索陷门传输到CloudServer
        // dataConsumer -> CloudServer : 8086
        // 7.搜索成功后CloudServer将密文传输给dataConsumer
        // CloudServer -> dataConsumer : 8087
        // 8.dataConsumer生成可传输的转化密钥和服务器发回来的密文传送到EdgeNode
        // dataConsumer -> EdgeNode : 转化密钥8088,密文8098
        // 9.
        // 10.EdgeNode进行部分解密，生成部分解密密文传输到dataConsumer
        // EdgeNode -> dataConsumer : 8088


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
        System.out.println("------->1.TA进行初始化ok");

        // TA监听端口，等待注册
        while(true){
            int signCode = (int) ta.getTcp().receiveObj(7070);
            if (signCode == 1) {
                // 2.收到注册请求,传给请求方公共参数
                System.out.println("------>TA:收到注册请求");
                TransportablePublicParams transportablePublicParams = ta.getTransportablePublicParams();
                ta.getTcp().sendObj(8080,"localhost",transportablePublicParams);
                System.out.println("------>TA:传给请求方公共参数");
            }
            else if(signCode == 2) {
                System.out.println("------>TA:收到属性集合请求");
                // 3.收到发送的用户属性集合，TA生成用户密钥将其传输到用户
                Set<String> userAttributes = (Set<String>) ta.getTcp().receiveObj(8081);
                System.out.println("------>TA:收到用户属性集合");
                //调用TA的keyGenTransportable()生成可以传输的用户密钥，然后将其传输到用户
                TransportableUserPrivateKey transportableUserPrivateKey = ta.keyGenTransportable(userAttributes);
                ta.getTcp().sendObj(8082,"localhost",transportableUserPrivateKey);
                System.out.println("------>TA:生成用户密钥将其传输到用户");
            }
            else {
                System.out.println("------>注册码错误");
            }
        }
    }
}
