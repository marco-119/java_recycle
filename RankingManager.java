package recycle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RankingManager {

	private static final String DB_URL = "jdbc:mysql://223.130.155.245:3306/recycle?serverTimezone=UTC&characterEncoding=UTF-8";
	private static final String DB_USER = "remote_user";
	private static final String DB_PASSWORD = "fjf0301!";

    public RankingManager() {
        // 생성자
    }

    /**
     * DB 연결을 도와주는 내부 메소드
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * (기능 1) DB에서 현재 랭킹 Top 5 리스트를 가져오는 메소드
     */
    public List<ScoreEntry> getRankings() {
        List<ScoreEntry> currentRanks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 랭킹 쿼리: 점수(totalPoints)가 높은 순서대로 5명 가져오기
        String sql = "SELECT username, totalPoints FROM Users ORDER BY totalPoints DESC LIMIT 5";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // 결과(ResultSet)를 한 줄씩 읽어서 리스트에 담기
            while (rs.next()) {
                String name = rs.getString("username");
                int points = rs.getInt("totalPoints");
                
                // ScoreEntry 객체로 포장해서 리스트에 추가
                currentRanks.add(new ScoreEntry(name, points));
            }

        } catch (SQLException e) {
            System.err.println("랭킹 조회 중 오류 발생!");
            e.printStackTrace();
        } finally {
            // 자원 해제 (닫기)
            close(conn, pstmt, rs);
        }
        
        return currentRanks;
    }

    /**
     * (기능 2) DB에 점수를 누적(저장)하는 메소드
     */
    public void accumulatePoints(String name, int pointsToAdd) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // 점수 누적 쿼리 (MySQL 전용 문법)
        // 1. Users 테이블에 이름(username)과 점수(totalPoints)를 넣습니다.
        // 2. 만약 이미 있는 이름이라면(ON DUPLICATE KEY UPDATE), 기존 점수에 새 점수를 더합니다.
        String sql = "INSERT INTO Users (username, totalPoints) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE totalPoints = totalPoints + ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            // 물음표(?) 채우기
            pstmt.setString(1, name);        // 첫 번째 ? : 이름
            pstmt.setInt(2, pointsToAdd);    // 두 번째 ? : 점수 (신규일 때)
            pstmt.setInt(3, pointsToAdd);    // 세 번째 ? : 더할 점수 (기존일 때)

            // 쿼리 실행 (INSERT, UPDATE는 executeUpdate 사용)
            pstmt.executeUpdate();
            System.out.println("✅ 점수 저장 완료: " + name + " (+" + pointsToAdd + "P)");

        } catch (SQLException e) {
            System.err.println("점수 저장 중 오류 발생!");
            e.printStackTrace();
        } finally {
            // 자원 해제
            close(conn, pstmt, null);
        }
    }

    /**
     * 자원 해제(close)를 편하게 하기 위한 내부 메소드
     */
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}