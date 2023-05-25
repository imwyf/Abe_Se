package com.duwei;

import com.duwei.entity.CloudServer;
import com.duwei.entity.TA;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.SearchTrapdoor;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIndexCiphertext;
import com.duwei.text.transportable.TransportableSearchTrapdoor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟CloudServer
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_CloudServer extends Thread{
    public static int CloudServer_LISTEN_PORT_TO_DataConsumer;
    public static int CloudServer_LISTEN_PORT_TO_DataOwner;
    public static String TA_ADDRESS;
    public static int TA_LISTEN_PORT;
    private final ServerSocket serverSocket;
    private final static CloudServer cloudServer = new CloudServer();
    private final Socket TA_socket;

    public Simulator_CloudServer(int listenPort) throws IOException {
        serverSocket = new ServerSocket(listenPort); // 监听来自数据拥有者的连接
        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT); // 与TA连接
    }

    public static <listSerializable> void main(String[] args) throws IOException {
        // 设置要连接的TA的地址
        Scanner scanner = new Scanner( System.in );
        System.out.print("请输入需要连接的TA的端口:");
        TA_LISTEN_PORT = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入需要连接的TA的地址:");
        TA_ADDRESS = scanner.nextLine();//数据类型为String
        System.out.print("请输入云服务器对数据拥有者监听的端口:");
        CloudServer_LISTEN_PORT_TO_DataOwner = scanner.nextInt();//数据类型为int
        scanner.nextLine();
        System.out.print("请输入云服务器对数据使用者监听的端口:");
        CloudServer_LISTEN_PORT_TO_DataConsumer = scanner.nextInt();//数据类型为int
        scanner.nextLine();

        // 向TA注册
        Simulator_CloudServer cloudServer1 = new Simulator_CloudServer(CloudServer_LISTEN_PORT_TO_DataOwner);
        Simulator_CloudServer cloudServer2 = new Simulator_CloudServer(CloudServer_LISTEN_PORT_TO_DataConsumer);
        System.out.println("云存储服务器初始化完成");
        System.out.println();

        cloudServer1.TA_handler();
        cloudServer2.TA_handler();
        ExecutorService executorService = Executors.newCachedThreadPool();
            //CloudServer监听端口，等待用户的连接
            executorService.execute(() -> {
                try {
                    while (true) {
                    Socket DataOwner_socket = cloudServer1.serverSocket.accept();
                    System.out.println("接收到一个连接,连接地址: " + DataOwner_socket.getRemoteSocketAddress());
                    cloudServer1.DataOwner_handler(DataOwner_socket);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            executorService.execute(() -> {
                try {
                    while (true) {
                        Socket DataConsumer_socket = cloudServer2.serverSocket.accept();
                        System.out.println("接收到一个连接,连接地址: " + DataConsumer_socket.getRemoteSocketAddress());
                        cloudServer2.DataConsumer_handler(DataConsumer_socket);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

    //1.发送1获取公共参数
    private void TA_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(TA_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(TA_socket.getInputStream())
        ) {

            objectOutputStream.writeInt(1);
            objectOutputStream.flush();
            TransportablePublicParams transportablePublicParams = (TransportablePublicParams) objectInputStream.readObject();
            System.out.println("接收到TA传来的公共参数：" + transportablePublicParams);
            cloudServer.buildPublicParams(transportablePublicParams);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void DataConsumer_handler(Socket dataOwnerSocket) throws IOException, ClassNotFoundException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataOwnerSocket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(dataOwnerSocket.getInputStream())
        ){
            TransportableSearchTrapdoor transportableSearchTrapdoor = (TransportableSearchTrapdoor) objectInputStream.readObject();
            System.out.println("接收到搜索陷门：" + transportableSearchTrapdoor);
            SearchTrapdoor searchTrapdoor = SearchTrapdoor.rebuild(transportableSearchTrapdoor, cloudServer.getPublicParams());
            //7.云服务器遍历自己所存储的索引密文，看是否能够搜索到
            TransportableFinalCiphertext transportableFinalCiphertext1 = cloudServer.checkSearchTrapdoor(searchTrapdoor);
            if (transportableFinalCiphertext1 == null) {
                System.out.println("没有匹配的关键字,对应密文被设为null");
            }
            //搜索成功后云服务器将对应的密文拿出来传输给用户
            objectOutputStream.writeObject(transportableFinalCiphertext1);
            objectOutputStream.flush();
            System.out.println("匹配成功,云服务器将对应的密文传输给用户: " + transportableFinalCiphertext1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void DataOwner_handler(Socket DataConsumersocket) throws IOException, ClassNotFoundException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(DataConsumersocket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(DataConsumersocket.getInputStream())
        ){
                TransportableIndexCiphertext transportableIndexCiphertext = (TransportableIndexCiphertext) objectInputStream.readObject();
                TransportableFinalCiphertext transportableFinalCiphertext = (TransportableFinalCiphertext) objectInputStream.readObject();
                System.out.println("接收到索引密文：" + transportableIndexCiphertext);
                System.out.println("接收到密文：" + transportableFinalCiphertext);

                //5.存储数据拥有者发来的索引和密文
                cloudServer.store(transportableIndexCiphertext, transportableFinalCiphertext);
                System.out.println("已存储数据拥有者发来的索引和密文");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}