package com.duwei;

import com.duwei.access.lsss.AccessPolicy;
import com.duwei.access.lsss.Policies;
import com.duwei.entity.CloudServer;
import com.duwei.entity.DataOwner;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟DataOwner
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_DataOwner {
    private final Socket TA_socket;
    private final Socket CloudServer_socket;
    public static final String TA_ADDRESS = "localhost";
    public static final int TA_LISTEN_PORT = 8080;
    public static final String CloudServer_ADDRESS = "localhost";
    public static final int CloudServer_LISTEN_PORT_TO_DataOwner = 8060;
    private final DataOwner dataOwner;

    public Simulator_DataOwner() throws IOException {
        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT);
        CloudServer_socket = new Socket(CloudServer_ADDRESS, CloudServer_LISTEN_PORT_TO_DataOwner);
        dataOwner = new DataOwner();
    }

    public static void main(String[] args) throws IOException {
        Simulator_DataOwner dataOwner = new Simulator_DataOwner();
        dataOwner.TA_handler();
        dataOwner.CloudServer_handler();
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
            dataOwner.buildPublicParams(transportablePublicParams);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void CloudServer_handler(){
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(CloudServer_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(CloudServer_socket.getInputStream())
        ) {
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
            objectOutputStream.writeObject(transportableIndexCiphertext);
            objectOutputStream.writeObject(transportableFinalCiphertext);
            objectOutputStream.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
