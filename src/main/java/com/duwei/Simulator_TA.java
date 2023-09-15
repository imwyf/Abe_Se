package com.duwei;

import com.duwei.entity.TA;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.util.DatabaseUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟TA
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_TA {
    public static final String TA_ATTRIBUTES_PATH = "conf/TA_attributes.txt";
    public static int TA_LISTEN_PORT;
    private final ServerSocket serverSocket;
    private final TA ta;
    public Simulator_TA(int listenPort) throws IOException {
        serverSocket = new ServerSocket(listenPort);
        ta = new TA();
        // 读取全局属性集合
        Set<String> attributes = new HashSet<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(TA_ATTRIBUTES_PATH)));
        String attr = in.readLine();

        for (String s: attr.split(" ")) {
            attributes.add(s);
        }
        in.close();
        ta.setUp(attributes);

        System.out.println("全局属性集合为: [" + attr + "]");
        System.out.println("TA初始化完成");
        System.out.println();
    }

    public static void main(String[] args) throws IOException {
        // 与控制台交互
        Scanner scanner = new Scanner( System.in );
        System.out.print("请输入TA监听的端口:");
        TA_LISTEN_PORT = scanner.nextInt();//数据类型为int

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
                    System.out.println("客户端" + socket.getRemoteSocketAddress() + "关闭连接...\n");
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

                System.out.println("已向" + socket.getRemoteSocketAddress() + "发送公共参数: " + transportablePublicParams);
            } else if (flag == 2) {
                //数据访问者提交提交属性
                System.out.println("接收到" + socket.getRemoteSocketAddress() + "获取生成密钥请求请求");
                Set attributes = (Set) objectInputStream.readObject();
                TransportableUserPrivateKey transportableUserPrivateKey = ta.keyGenTransportable(attributes);
                objectOutputStream.writeObject(transportableUserPrivateKey);
                System.out.println("已向" + socket.getRemoteSocketAddress() + "发送属性密钥: " + transportableUserPrivateKey);
            }
            objectOutputStream.flush();
        }
    }
}
