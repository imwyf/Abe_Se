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
import com.duwei.util.DatabaseUtils;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟DataOwner
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_DataOwner {
    public static final String DataOwner_ACCESSEXPRESSIONE_PATH = "conf/DataOwner_accessExpression.txt";
    public static final String DataOwner_MESSAGE_AND_KEYWORDS_PATH = "conf/DataOwner_message_and_keywords.txt";
    private final Socket TA_socket;
    private final Socket CloudServer_socket;
    public static String TA_ADDRESS;
    public static int TA_LISTEN_PORT;
    public static String CloudServer_ADDRESS;
    public static int CloudServer_LISTEN_PORT_TO_DataOwner;
    private final DataOwner dataOwner;
    private static String accessExpression;
    private static String message;
    private static Set<String> keywordsSet;
    private static DatabaseUtils databaseUtils;

    public Simulator_DataOwner() throws IOException {

        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT);
        CloudServer_socket = new Socket(CloudServer_ADDRESS, CloudServer_LISTEN_PORT_TO_DataOwner);
        dataOwner = new DataOwner();
        databaseUtils = new DatabaseUtils();

        //构建策略表达式
        //从文件中读取
        BufferedReader in1 = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(DataOwner_ACCESSEXPRESSIONE_PATH)));
        accessExpression = in1.readLine();
        in1.close();

        //读取原始消息和关键词
        BufferedReader in2 = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(DataOwner_MESSAGE_AND_KEYWORDS_PATH)));
        message = in2.readLine();
        Set<String> keywords = new HashSet<>();
        for (String s: in2.readLine().split(" ")) {
            keywords.add(s);
        }
        in2.close();
        keywordsSet = keywords;

        System.out.println("数据拥有者的原始消息: " + message);
        System.out.println("数据拥有者的消息关键词: " + keywords);
        System.out.println("数据拥有者的策略表达式: " + accessExpression);
        System.out.println("数据拥有者初始化完成");
        System.out.println();
    }

    public static void main(String[] args) throws IOException {
        // 设置要连接的TA的地址
        Scanner scanner = new Scanner( System.in );
        System.out.print("请输入需要连接的TA的端口:");
        TA_LISTEN_PORT = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的TA的地址:");
        TA_ADDRESS = scanner.nextLine();//
        System.out.print("请输入需要连接的云服务器的端口:");
        CloudServer_LISTEN_PORT_TO_DataOwner = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的云服务器的地址:");
        CloudServer_ADDRESS = scanner.nextLine();//数据类型为int

        Simulator_DataOwner dataOwner = new Simulator_DataOwner();
        dataOwner.TA_handler();
        dataOwner.CloudServer_handler();
        databaseUtils.DisconnectToDatabase();
    }

    private void TA_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(TA_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(TA_socket.getInputStream())
        ) {
            //1.发送1获取公共参数
            objectOutputStream.writeInt(1);
            objectOutputStream.flush();
            TransportablePublicParams transportablePublicParams = (TransportablePublicParams) objectInputStream.readObject();
            // 插入到数据库
            databaseUtils.InsertSQL(transportablePublicParams,"DataOwner");
            System.out.println("接收到TA传来的公共参数：" + transportablePublicParams);
            dataOwner.buildPublicParams(transportablePublicParams);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void CloudServer_handler(){
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(CloudServer_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(CloudServer_socket.getInputStream())
        ) {
            //先进行离线计算
            dataOwner.offlineEnc();
            //接下来加密索引，生成可以传输的索引密文
            TransportableIndexCiphertext transportableIndexCiphertext = dataOwner.keywordEncToTransportableIndexCiphertext(keywordsSet);
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

            System.out.println("生成索引密文并发送给云存储服务器: " + transportableIndexCiphertext);
            System.out.println("生成密文并发送给云存储服务器: " + transportableFinalCiphertext);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
