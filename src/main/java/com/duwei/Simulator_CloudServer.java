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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟CloudServer
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_CloudServer extends Thread{
    public static final int CloudServer_LISTEN_PORT_TO_DataConsumer = 8090;
    public static final int CloudServer_LISTEN_PORT_TO_DataOwner = 8060;
    public static final String TA_ADDRESS = "localhost";
    public static final int TA_LISTEN_PORT = 8080;
    private final ServerSocket serverSocket;
    private final static CloudServer cloudServer = new CloudServer();
    private final Socket TA_socket;

    public Simulator_CloudServer(int listenPort) throws IOException {
        serverSocket = new ServerSocket(listenPort); // 监听来自数据拥有者的连接
        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT); // 与TA连接
    }

    public static <listSerializable> void main(String[] args) throws IOException {
        // 向TA注册
        Simulator_CloudServer cloudServer1 = new Simulator_CloudServer(CloudServer_LISTEN_PORT_TO_DataOwner);
        Simulator_CloudServer cloudServer2 = new Simulator_CloudServer(CloudServer_LISTEN_PORT_TO_DataConsumer);
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
                    System.out.println("没有匹配的关键字");
                    return;
                }
                //搜索成功后云服务器将对应的密文拿出来传输给用户
                objectOutputStream.writeObject(transportableFinalCiphertext1);
                objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //1.发送1获取公共参数
    private void TA_handler() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(TA_socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(TA_socket.getInputStream())
        ) {

            objectOutputStream.writeInt(1);
            objectOutputStream.flush();
            TransportablePublicParams transportablePublicParams = (TransportablePublicParams) objectInputStream.readObject();
            System.out.println("接收到公共参数：" + transportablePublicParams);
            cloudServer.buildPublicParams(transportablePublicParams);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void DataOwner_handler(Socket DataConsumersocket) throws IOException, ClassNotFoundException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(DataConsumersocket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(DataConsumersocket.getInputStream())
        ){
                TransportableIndexCiphertext transportableIndexCiphertext = (TransportableIndexCiphertext) objectInputStream.readObject();
                TransportableFinalCiphertext transportableFinalCiphertext = (TransportableFinalCiphertext) objectInputStream.readObject();
                System.out.println("接收到密文索引：" + transportableIndexCiphertext);
                System.out.println("接收到密文：" + transportableFinalCiphertext);

                //5.存储数据拥有者发来的索引和密文
                cloudServer.store(transportableIndexCiphertext, transportableFinalCiphertext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}