// DatabaseManager.java (MySQL 버전)
package recycle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

	private static final String DB_URL = "jdbc:mysql://223.130.155.245:3306/recycle?serverTimezone=UTC&characterEncoding=UTF-8";
	private static final String DB_USER = "remote_user";
	private static final String DB_PASSWORD = "fjf0301!";

    public static Connection getConnection() throws SQLException {
        try {
            // (수정) MySQL 드라이버 클래스
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC 드라이버를 찾을 수 없습니다. Build Path를 확인하세요.");
            e.printStackTrace();
        }
        // (수정) ID와 PW를 사용해 접속
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() {
        // (수정) MySQL용 테이블 생성 쿼리 (username이 PK)
        String sql = "CREATE TABLE IF NOT EXISTS Users ("
                   + " username VARCHAR(50) PRIMARY KEY NOT NULL UNIQUE," 
                   + " totalPoints INT NOT NULL DEFAULT 0,"
                   + " lastQuizDate DATE" 
                   + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("MySQL 데이터베이스 테이블이 성공적으로 준비되었습니다.");
            
        } catch (SQLException e) {
            System.err.println("데이터베이스 초기화 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}