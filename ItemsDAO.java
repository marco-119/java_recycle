package recycle.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import recycle.DTO.ItemsDTO;

public class ItemsDAO {

    // UsersDAO와 동일한 DB 연결 정보
    private final String DB_URL = "jdbc:mysql://223.130.155.245:3306/recycle?serverTimezone=UTC&characterEncoding=UTF-8";
    private final String DB_ID = "remote_user";
    private final String DB_PASSWORD = "fjf0301!";

    // 공통: DB 연결 메서드
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, DB_ID, DB_PASSWORD);
    }

    // 공통: 자원 해제 메서드
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // 분류별 품목 가져오기 (getItemsByCategory) - SELECT
    // ---------------------------------------------------------------
    public List<ItemsDTO> getItemsByCategory(String categoryId) {
        List<ItemsDTO> list = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // 해당 분류ID를 가진 모든 품목을 이름순(오름차순)으로 조회
        String sql = "SELECT * FROM ITEMS WHERE CATEGORY_ID = ? ORDER BY ITEM_NAME ASC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, categoryId);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // DB에서 꺼낸 데이터를 DTO 객체로 변환
                ItemsDTO item = new ItemsDTO(
                    rs.getString("ITEM_ID"), 		//품목ID
                    rs.getString("ITEM_NAME"), 		//품목명
                    rs.getString("CATEGORY_ID"), 	//분류ID
                    rs.getString("DISPOSAL_GUIDE") 	//배출방법설명
                );
                
                // 리스트에 추가
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        
        return list; // 완성된 리스트 반환
    }    
}