package recycle.createTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateItemsTable {

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

                //기존에 ITEMS 테이블이 있다면 삭제(테이블 수정 필요할 시 사용)
                String dropSql = "DROP TABLE IF EXISTS ITEMS";
                stmt.executeUpdate(dropSql);

                // [속성]
                // ITEM_ID: 품목번호
                // ITEM_NAME: 품목명
                // DISPOSAL_GUIDE: 배출방법설명
                // CATEGORY_ID: 분류번호
                
                String createSql = "CREATE TABLE ITEMS ("
                        + "ITEM_ID VARCHAR(10) NOT NULL PRIMARY KEY COMMENT '품목번호', "
                        + "ITEM_NAME VARCHAR(20) NOT NULL UNIQUE COMMENT '품목명', "
                        + "DISPOSAL_GUIDE VARCHAR(1000) NOT NULL COMMENT '배출방법설명', "
                        + "CATEGORY_ID VARCHAR(10) NOT NULL COMMENT '분류번호', "
                        + "CONSTRAINT fk_items_category FOREIGN KEY (CATEGORY_ID) "
                        + "REFERENCES CATEGORIES(CATEGORY_ID) ON UPDATE CASCADE" // 부모 테이블(CATEGORIES) 참조
                        + ")";

                stmt.executeUpdate(createSql);
                System.out.println("'ITEMS' 테이블 생성 완료!");
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