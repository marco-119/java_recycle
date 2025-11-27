package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

// 데이터베이스 연결 정보를 관리하고 Connection 객체를 제공하는 유틸리티 클래스입니다.
public class RecycleDB {

    private static final String DB_URL = "jdbc:mysql://223.130.155.245:3306/recycle?serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DB_USER = "remote_user";
    private static final String DB_PASSWORD = "fjf0301!";

    /**
     * MySQL 데이터베이스와 연결된 Connection 객체를 반환합니다.
     * @return DB Connection 객체
     * @throws SQLException 연결 중 발생하는 오류 (드라이버 로드 실패, 접속 실패 등)
     */
    public static Connection connect() throws SQLException {
        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // 드라이버를 찾지 못한 경우 (JAR 파일 누락 등)
            JOptionPane.showMessageDialog(null, "MySQL 드라이버를 찾을 수 없습니다. (JAR 파일 확인 필요)", "DB 연결 오류", JOptionPane.ERROR_MESSAGE);
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}