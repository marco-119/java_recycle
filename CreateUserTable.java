package recycle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserTable {

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

                //기존에 users 테이블이 있다면 삭제(테이블 수정 필요할 시 사용)
                //String dropSql = "DROP TABLE IF EXISTS users";
                //stmt.executeUpdate(dropSql);

                // [속성]
                // user_id: 회원아이디
                // nickname: 닉네임
                // password: 비밀번호
                // total_points: 누적포인트
                // balance_points: 보유포인트
                // attendance_streak: 연속출석횟수
                // is_admin: 관리자여부
                
                String createSql = "CREATE TABLE users ("
                        + "user_id VARCHAR(20) NOT NULL PRIMARY KEY COMMENT '회원아이디', "
                        + "nickname VARCHAR(20) NOT NULL UNIQUE COMMENT '닉네임(중복금지)', "
                        + "password VARCHAR(20) NOT NULL COMMENT '비밀번호', "
                        + "total_points INT NOT NULL DEFAULT 0 CHECK (total_points >= 0) COMMENT '누적포인트', "
                        + "balance_points INT NOT NULL DEFAULT 0 CHECK (balance_points >= 0) COMMENT '보유포인트', "
                        + "attendance_streak INT NOT NULL DEFAULT 0 CHECK (attendance_streak >= 0) COMMENT '연속출석횟수', "
                        + "is_admin BOOLEAN NOT NULL DEFAULT FALSE COMMENT '관리자여부' "
                        + ")";

                stmt.executeUpdate(createSql);
                System.out.println("'users' 테이블 생성 완료!");
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