package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * recycle_logs 및 user_points 테이블에 접근하는 DAO 클래스입니다.
 */
public class RecycleLogDAO {

    private static final String LOGS_TABLE = "recycle_logs";
    private static final String POINTS_TABLE = "user_points";

    // DB 초기화 및 테이블 생성 (user_id 포함)
    public static void initializeDatabase() throws SQLException {
        
        // 사용자 기록 테이블: user_id 추가
        String createLogs =
                "CREATE TABLE IF NOT EXISTS " + LOGS_TABLE + " (" +
                        "log_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id VARCHAR(50) NOT NULL," + 
                        "item_name VARCHAR(50) NOT NULL," +
                        "point INT NOT NULL," +
                        "timestamp DATETIME NOT NULL" +
                        ");";

        // 사용자 총 포인트 관리 테이블
        String createPoints =
                "CREATE TABLE IF NOT EXISTS " + POINTS_TABLE + " (" +
                        "user_id VARCHAR(50) PRIMARY KEY," +
                        "total_points INT DEFAULT 0," +
                        "last_update DATETIME" +
                        ");";

        try (Connection conn = recycleDB.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createLogs);
            stmt.execute(createPoints);
        }
    }

    /**
     * 특정 사용자의 기존 기록 품목 Set을 조회합니다.
     */
    public Set<String> loadSavedItemsSet(String userId) throws SQLException {
        Set<String> items = new HashSet<>();
        // userId를 기준으로 조회
        String sql = "SELECT DISTINCT item_name FROM " + LOGS_TABLE + " WHERE user_id = ?";
        
        try (Connection conn = recycleDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(rs.getString("item_name"));
                }
            }
        }
        return items;
    }

    /**
     * 현재 테이블 데이터를 DB에 덮어씁니다 (기존 기록 삭제 후 삽입).
     */
    public void rewriteDbData(String userId, List<String> currentItems, Map<String, Integer> itemPoints) throws SQLException {
        // 기존 로그 삭제 (해당 사용자 ID에 대해서만)
        String deleteSql = "DELETE FROM " + LOGS_TABLE + " WHERE user_id = ?";
        // 새 로그 삽입
        String insertSql =
                "INSERT INTO " + LOGS_TABLE + "(user_id, item_name, point, timestamp) VALUES(?, ?, ?, ?)";

        try (Connection conn = recycleDB.connect();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            conn.setAutoCommit(false); 
            
            // 1. 기존 기록 삭제
            deleteStmt.setString(1, userId);
            deleteStmt.executeUpdate(); 

            // 2. 새 기록 삽입 (userId 포함)
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            for (String item : currentItems) {
                int point = itemPoints.getOrDefault(item, 0);
                insertStmt.setString(1, userId);
                insertStmt.setString(2, item);
                insertStmt.setInt(3, point);
                insertStmt.setString(4, timestamp);
                insertStmt.addBatch(); 
            }

            insertStmt.executeBatch(); 
            conn.commit(); 
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * 사용자의 총 포인트를 갱신합니다.
     * @param userId 포인트 적립 대상 사용자 ID
     * @param newPoints 새로 적립할 포인트
     */
    public void addPointsToUser(String userId, int newPoints) throws SQLException {
        // user_points 테이블에 INSERT 또는 UPDATE (MySQL의 ON DUPLICATE KEY UPDATE 활용)
        String updateSql = "INSERT INTO " + POINTS_TABLE + " (user_id, total_points, last_update) VALUES (?, ?, ?) "
                         + "ON DUPLICATE KEY UPDATE total_points = total_points + VALUES(total_points), last_update = VALUES(last_update)";
        
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        try (Connection conn = recycleDB.connect();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            updateStmt.setString(1, userId);
            updateStmt.setInt(2, newPoints);
            updateStmt.setString(3, timestamp);
            
            updateStmt.executeUpdate();
        }
    }
}