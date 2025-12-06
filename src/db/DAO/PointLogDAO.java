package db.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PointLogDAO {
    
    private static final String POINT_LOGS_TABLE = "POINT_LOGS";

    public PointLogDAO() {
        
    }

    public void insertPointLog(Connection conn, String userId, String type, String detail, int points) throws SQLException {
        String sql = "INSERT INTO " + POINT_LOGS_TABLE + 
                     " (USER_ID, TIMESTAMP, TYPE, DETAIL, POINT) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(3, type);
            pstmt.setString(4, detail); 
            pstmt.setInt(5, points);
            
            pstmt.executeUpdate();
        }
    }
    
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + POINT_LOGS_TABLE + " ("
                + "`LOG_ID` INT NOT NULL AUTO_INCREMENT COMMENT '로그 고유 ID',"
                + "`USER_ID` VARCHAR(50) NOT NULL COMMENT '사용자 ID',"
                + "`TIMESTAMP` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '기록 시간',"
                + "`TYPE` VARCHAR(10) NOT NULL COMMENT 'EARN 또는 SPEND',"
                + "`DETAIL` VARCHAR(255) COMMENT '상세 내용',"
                + "`POINT` INT NOT NULL COMMENT '포인트 변동량',"
                + "PRIMARY KEY (`LOG_ID`),"
                + "KEY `USER_ID_idx` (`USER_ID`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
    }
}