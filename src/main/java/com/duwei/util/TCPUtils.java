package com.duwei.util;


public class TCPUtils {
    public static TCPClient tcpClient = new TCPClient(); // 发送器静态实例
    public static TCPServer tcpServer = new TCPServer(); // 接收器静态实例

    /**
     * @description: 指定端口和ip，发送消息(obj)
     * @author: wuyufeng
     * @date: 2023/5/17 11:46
     * @param: [int, java.lang.String, java.lang.Object] [port, ip, o]
     * @return: void
     **/
    public void sendObj(int port, String ip,Object o){
        tcpClient.setPortAndIp(port,ip);
        tcpClient.SendObj(o);
    }

    /**
     * @description: 指定端口监听，获取收到的消息（Object）
     * @author: wuyufeng
     * @date: 2023/5/17 11:46
     * @param: [int] [port]
     * @return: java.lang.String
     **/
    public Object receiveObj(int port){
        tcpServer.setPORT(port);
        return tcpServer.ReceiveObj();
    }
}
