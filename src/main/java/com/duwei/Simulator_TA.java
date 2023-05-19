package com.duwei;

import com.duwei.entity.TA;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟TA
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_TA {
    public static final int TA_LISTEN_PORT = 8080;
    private final ServerSocket serverSocket;
    private final TA ta;

    public Simulator_TA(int listenPort) throws IOException {
        serverSocket = new ServerSocket(listenPort);
        ta = new TA();
        Set<String> attributes = new HashSet<>();
        attributes.add("A");
        attributes.add("B");
        attributes.add("C");
        attributes.add("D");
        attributes.add("E");
        attributes.add("F");
        attributes.add("G");
        attributes.add("H");
        ta.setUp(attributes);
        System.out.println("------->1.TA进行初始化ok");
    }

    public static void main(String[] args) throws IOException {
        //1.Ta根据全局属性集合，设置公共参数
        Simulator_TA ta = new Simulator_TA(TA_LISTEN_PORT); //8080
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            // TA监听端口，等待注册
            Socket socket = ta.serverSocket.accept();
            System.out.println("接收到一个连接,连接地址: " + socket.getRemoteSocketAddress());
            executorService.execute(() -> {
                try {
                    ta.handler(socket);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("客户端" + socket.getRemoteSocketAddress() + "关闭连接...");
                }
            });
        }
    }

    private void handler(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        while (true) {
            int flag = objectInputStream.readInt();
            if (flag == 1) {
                //获取公共参数
                System.out.println("接收到" + socket.getRemoteSocketAddress() + "获取公共参数请求");
                TransportablePublicParams transportablePublicParams = ta.getTransportablePublicParams();
                objectOutputStream.writeObject(transportablePublicParams);
            } else if (flag == 2) {
                //数据访问者提交提交属性
                System.out.println("接收到" + socket.getRemoteSocketAddress() + "获取生成密钥请求请求");
                Set attributes = (Set) objectInputStream.readObject();
                TransportableUserPrivateKey transportableUserPrivateKey = ta.keyGenTransportable(attributes);
                objectOutputStream.writeObject(transportableUserPrivateKey);
            }
            objectOutputStream.flush();
        }
    }
}
