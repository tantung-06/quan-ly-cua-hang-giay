package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quanlycuahanggiay";
    private static final String USER = "root";
    private static final String PASSWORD = "2310";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @FunctionalInterface
    public interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}