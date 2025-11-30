package recycle.TABLE;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUsersTable {

    // DB 연결 정보
    private static final String DB_URL = "jdbc:mysql://223.130.155.245:3306/recycle?serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DB_ID = "remote_user";
    private static final String DB_PASSWORD = "fjf0301!";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //JDBC 드라이버를 로드
            conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PASSWORD); //url, id, pw로 db와 연결

            if (conn != null) {
                System.out.println("DB 연결 성공! 테이블 생성 시작");
                stmt = conn.createStatement();

                //기존에 USERS 테이블이 있다면 삭제(테이블 수정 필요할 시 사용)
                //String dropSql = "DROP TABLE IF EXISTS USERS";
                //stmt.executeUpdate(dropSql);

                // [속성]
                // USER_ID: 회원아이디
                // NICKNAME: 닉네임
                // PASSWORD: 비밀번호
                // TOTAL_POINTS: 누적포인트
                // BALANCE_POINTS: 보유포인트
                // ATTENDANCE_STREAK: 연속출석횟수
                // IS_ADMIN: 관리자여부
                
                String createSql = "CREATE TABLE USERS ("
                        + "USER_ID VARCHAR(20) NOT NULL PRIMARY KEY COMMENT '회원아이디', "
                        + "NICKNAME VARCHAR(20) NOT NULL UNIQUE COMMENT '닉네임(중복금지)', "
                        + "PASSWORD VARCHAR(20) NOT NULL COMMENT '비밀번호', "
                        + "TOTAL_POINTS INT NOT NULL DEFAULT 0 CHECK (TOTAL_POINTS >= 0) COMMENT '누적포인트', "
                        + "BALANCE_POINTS INT NOT NULL DEFAULT 0 CHECK (BALANCE_POINTS >= 0) COMMENT '보유포인트', "
                        + "ATTENDANCE_STREAK INT NOT NULL DEFAULT 0 CHECK (ATTENDANCE_STREAK >= 0) COMMENT '연속출석횟수', "
                        + "IS_ADMIN BOOLEAN NOT NULL DEFAULT FALSE COMMENT '관리자여부' "
                        + ")";

                stmt.executeUpdate(createSql);
                System.out.println("'USERS' 테이블 생성 완료!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
            e.printStackTrace();
            
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}