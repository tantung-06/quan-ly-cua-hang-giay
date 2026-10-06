package config;

import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null) {
                System.out.println("Kết nối database thành công!");
            } else {
                System.out.println("Kết nối database thất bại!");
            }

            DatabaseConnection.closeConnection();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database!");
            e.printStackTrace();
        }
    }
}