package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties prop = new Properties();
            try (InputStream input = Files.newInputStream(Paths.get("javagp2/bin/dao/db.properties"))) {
                prop.load(input);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SQLException("Unable to load database properties", e);
            }

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("MySQL JDBC Driver not found", e);
            }

            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
}
