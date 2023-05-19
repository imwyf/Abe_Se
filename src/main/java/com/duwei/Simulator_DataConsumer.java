package com.duwei;

import com.duwei.entity.DataConsumer;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟DataConsumer
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_DataConsumer {
    public static final String TA_ADDRESS = "localhost";
    public static final String CloudServer_ADDRESS = "localhost";
    public static final String EdgeNode_ADDRESS = "localhost";

    public static final int TA_LISTEN_PORT = 8080;
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
        userAttributes.add("D");
        userAttributes.add("B");
        userAttributes.add("A");
        userAttributes.add("C");
        userAttributes.add("F");
        userAttributes.add("H");
        userAttributeSet = userAttributes;
    }

    public static void main(String[] args) throws IOException {
        Simulator_DataConsumer dataConsumer = new Simulator_DataConsumer();
        dataConsumer.TA_handler();
        dataConsumer.CloudServer_handler();
        dataConsumer.EdgeNode_handler();
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
