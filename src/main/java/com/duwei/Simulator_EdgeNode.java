package com.duwei;

import com.duwei.entity.EdgeNode;
import com.duwei.key.ConversionKey;
import com.duwei.key.transportable.TransportableConversionKey;
import com.duwei.key.transportable.TransportableUserPrivateKey;
import com.duwei.param.TransportablePublicParams;
import com.duwei.text.FinalCiphertext;
import com.duwei.text.transportable.TransportableFinalCiphertext;
import com.duwei.text.transportable.TransportableIntermediateDecCiphertext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 在一台机器上模拟5个实体通信来实现主流程，本进程模拟EdgeNode
 * @author: wyf
 * @time: 2023/5/17 10:53
 */
public class Simulator_EdgeNode {
    public static int TA_LISTEN_PORT;
    public static String TA_ADDRESS;
    public static int EdgeNode_LISTEN_PORT;
    private final ServerSocket serverSocket;
    private final Socket TA_socket;
    private final EdgeNode edgeNode;

    public Simulator_EdgeNode(int listenPort) throws IOException {
        TA_socket = new Socket(TA_ADDRESS, TA_LISTEN_PORT);
        serverSocket = new ServerSocket(listenPort); // 监听来自数据拥有者的连接
        edgeNode = new EdgeNode();

        System.out.println("边缘节点初始化完成");
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

        System.out.print("请输入边缘节点监听的端口:");
        EdgeNode_LISTEN_PORT = scanner.nextInt();//数据类型为int
        scanner.nextLine();


        Simulator_EdgeNode edgeNode = new Simulator_EdgeNode(EdgeNode_LISTEN_PORT);
        ExecutorService executorService = Executors.newCachedThreadPool();
        edgeNode.TA_handler();
        while (true) {
            // TA监听端口，等待注册
            Socket socket = edgeNode.serverSocket.accept();
            System.out.println("接收到一个连接,连接地址: " + socket.getRemoteSocketAddress());
            executorService.execute(() -> {
                try {
                    edgeNode.handler(socket);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("客户端" + socket.getRemoteSocketAddress() + "关闭连接...");
                }
            });
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
            System.out.println("接收到TA传来的公共参数：" + transportablePublicParams);
            edgeNode.buildPublicParams(transportablePublicParams);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handler(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        //9.边缘节点判断是否满足属性
        while (true) {
            TransportableConversionKey transportableConversionKey = (TransportableConversionKey) objectInputStream.readObject();
            TransportableFinalCiphertext transportableFinalCiphertext1 = (TransportableFinalCiphertext) objectInputStream.readObject();
            System.out.println("接收到转化密钥：" + transportableConversionKey);
            System.out.println("接收到密文：" + transportableFinalCiphertext1);

        //首先重构密文和转化密钥
            ConversionKey conversionKey = ConversionKey.rebuild(transportableConversionKey, edgeNode.getPublicParams());
            FinalCiphertext finalCiphertext = FinalCiphertext.rebuild(transportableFinalCiphertext1, edgeNode.getPublicParams());
            TransportableIntermediateDecCiphertext transportableIntermediateDecCiphertext;
            boolean isSatisfyAccessPolicy = edgeNode.isSatisfyAccessPolicy(finalCiphertext, conversionKey);
            if (!isSatisfyAccessPolicy) {
                System.out.println("属性匹配失败,部分解密密文被设为null");
                transportableIntermediateDecCiphertext = null;
            }
            else { //10.边缘节点进行部分解密，生成部分解密密文传输到数据使用者
                transportableIntermediateDecCiphertext = edgeNode.partialDec(finalCiphertext, conversionKey);
                System.out.println("属性匹配成功,边缘节点生成部分解密密文并传输到数据使用者: " + transportableIntermediateDecCiphertext);
            }

            objectOutputStream.writeObject(transportableIntermediateDecCiphertext);
            objectOutputStream.flush();
        }
    }
}

