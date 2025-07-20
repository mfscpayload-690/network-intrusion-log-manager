package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("dao/db.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                throw new IOException("Unable to find dao/db.properties");
            }

            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url, user, password);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Failed to create database connection", e);
        }
    }
}
