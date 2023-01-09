package com.example.projectjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseFactory {

    private DatabaseFactory() {}

    public static Connection getDatabase(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
