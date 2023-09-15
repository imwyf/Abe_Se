package com.duwei;

import com.duwei.util.DatabaseUtils;

import java.sql.*;

public class MySQLTest {
    public static void main(String[] args) throws Exception {   //下面方法有不同的异常，我直接抛出一个大的异常
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.ConnectToDatabase();
//        databaseUtils.
    }
}
