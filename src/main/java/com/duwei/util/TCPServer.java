package com.duwei.util;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TCPServer {
    private int _PORT = 8080;// 默认连接到端口8080
    public TCPServer() {

    }
    public void setPORT(int port){
        _PORT = port;
    }
   /**
    * @description: 接收者监听端口，启动服务，等待发送者传输数据，接收数据后返回一个string作为数据
    * @author: wuyufeng
    * @date: 2023/5/16 11:35
    * @param: []
    * @return: java.lang.String
    **/

    public Object ReceiveObj() {
        Object o = null;
        try {
            ServerSocket serverSocket = new ServerSocket(_PORT);
            // 开始监听 等待客户端连接
            System.out.println("---------接收者启动，等待发送者连接");
            Socket socket = serverSocket.accept();
            // 获取输入流 并读取客户端信息
            InputStream is = socket.getInputStream();//字节输入流
            ObjectInputStream ois = new ObjectInputStream(is); //将字节流转化为对象流
            o = ois.readObject();
            System.out.println("-----------接收者收到数据：" + o);
            ois.close();
            socket.close(); // 关闭资源
            serverSocket.close();
        }catch (Exception e){
            System.out.println("---------接收者启动失败");
            e.printStackTrace();
        }
        return o;
    }
}
