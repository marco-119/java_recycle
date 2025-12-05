package recycle.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import recycle.DTO.UsersDTO;

public class UsersDAO {

    // DB 연결 정보
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
    // 1. 회원가입 (addUser) - INSERT
    // ---------------------------------------------------------------
    public int addUser(UsersDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        String sql = "INSERT INTO USERS (USER_ID, NICKNAME, PASSWORD, TOTAL_POINTS, BALANCE_POINTS, ATTENDANCE_STREAK, IS_ADMIN) VALUES (?, ?, ?, 0, 0, 0, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getNickname());
            pstmt.setString(3, user.getPassword());
            pstmt.setBoolean(4, user.isAdmin()); // 관리자 여부

            result = pstmt.executeUpdate(); // 실행 결과 반환 (성공시 1)
            System.out.println("회원가입 완료: " + user.getNickname());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, null);
        }
        return result;
    }
    
    // ---------------------------------------------------------------
    // 2. 아이디 중복 체크 (isIdExists) - SELECT
    // ---------------------------------------------------------------
    public boolean isIdExists(String userId) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT count(*) FROM USERS WHERE USER_ID = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1); 
                if (count > 0) exists = true; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return exists;
    }
    
    // ---------------------------------------------------------------
    // 3. 닉네임 중복 체크 (isNicknameExists) - SELECT
    // ---------------------------------------------------------------
    public boolean isNicknameExists(String nickname) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT count(*) FROM USERS WHERE NICKNAME = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) exists = true; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return exists;
    }

    // ---------------------------------------------------------------
    // 4. 로그인 한 회원 정보 DTO에 담기 (getUser) - SELECT
    // ---------------------------------------------------------------
    public UsersDTO getUser(String userId) {
        UsersDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new UsersDTO(
                    rs.getString("USER_ID"),
                    rs.getString("NICKNAME"),
                    rs.getString("PASSWORD"),
                    rs.getInt("TOTAL_POINTS"),
                    rs.getInt("BALANCE_POINTS"),
                    rs.getInt("ATTENDANCE_STREAK"),
                    rs.getBoolean("IS_ADMIN")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return user;
    }
    
    // ---------------------------------------------------------------
    // 5. 회원 포인트 및 정보 수정 (퀴즈, 분리수거, 구매 공용) - UPDATE
    // ---------------------------------------------------------------
    public void updateUserPoint(UsersDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE USERS SET TOTAL_POINTS = ?, BALANCE_POINTS = ?, ATTENDANCE_STREAK = ? WHERE USER_ID = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, user.getTotalPoints());      // 누적 포인트
            pstmt.setInt(2, user.getBalancePoints());    // 보유 포인트
            pstmt.setInt(3, user.getAttendanceStreak()); // 연속 출석 횟수
            pstmt.setString(4, user.getUserId());        // 해당 유저 ID

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("회원 정보(포인트/스트릭) 업데이트 완료");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("회원 정보 업데이트 실패");
        } finally {
            close(conn, pstmt, null);
        }
    }
    
    // ---------------------------------------------------------------
    // 6. 전체 랭킹 조회 (getRankings) - SELECT
    // ---------------------------------------------------------------
    public List<UsersDTO> getRankings() {
        List<UsersDTO> rankList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM USERS ORDER BY TOTAL_POINTS DESC LIMIT 5";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                UsersDTO user = new UsersDTO();
                user.setNickname(rs.getString("NICKNAME"));
                user.setTotalPoints(rs.getInt("TOTAL_POINTS"));
                
                rankList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return rankList; 
    }

    // ---------------------------------------------------------------
    // 7. 회원 탈퇴 (deleteUser) - DELETE
    // ---------------------------------------------------------------
    public void deleteUser(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM USERS WHERE USER_ID = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            int result = pstmt.executeUpdate();
            if (result > 0) System.out.println("회원 탈퇴 완료");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, null);
        }
    }
}