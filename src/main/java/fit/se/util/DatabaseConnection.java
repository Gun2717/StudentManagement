package fit.se.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new IOException("db.properties not found in classpath");
            props.load(in);
        }
        this.url = props.getProperty("db.url");
        this.username = props.getProperty("db.username");
        this.password = props.getProperty("db.password");
    }

    public static synchronized DatabaseConnection getInstance() throws IOException {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() throws SQLException, IOException {
        return DriverManager.getConnection(url, username, password);
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(2);
        } catch (Exception e) {
            System.err.println("DB test failed: " + e.getMessage());
            return false;
        }
    }
}
