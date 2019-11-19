package com.tavisca.javatraining.dbaccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static Connection connection;

    public static Connection getConnection(String tableName) throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL + tableName, DB_USER, DB_PASSWORD);
        }
        return connection;
    }
}
