package recycle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryDAO {

    // 1. 테이블 자동 생성 
    public void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS purchase_history (" +
                     " purchase_id INT AUTO_INCREMENT PRIMARY KEY," +
                     " user_id VARCHAR(50) NOT NULL," +
                     " item_name VARCHAR(50) NOT NULL," +
                     " point_cost INT NOT NULL," +
                     " purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP" +
                     ");";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("✅ 구매 내역 테이블(purchase_history) 준비 완료");
            
        } catch (SQLException e) {
            System.err.println("구매 테이블 생성 실패");
            e.printStackTrace();
        }
    }

    /**
     * 2. 구매 내역 저장 (INSERT)
     * - DTO 없이 변수들을 직접 받아서 저장합니다.
     */
    public void insertPurchase(String userId, String itemName, int pointCost) {
        // 테이블이 없을 경우를 대비해 생성 시도
        initializeTable();

        String sql = "INSERT INTO purchase_history (user_id, item_name, point_cost, purchase_date) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, itemName);
            pstmt.setInt(3, pointCost);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("✅ 구매 기록 저장 성공: " + itemName + " (-" + pointCost + "P)");
            }

        } catch (SQLException e) {
            System.err.println("❌ 구매 기록 저장 실패");
            e.printStackTrace();
        }
    }

    /**
     * 3. 내 구매 내역 조회 (SELECT)
     * - DTO 객체 대신, 화면에 바로 뿌리기 좋은 '문자열 리스트'로 반환합니다.
     */
    public List<String> getUserPurchases(String userId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM purchase_history WHERE user_id = ? ORDER BY purchase_date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getString("item_name");
                    int cost = rs.getInt("point_cost");
                    String date = rs.getString("purchase_date"); // 년-월-일 시:분:초

                    // 문자열로 예쁘게 포장해서 리스트에 담기
                    // 예: "[2025-11-27 15:30] 문화상품권 5000원권 (-5000P)"
                    String line = String.format("[%s] %s (-%dP)", date, item, cost);
                    list.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}