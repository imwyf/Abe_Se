package com.duwei.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.Arrays;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.util
 * @Author: wyf
 * @Date: 2023/9/13
 * @Description: 将发送数据打包并连接MySQL数据库的工具类
 */
public class DatabaseUtils {

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// 属性
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static enum _messageTypes
    {
        TransportablePublicParams, // PublicParams
        TransportableUserPrivateKey, // UserPrivateKey
        TransportableConversionKey, // ConversionKey
        TransportableSearchTrapdoor, // SearchTrapdoor
        TransportableIndexCiphertext, // IndexCiphertext
        TransportableFinalCiphertext, // Ciphertext
        TransportableIntermediateDecCiphertext // IntermediateDecCiphertext
    };
    public _messageTypes _messageType;
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    public final String DATABASE_NAME = "datapacket";
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /// 方法
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public DatabaseUtils()
    {
        ConnectToDatabase();
    };

    // 注册和连接数据库
    public void ConnectToDatabase() {
        try
        {
            //注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //获取数据库的连接对象
            connection = DriverManager. getConnection("jdbc:mysql://localhost:3306/"+DATABASE_NAME, "root", "111111"); //  默认：z<Ec%e(dw5fs
            //获取执行sql语句的对象
            statement = connection.createStatement();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void DisconnectToDatabase()
    {
        if (connection != null) {   //避免空指针异常
            //7、释放资源
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {  //避免空指针异常
            //7、释放资源
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (resultSet != null){  //避免空指针异常
            //7、释放资源
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // 序列化和反序列化message
    public byte[] Serialize(Object obj) throws Exception {
        byte[] ret = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        out.close();
        ret = baos.toByteArray();
        baos.close();
        return ret;
    }
    public Object DeSerialize(byte[] bytes) throws Exception {
        Object ret = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bais);
        ret = in.readObject();
        in.close();
        return ret;
    }

    // 根据提供的对象，根据其类型自动生成sender，一条打包好的数据
    // 生成一条Insert的SQL命令，该命令是String格式的,并直接执行
    public void InsertSQL(Object obj, String receiver) {
        try
        {
            String messageType;
            String sender;
            byte[] data;
            messageType = obj.getClass().getSimpleName(); // 读取该对象的类型名称
            _messageType = _messageTypes.valueOf(messageType); // 根据名称获得对应的type
            switch (_messageType) // 根据type倒推sender
            {
                // 公共参数和属性密钥只能是TA发送
                case TransportablePublicParams:
                case TransportableUserPrivateKey:
                    sender = "TA";
                    break;
                // 转化密钥和搜索陷门只能是data使用者发送的
                case TransportableConversionKey:
                case TransportableSearchTrapdoor:
                    sender = "DataConsumer";
                    break;
                // 索引密文只能是data拥有者发送的
                case TransportableIndexCiphertext:
                    sender = "DataOwner";
                    break;
                // 部分解密密文只能是EN回传给data使用者的
                case TransportableIntermediateDecCiphertext:
                    sender = "EdgeNode";
                    break;
                // 普通的密文就需要分三种情况
                case TransportableFinalCiphertext:
                    switch (receiver)
                    {
                        case "CloudServer":
                            sender = "DataOwner";
                            break;
                        case "DataConsumer":
                            sender = "CloudServer";
                            break;
                        case "EdgeNode":
                            sender = "DataConsumer";
                            break;
                    }
                default:
                    sender = null;
                    break;
            }
            data = Serialize(obj);  // 把obj序列化

            //////////////////////////////////////////////////////////////////////
            /// Debug
            //////////////////////////////////////////////////////////////////////
            System.out.println("messageType: " + messageType);
            System.out.println("sender: " + sender);
            System.out.println("receiver: " + receiver);
            System.out.println("data: ");
            System.out.println("序列化后：" + Arrays.toString(data));
            System.out.println("序列化长度：" + Arrays.toString(data).length());
            System.out.println("反序列化后：" + DeSerialize(data));
            System.exit(1);

            // 定义sql语句
            // -----> 插入格式为 ["messageType", "sender", "receiver", "data"]
            String sql;
            sql = "insert into data(type,sender,receiver,data) value(" + messageType + ',' + sender + ',' + receiver + ',' + data + "); "; // 增
            // update Student set age = 20,score = 100 where id = '10002' // 改
            // delete from Student where id = '10001' // 删
//            while (rs.next()){  //循环一次，游标移动一行 // 查
//                System.out.println("id：" + rs.getString(1)); //  获取第一列的数据
//                System.out.println("name：" + rs.getString("name"));  //获取字段为name的数据
//                System.out.println("age：" + rs.getInt(3)); //  获取第三列的数据
//                System.out.println("score：" + rs.getInt(4)); //  获取第四列的数据
//                System.out.println("-------------------");
//            }

            //执行sql并接收返回结果
            int count = statement.executeUpdate(sql);
            //5、执行sql并接收返回结果
//            rs = stat.executeQuery(sql);

            //处理结果
            System.out.println(count);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
