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
    public void addUser(UsersDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        // 아이디, 닉네임, 비번, 초기 포인트(0), 초기 잔액(0), 연속출석(0), 관리자(false)
        String sql = "INSERT INTO users (user_id, nickname, password, total_points, balance_points, attendance_streak, is_admin) VALUES (?, ?, ?, 0, 0, 0, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getNickname());
            pstmt.setString(3, user.getPassword());
            pstmt.setBoolean(4, user.isAdmin()); // 관리자 여부

            pstmt.executeUpdate(); // 실행
            System.out.println("회원가입 완료: " + user.getNickname());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, null);
        }
    }
    
    // ---------------------------------------------------------------
    // 2. 중복 체크 (isIdExists) - SELECT
    //    - 회원가입 시 아이디 중복 확인용. 존재하면 true, 없으면 false 반환
    // ---------------------------------------------------------------
    public boolean isIdExists(String userId) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT count(*) FROM users WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1); // 조회된 개수
                if (count > 0) exists = true; // 1개 이상이면 중복
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
    //    - 회원가입 시 아이디 중복 확인용. 존재하면 true, 없으면 false 반환
    // ---------------------------------------------------------------
    public boolean isNicknameExists(String nickname) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 아이디 대신 닉네임으로 개수 조회
        String sql = "SELECT count(*) FROM users WHERE nickname = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) exists = true; // 1개 이상이면 이미 존재하는 닉네임
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

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // DB에서 꺼내서 DTO에 담기
                user = new UsersDTO(
                    rs.getString("user_id"),
                    rs.getString("nickname"),
                    rs.getString("password"),
                    rs.getInt("total_points"),
                    rs.getInt("balance_points"),
                    rs.getInt("attendance_streak"),
                    rs.getBoolean("is_admin")
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
    // 5. 퀴즈/출석체크 결과 저장 (updateQuizResult)
    //    - 변경 대상: 누적포인트, 보유포인트, 연속출석횟수
    // ---------------------------------------------------------------
    public void updateQuizResult(UsersDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // 누적포인트, 보유포인트, 연속 출석 횟수 수정
        String sql = "UPDATE users SET total_points = ?, balance_points = ?, attendance_streak = ? WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, user.getTotalPoints());      // 갱신된 누적포인트
            pstmt.setInt(2, user.getBalancePoints());    // 갱신된 보유포인트
            pstmt.setInt(3, user.getAttendanceStreak()); // 갱신된 연속출석횟수
            pstmt.setString(4, user.getUserId());        // 대상 회원 ID

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("퀴즈 결과 저장 완료");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("퀴즈 결과 저장 실패");
        } finally {
            close(conn, pstmt, null);
        }
    }

    // ---------------------------------------------------------------
    // 6. 분리수거 포인트 적립 (updateRecyclePoints) - UPDATE
    //    - 적립 시 변경 속성: 누적포인트, 보유포인트
    // ---------------------------------------------------------------
    public void updateRecyclePoints(UsersDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // '연속 출석 횟수'는 쿼리에서 제외함
        String sql = "UPDATE users SET total_points = ?, balance_points = ? WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, user.getTotalPoints());   // 갱신된 누적포인트
            pstmt.setInt(2, user.getBalancePoints()); // 갱신된 보유포인트
            pstmt.setString(3, user.getUserId());     // 대상 회원 ID

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("✅ 분리수거 포인트 적립 완료");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ 분리수거 포인트 적립 실패");
        } finally {
            close(conn, pstmt, null);
        }
    }
    
    // ---------------------------------------------------------------
    // 7. 전체 랭킹 조회 (getRankings) - SELECT
    //    - 누적 포인트(total_points) 내림차순으로 상위 5명만 가져오기
    // ---------------------------------------------------------------
    public List<UsersDTO> getRankings() {
        List<UsersDTO> rankList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 누적포인트 기준 내림차순(DESC) 정렬 후 상위 5개만 조회
        String sql = "SELECT * FROM users ORDER BY total_points DESC LIMIT 5";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                UsersDTO user = new UsersDTO();
                		
                user.setNickname(rs.getString("nickname"));
                user.setTotalPoints(rs.getInt("total_points"));
                
                rankList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return rankList; // 리스트 리턴
    }

    // ---------------------------------------------------------------
    // 8. 회원 탈퇴 (deleteUser) - DELETE
    // ---------------------------------------------------------------
    public void deleteUser(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM users WHERE user_id = ?";

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