//package com.duwei.text;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//public class DataPacketEntry {
//
//    ////////////////////////////////////////////////////////////////////////////////////////////
//    /// 属性
//    ///////////////////////////////////////////////////////////////////////////////////////////
//
//    private String _sender;
//    private String _receiver;
//    private _messageTypes _messageType;
//    private byte[] _message; // 传输的数据
//
//
//    ///////////////////////////////////////////////////////////////////////////////////////////////
//    /// 方法
//    ///////////////////////////////////////////////////////////////////////////////////////////////
//
//    // 设置数据内容
//    public void Set(String messageType, String sender, String receiver,  byte[] message)
//    {
//        _sender = sender;
//        _receiver = receiver;
//        _messageType = _messageTypes.valueOf(messageType); // String转对应的enum值
//        _message = message;
//    }
//}
