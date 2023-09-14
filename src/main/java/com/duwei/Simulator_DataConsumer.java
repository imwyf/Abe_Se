package com.duwei;

import com.duwei.entity.DataConsumer;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;
import com.duwei.util.DatabaseUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟DataConsumer
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_DataConsumer {
    public static final String DataConsumer_ATTRIBUTES_PATH = "conf/DataConsumer_attributes.txt";
    public static final String DataConsumer_KEYWORDSEARCH_PATH = "conf/DataConsumer_keyWordSearch.txt";
    public static String TA_ADDRESS;
    public static String CloudServer_ADDRESS;
    public static String EdgeNode_ADDRESS;

    public static int TA_LISTEN_PORT;
    public static int CloudServer_LISTEN_PORT_TO_DataConsumer;
    public static int EdgeNode_LISTEN_PORT;
    private final Socket TA_socket;
    private final Socket CloudServer_socket;
    private final Socket EdgeNode_socket;

    private final DataConsumer dataConsumer;
    private Set<String> userAttributeSet;
    Set<String> keyWordSearchSet;

    private TransportableConversionKey transportableConversionKey;
    private TransportableFinalCiphertext transportableFinalCiphertext1;
    private static DatabaseUtils databaseUtils;

    public Simulator_DataConsumer() throws IOException {
        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT);
        CloudServer_socket = new Socket(CloudServer_ADDRESS, CloudServer_LISTEN_PORT_TO_DataConsumer);
        EdgeNode_socket = new Socket(EdgeNode_ADDRESS, EdgeNode_LISTEN_PORT);
        databaseUtils = new DatabaseUtils();
        dataConsumer = new DataConsumer();

        Set<String> userAttributes = new HashSet<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(DataConsumer_ATTRIBUTES_PATH)));
        String attr = in.readLine();
        for (String s: attr.split(" ")) {
            userAttributes.add(s);
        }
        in.close();

        Set<String> keyWordSearch = new HashSet<>();
        BufferedReader in1 = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(DataConsumer_KEYWORDSEARCH_PATH)));
        String attr1 = in1.readLine();
        for (String s: attr1.split(" ")) {
            keyWordSearch.add(s);
        }
        in.close();

        keyWordSearchSet = keyWordSearch;
        userAttributeSet = userAttributes;
        System.out.println("数据使用者的属性集合: " + attr);
        System.out.println("数据使用者的搜索关键词: " + attr1);
        System.out.println("数据使用者初始化完成");
        System.out.println();

    }

    public static void main(String[] args) throws IOException {
        // 设置要连接的TA的地址
        Scanner scanner = new Scanner( System.in );
        System.out.print("请输入需要连接的TA的端口:");
        TA_LISTEN_PORT = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的TA的地址:");
        TA_ADDRESS = scanner.nextLine();//数据类型为String
        System.out.print("请输入需要连接的云服务器的端口:");
        CloudServer_LISTEN_PORT_TO_DataConsumer = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的云服务器的地址:");
        CloudServer_ADDRESS = scanner.nextLine();//数据类型为int
        System.out.print("请输入需要连接的边缘节点的端口:");
        EdgeNode_LISTEN_PORT = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的边缘节点的地址:");
        EdgeNode_ADDRESS = scanner.nextLine();//数据类型为String

        Simulator_DataConsumer dataConsumer = new Simulator_DataConsumer();
        dataConsumer.TA_handler();
        dataConsumer.CloudServer_handler(); // 没搜索到就直接终止程序

        dataConsumer.EdgeNode_handler(); // 属性匹配失败也终止
        databaseUtils.DisconnectToDatabase();
    }

    private void EdgeNode_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(EdgeNode_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(EdgeNode_socket.getInputStream())
        ) {
            // 将转化密钥传送到边缘节点, 同时将服务器发回来的密文也传输到边缘节点
            objectOutputStream.writeObject(transportableConversionKey);
            objectOutputStream.writeObject(transportableFinalCiphertext1);
            System.out.println("将转化密钥传送到边缘节点: " + transportableConversionKey);
            System.out.println("同时将服务器发回来的密文也传输到边缘节点: " + transportableFinalCiphertext1);

            //11.数据使用者进行本地解密
            TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext = (TransportableIntermediateDecCiphertext) objectInputStream.readObject();
            System.out.println("收到部分解密结果: " + transportableIntermediateDecCiphertext);
            if (transportableIntermediateDecCiphertext == null){
                System.out.println("属性匹配失败");
                System.exit(0); // 程序退出
            }

            String decrypt = dataConsumer.decrypt(transportableIntermediateDecCiphertext);
            System.out.println("解密消息: " + decrypt);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void CloudServer_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(CloudServer_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(CloudServer_socket.getInputStream())
        ) {
            //6.数据使用者查询关键字对应的密文
            //生成可传输的搜索陷门传输到云服务器
            TransportableSearchTrapdoor transportableSearchTrapdoor = dataConsumer.generateTransportableSearchTrapdoor(keyWordSearchSet);
            objectOutputStream.writeObject(transportableSearchTrapdoor);
            objectOutputStream.flush();

            System.out.println("生成搜索陷门传输到云服务器：" + transportableSearchTrapdoor);
            //8.搜索到之后，数据使用者生成可传输的转化密钥
            transportableFinalCiphertext1 = (TransportableFinalCiphertext) objectInputStream.readObject();
            System.out.println("接收到服务器发回来的密文：" + transportableFinalCiphertext1);

            if(transportableFinalCiphertext1 == null){ // 如果没搜索到
                System.out.println("没有匹配的关键字");
                System.exit(0); // 程序终止
            }
            transportableConversionKey = dataConsumer.generateTransportableConversionKey();
            System.out.println("生成转化密钥：" + transportableConversionKey);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void TA_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(TA_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(TA_socket.getInputStream())
        ) {
            //1.发送1获取公共参数
            objectOutputStream.writeInt(1);
            objectOutputStream.flush();
            TransportablePublicParams transportablePublicParams = (TransportablePublicParams) objectInputStream.readObject();
            System.out.println("接收到TA传来的公共参数：" + transportablePublicParams);
            dataConsumer.buildPublicParams(transportablePublicParams);

            //2.发送2提交属性获取密钥
            objectOutputStream.writeInt(2);
            objectOutputStream.writeObject(userAttributeSet);
            objectOutputStream.flush();
            TransportableUserPrivateKey transportableUserPrivateKey = (TransportableUserPrivateKey) objectInputStream.readObject();
            System.out.println("接收到TA传来的属性密钥：" + transportableUserPrivateKey);
            dataConsumer.buildUserPrivateKey(transportableUserPrivateKey);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
