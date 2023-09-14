package com.duwei;

import java.sql.*;

public class MySQLTest {
    public static void main(String[] args) throws Exception {   //下面方法有不同的异常，我直接抛出一个大的异常

        Connection con = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            //1、注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            //2、获取数据库的连接对象
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", "root", "111111"); //  默认：z<Ec%e(dw5fs

            //3、定义sql语句
            String sql = "insert into Student value('10004','李白',21,59)"; // 增
            // update Student set age = 20,score = 100 where id = '10002' // 改
            // delete from Student where id = '10001' // 删
//            while (rs.next()){  //循环一次，游标移动一行 // 查
//                System.out.println("id：" + rs.getString(1)); //  获取第一列的数据
//                System.out.println("name：" + rs.getString("name"));  //获取字段为name的数据
//                System.out.println("age：" + rs.getInt(3)); //  获取第三列的数据
//                System.out.println("score：" + rs.getInt(4)); //  获取第四列的数据
//                System.out.println("-------------------");
//            }

            //4、获取执行sql语句的对象
            stat = con.createStatement();

            //5、执行sql并接收返回结果
            int count = stat.executeUpdate(sql);

            //6、处理结果
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (con != null) {   //避免空指针异常
                //7、释放资源
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stat != null) {  //避免空指针异常
                //7、释放资源
                try {
                    stat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null){  //避免空指针异常
                //7、释放资源
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
