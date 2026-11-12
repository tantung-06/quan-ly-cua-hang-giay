package config;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn.isValid(5)) {
                System.out.println("Kết nối database thành công!");
            } else {
                System.out.println("Kết nối database thất bại!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database!");
            e.printStackTrace();
        }
    }
}