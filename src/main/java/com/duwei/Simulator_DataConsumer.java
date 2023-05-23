package com.duwei;

import com.duwei.entity.DataConsumer;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

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
    public static final String DataConsumer_ATTRIBUTES_PATH = "src/main/resources/DataConsumer_attributes.txt";
    public static String TA_ADDRESS = "localhost";
    public static final String CloudServer_ADDRESS = "localhost";
    public static final String EdgeNode_ADDRESS = "localhost";

    public static int TA_LISTEN_PORT = 8080;
    public static final int CloudServer_LISTEN_PORT_TO_DataConsumer = 8090;
    public static final int EdgeNode_LISTEN_PORT = 8070;
    private final Socket TA_socket;
    private final Socket CloudServer_socket;
    private final Socket EdgeNode_socket;

    private final DataConsumer dataConsumer;
    private Set<String> userAttributeSet;

    private TransportableConversionKey transportableConversionKey;
    private TransportableFinalCiphertext transportableFinalCiphertext1;

    public Simulator_DataConsumer() throws IOException {
        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT);
        CloudServer_socket = new Socket(CloudServer_ADDRESS, CloudServer_LISTEN_PORT_TO_DataConsumer);
        EdgeNode_socket = new Socket(EdgeNode_ADDRESS, EdgeNode_LISTEN_PORT);

        dataConsumer = new DataConsumer();
        Set<String> userAttributes = new HashSet<>();
        BufferedReader in = new BufferedReader(new FileReader(DataConsumer_ATTRIBUTES_PATH));
        String attr = in.readLine();
        for (String s: attr.split(" ")) {
            userAttributes.add(s);
        }
        in.close();
        userAttributeSet = userAttributes;
    }

    public static void main(String[] args) throws IOException {
        // 设置要连接的TA的地址
        Scanner scanner = new Scanner( System.in );
        System.out.print("请输入需要连接的TA的端口:");
        TA_LISTEN_PORT = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的TA的地址:");
        TA_ADDRESS = scanner.nextLine();//数据类型为String

        Simulator_DataConsumer dataConsumer = new Simulator_DataConsumer();
        dataConsumer.TA_handler();
        dataConsumer.CloudServer_handler(); // 没搜索到就直接终止程序

        dataConsumer.EdgeNode_handler(); // 属性匹配失败也终止
    }

    private void EdgeNode_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(EdgeNode_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(EdgeNode_socket.getInputStream())
        ) {
            // 将转化密钥传送到边缘节点, 同时将服务器发回来的密文也传输到边缘节点
            objectOutputStream.writeObject(transportableConversionKey);
            objectOutputStream.writeObject(transportableFinalCiphertext1);

            //11.数据使用者进行本地解密
            TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext = (TransportableIntermediateDecCiphertext) objectInputStream.readObject();
            if (transportableIntermediateDecCiphertext == null){
                System.out.println("属性匹配失败");
                System.exit(0); // 程序退出
            }
            String decrypt = dataConsumer.decrypt(transportableIntermediateDecCiphertext);
            System.out.println("解密消息：" + decrypt);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void CloudServer_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(CloudServer_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(CloudServer_socket.getInputStream())
        ) {
            //6.数据使用者查询关键字对应的密文
            Set<String> keyWordSearch = new HashSet<>();
            keyWordSearch.add("fed");
            keyWordSearch.add("ieee");
            //生成可传输的搜索陷门传输到云服务器
            TransportableSearchTrapdoor transportableSearchTrapdoor = dataConsumer.generateTransportableSearchTrapdoor(keyWordSearch);
            objectOutputStream.writeObject(transportableSearchTrapdoor);
            objectOutputStream.flush();

            //8.搜索到之后，数据使用者生成可传输的转化密钥
            transportableConversionKey = dataConsumer.generateTransportableConversionKey();
            transportableFinalCiphertext1 = (TransportableFinalCiphertext) objectInputStream.readObject();
            System.out.println("接收到服务器发回来的密文：" + transportableFinalCiphertext1);
            if(transportableFinalCiphertext1 == null){ // 如果没搜索到
                System.out.println("没有匹配的关键字");
                System.exit(0); // 程序终止
            }
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
            System.out.println("接收到公共参数：" + transportablePublicParams);
            dataConsumer.buildPublicParams(transportablePublicParams);

            //2.发送2提交属性获取密钥
            objectOutputStream.writeInt(2);
            objectOutputStream.writeObject(userAttributeSet);
            objectOutputStream.flush();
            TransportableUserPrivateKey transportableUserPrivateKey = (TransportableUserPrivateKey) objectInputStream.readObject();
            System.out.println("接收到属性密钥：" + transportableUserPrivateKey);
            dataConsumer.buildUserPrivateKey(transportableUserPrivateKey);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
