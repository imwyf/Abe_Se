package com.duwei.util;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class TCPClient {
    private int _PORT = 8080;// 默认连接到端口8080
    private String _IP = "localhost"; // 默认连接本机

    public TCPClient() {

    }

    public void setPortAndIp(int port, String ip) {
        _PORT = port;
        _IP = ip;
    }

    /**
     * @description: 一对一发送数据(Object)
     * @author: wuyufeng
     * @date: 2023/5/17 11:41
     * @param: [java.lang.Object] [o]
     * @return: void
     **/
    public void SendObj(Object o) {
        try {
            // 创建客户端Socket，指定服务器地址和端口
            Socket socket = new Socket(_IP, _PORT);
            System.out.println("-----------发送者启动，等待发送数据");
            // 获取输出流，向服务器发送信息
            OutputStream os = socket.getOutputStream();//字节输出流
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o); // 输出对象数据
            System.out.println("-----------发送者发送数据：" + o);
            oos.close();
            socket.close(); // 关闭资源
        } catch (Exception e) {
            System.out.println("----------发送者启动失败");
            e.printStackTrace();
        }
    }
}



