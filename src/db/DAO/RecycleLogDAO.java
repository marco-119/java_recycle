package db.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; 
import java.util.Map;
import javax.swing.JOptionPane;
import java.util.LinkedHashMap;
import java.util.List;

import db.RecycleDB; 
import db.DAO.UserDAO;

public class RecycleLogDAO {

    private static final String LOGS_TABLE = "LOGS";
    private static final String ITEMS_TABLE = "ITEMS"; 
    
    private final UserDAO userDAO;


    public static class LogItem {
        public String itemName;
        
        public LogItem(String itemName) {
            this.itemName = itemName;
        }
    }


    public RecycleLogDAO() throws Exception {
        this.userDAO = new UserDAO(); 
    }


    public static void initializeDatabase() {
        String createItems = 
                "CREATE TABLE IF NOT EXISTS " + ITEMS_TABLE + " (" +
                        "ITEM_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "ITEM_NAME VARCHAR(50) NOT NULL UNIQUE," +
                        "POINT INT NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        String createLogs =
                "CREATE TABLE IF NOT EXISTS " + LOGS_TABLE + " (" +
                        "LOG_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "USER_ID VARCHAR(50) NOT NULL," + 
                        "ITEM_NAME VARCHAR(50) NOT NULL," +
                        "POINT INT NOT NULL," + 
                        "TIMESTAMP DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "IS_EARNED BOOLEAN NOT NULL DEFAULT TRUE," + 
                        "KEY FK_LOGS_ITEM (ITEM_NAME)," +
                        "KEY FK_LOGS_USER (USER_ID)," +
                        "CONSTRAINT FK_LOGS_ITEM FOREIGN KEY (ITEM_NAME) REFERENCES " + ITEMS_TABLE + " (ITEM_NAME) ON DELETE RESTRICT ON UPDATE CASCADE," +
                        "CONSTRAINT FK_LOGS_USER FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE ON UPDATE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try (Connection conn = RecycleDB.connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createItems); 
            stmt.execute(createLogs);
            ensureMasterItems(); 
            
        } catch (SQLException e) {
            System.err.println("DB 초기화 오류: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "DB 초기화 중 오류가 발생했습니다: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void ensureMasterItems() throws SQLException {
        String insertItemsSql = "INSERT INTO " + ITEMS_TABLE + " (ITEM_NAME, POINT) VALUES (?, ?) "
                              + "ON DUPLICATE KEY UPDATE POINT=VALUES(POINT)"; 

        Map<String, Integer> initialItems = new LinkedHashMap<>();
        initialItems.put("종이", 15);
        initialItems.put("종이팩", 20);
        initialItems.put("플라스틱", 10);
        initialItems.put("페트병", 30);
        initialItems.put("캔/고철", 40);
        initialItems.put("유리병", 25);
        initialItems.put("스티로폼", 10);
        initialItems.put("비닐", 10); 
        initialItems.put("기타", 5); 

        try (Connection conn = RecycleDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertItemsSql)) {
            
            for (Map.Entry<String, Integer> entry : initialItems.entrySet()) {
                pstmt.setString(1, entry.getKey());
                pstmt.setInt(2, entry.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public Map<String, Integer> getAllItemPoints() throws SQLException {
        String sql = "SELECT ITEM_NAME, POINT FROM " + ITEMS_TABLE + " ORDER BY ITEM_NAME ASC";
        Map<String, Integer> itemPoints = new LinkedHashMap<>();
        
        try (Connection conn = RecycleDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                itemPoints.put(rs.getString("ITEM_NAME"), rs.getInt("POINT"));
            }
        }
        return itemPoints;
    }

    // ---------------------- LOGS 테이블 관리 ----------------------
    
    //LOGS 테이블의 모든 기록을 삭제합니다.
     
    public void clearAllLogs() throws SQLException {
        String sql = "TRUNCATE TABLE " + LOGS_TABLE;
        try (Connection conn = RecycleDB.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
    
    //같은 날짜에 같은 품목이 이미 DB에 기록되었는지 확인합니다.
     
    private boolean isDuplicateToday(Connection conn, String userId, String itemName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + LOGS_TABLE + " WHERE USER_ID = ? AND ITEM_NAME = ? AND DATE(TIMESTAMP) = CURDATE()";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, itemName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    //오늘 DB에 저장된 분리수거 항목 목록을 조회합니다.

     
    public List<LogItem> getTodayRecycleLogs(String userId) throws SQLException {
        List<LogItem> logs = new ArrayList<>();
        String sql = "SELECT ITEM_NAME FROM " + LOGS_TABLE + " WHERE USER_ID = ? AND DATE(TIMESTAMP) = CURDATE() ORDER BY TIMESTAMP ASC";
        
        try (Connection conn = RecycleDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new LogItem(rs.getString("ITEM_NAME")));
                }
            }
        }
        return logs;
    }
    
    //목록 항목을 DB에 저장하고, 중복이 아닌 항목에 대해 포인트를 적립합니다. 
     
    public int insertRecycleLogsAndEarn(String userId, List<String> itemsLog, Map<String, Integer> itemPoints) throws SQLException {
        Connection conn = null; 
        int totalPointsEarned = 0;
        
        try {
            conn = RecycleDB.connect();
            conn.setAutoCommit(false); 

            // 1. 중복 검사 및 삽입할 항목 목록 정리
            List<String> itemsToInsert = new ArrayList<>();
            for (String item : itemsLog) {
                if (!isDuplicateToday(conn, userId, item)) {
                    itemsToInsert.add(item);
                    totalPointsEarned += itemPoints.getOrDefault(item, 0);
                }
            }
            
            // 2. LOGS 기록 
            if (!itemsToInsert.isEmpty()) {
                String insertSql = "INSERT INTO " + LOGS_TABLE + " (USER_ID, ITEM_NAME, POINT, IS_EARNED) VALUES (?, ?, ?, TRUE)"; 
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (String item : itemsToInsert) {
                        insertStmt.setString(1, userId);
                        insertStmt.setString(2, item); 
                        insertStmt.setInt(3, itemPoints.getOrDefault(item, 0));
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch(); 
                }
            }
            
            // 3. 포인트 적립 
            if (totalPointsEarned > 0) {
                 userDAO.addPointsToUser(conn, userId, totalPointsEarned); 
            }
            
            conn.commit(); 
            return totalPointsEarned;
            
        } catch (SQLException e) {
            System.err.println("로그 기록 및 포인트 적립 중 DB 오류: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException rollbackEx) {
                    System.err.println("롤백 오류: " + rollbackEx.getMessage());
                }
            }
            throw e; 
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Connection 닫기 오류: " + closeEx.getMessage());
                }
            }
        }
    }
}