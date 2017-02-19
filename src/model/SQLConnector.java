package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {
    public static final String CONNECTION_URL = "jdbc:mysql://10.100.0.238:3306/pm?useUnicode=true&characterEncoding=UTF-8";
//    public static final String CONNECTION_URL = "jdbc:mysql://10.100.0.238:3306/pm?useUnicode=true&characterEncoding=UTF-8";
    public static final String USER = "remoteroot";
    public static final String PASSWORD = "123!@#qweQWE";
    public static Connection conn;

    private static SQLConnector ourInstance = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("mysql jdbc driver not found");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLConnector getInstance() {
        if (ourInstance == null) {
            ourInstance = new SQLConnector();
            return ourInstance;
        } else {
            return ourInstance;
        }
    }

    public static void main(String[] args) {
        getInstance();
    }
}