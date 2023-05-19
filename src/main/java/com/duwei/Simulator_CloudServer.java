package com.duwei;

import com.duwei.entity.CloudServer;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.SearchTrapdoor;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

import java.util.*;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟CloudServer
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_CloudServer extends Thread {
    //5.初始化云存储服务器，存储数据拥有者发来的索引和密文
    static CloudServer cloudServer = new CloudServer(); // 全局变量
    static TransportablePublicParams transportablePublicParams;

    public static <listSerializable> void main(String[] args) {
        System.out.println("-----------> 我是CloudServer");
        // 向TA注册
        cloudServer.tcp.sendObj(7070,"localhost",1);
        // 接收公共参数
        transportablePublicParams = (TransportablePublicParams) cloudServer.tcp.receiveObj(8080);

        // 启动来接收数据拥有者发来的密文、索引的线程
        Simulator_CloudServer simulatorCloudServer = new Simulator_CloudServer();
        simulatorCloudServer.start();

        // 启动数据拥有者查询密文的线程
        while (true){
            //7.云服务器首先重构搜索陷门
            TransportableSearchTrapdoor transportableSearchTrapdoor = (TransportableSearchTrapdoor) cloudServer.tcp.receiveObj(8086);
            SearchTrapdoor searchTrapdoor = SearchTrapdoor.rebuild(transportableSearchTrapdoor, cloudServer.getPublicParams());
            //云服务器遍历自己所存储的索引密文，看是否能够搜索到
            TransportableFinalCiphertext transportableFinalCiphertext1 = cloudServer.checkSearchTrapdoor(searchTrapdoor);
            if (transportableFinalCiphertext1 == null) {
                System.out.println("没有匹配的关键字");
                return;
            }
            //搜索成功后云服务器将对应的密文拿出来传输给用户
            cloudServer.tcp.sendObj(8087,"localhost",transportableFinalCiphertext1);
            System.out.println("--------------->7.搜索成功后云服务器将对应的密文拿出来传输给用户ok");
        }
    }

    /**
     * @description: 用来接收数据拥有者发来的密文、索引的线程
     * @author: wuyufeng
     * @date: 2023/5/19 12:11
     * @param: [] []
     * @return: void
     **/
    @Override
    public void run() {
        while (true){
            // 接收数据拥有者发来的密文、索引
            TransportableIndexCiphertext transportableIndexCiphertext = (TransportableIndexCiphertext) cloudServer.tcp.receiveObj(8085);
            TransportableFinalCiphertext transportableFinalCiphertext = (TransportableFinalCiphertext) cloudServer.tcp.receiveObj(8095);
            //首先需要将可传输的索引和密文变为正常的索引和密文
            cloudServer.buildPublicParams(transportablePublicParams);
            cloudServer.store(transportableIndexCiphertext, transportableFinalCiphertext);
            System.out.println("--------->5.云服务器存储数据拥有者发来的索引和密文ok");
        }
    }
}
