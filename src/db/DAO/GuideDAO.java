package db.DAO;

import java.sql.Connection;
import db.RecycleDB;
import db.DTO.GuideDTO; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections; 

public class GuideDAO {
    
    private static final String CATEGORIES_TABLE = "CATEGORIES";
    private static final String ITEMS_TABLE = "ITEMS"; 

    private static final String CSS_STYLES = 
        "<style>"
        + "h2{ margin-top: 10px; margin-bottom: 5px; font-size: 1.1em; color: #007bff; }"
        + "h3 { margin-top: 10px; margin-bottom: 5px; font-size: 1.0em; color: #333; }"
        + "ul { margin-top: 5px; margin-left: 10px; padding-left: 10px; }"
        + "li { margin-bottom: 3px; list-style-type: disc; }"
        + ".note { padding-left: 15px; list-style-type: none; font-style: italic; color: #ff5722; }"
        + "hr { border: 0; height: 1px; background-color: #eee; margin: 10px 0; }"
        + "</style>";

    private static final Map<String, Integer> CATEGORY_REWARDS = new LinkedHashMap<>();
    static {
        CATEGORY_REWARDS.put("종이", 15);
        CATEGORY_REWARDS.put("비닐", 10);
        CATEGORY_REWARDS.put("유리병", 25);
        CATEGORY_REWARDS.put("종이팩", 20);
        CATEGORY_REWARDS.put("캔ㆍ고철", 40);
        CATEGORY_REWARDS.put("스티로폼", 10);
        CATEGORY_REWARDS.put("플라스틱", 10);
        CATEGORY_REWARDS.put("기타", 5);
    }
    
    private static final Map<String, Map<String, String>> INITIAL_GUIDE_DATA = new LinkedHashMap<>();
    static {
        // 1. 종이
        Map<String, String> paperItems = new LinkedHashMap<>();
        paperItems.put("신문지/책자", "<h3>세부품목</h3><li>신문지, 책자, 노트 등</li><h3>배출방법</h3><ul><li>물기에 젖지 않도록 하고, 스프링 등 다른 재질을 제거한 후 끈으로 묶어서 배출</li></ul>");
        paperItems.put("상자류", "<h3>세부품목</h3><li>종이박스, 골판지 등</li><h3>배출방법</h3><ul><li>테이프, 철핀 등 종이류와 다른 재질은 제거한 후 반듯하게 펴서 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("종이", paperItems);

        // 2. 비닐
        Map<String, String> vinylItems = new LinkedHashMap<>();
        vinylItems.put("비닐포장재/봉투", "<h2>합성수지류</h2><h3>세부품목</h3><li>비닐포장재, 1회용 비닐봉투 </li><h3>배출방법</h3><ul><li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li><li>흩날리지 않도록 봉투에 담아 배출 (분리배출표시가 없는 비닐류 포함)</li></ul>");
        vinylItems.put("랩 필름", "<h3>세부품목</h3><li>깨끗하게 이물질 제거가 되지 않은 랩필름</li><h3>배출방법</h3><ul><li class=\"note\">* 종량제봉투로 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("비닐", vinylItems);

        // 3. 유리병
        Map<String, String> glassItems = new LinkedHashMap<>();
        glassItems.put("음료수병/주류병", "<h2>유리병</h2><h3>세부품목</h3><li>음료수병, 와인병, 양주병 등</li><h3>배출방법</h3><ul><li>내용물을 비우고 헹군 후 배출</li><li>빈용기보증금 대상 유리병은 소매점으로 반납하여 환급</li></ul>");
        glassItems.put("깨진 유리제품", "<h3>세부품목</h3><li>깨진 유리, 코팅된 유리</li><h3>배출방법</h3><ul><li class=\"note\">* 신문지 등에 싸서 종량제 봉투 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("유리병", glassItems);
        
        // 4. 종이팩
        Map<String, String> packItems = new LinkedHashMap<>();
        packItems.put("우유팩/멸균팩", "<h2>종이팩</h2><h3>세부품목</h3><li>우유팩, 두유팩, 주스팩 등</li><h3>배출방법</h3><ul><li>내용물을 비우고 헹군 후 말려서 빨대, 비닐 등 다른 재질은 제거하고 전용수거함에 배출</li></ul>");
        packItems.put("종이컵", "<h2>종이컵</h2><h3>세부품목</h3><li>종이컵</li><h3>배출방법</h3><ul><li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("종이팩", packItems);

        // 5. 캔ㆍ고철
        Map<String, String> metalItems = new LinkedHashMap<>();
        metalItems.put("음료/주류캔", "<h2>금속캔</h2><h3>세부품목</h3><li>음료·주류캔, 통조림캔</li><h3>배출방법</h3><ul><li>내용물을 비우고 헹군 후 플라스틱 뚜껑 등 다른 재질은 제거하여 배출</li></ul>");
        metalItems.put("부탄가스/스프레이", "<h2>기타캔류</h2><h3>세부품목</h3><li>부탄가스 용기, 살충제 용기 등</li><h3>배출방법</h3><ul><li>내용물을 완전히 제거(가스 배출)한 후 배출</li></ul>");
        metalItems.put("고철류", "<h2>고철류</h2><h3>세부품목</h3><li>공기구, 철사, 못, 알루미늄 제품 등</li><h3>배출방법</h3><ul><li>이물질이 섞이지 않도록 한 후 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("캔ㆍ고철", metalItems);
        
        // 6. 스티로폼
        Map<String, String> styrofoamItems = new LinkedHashMap<>();
        styrofoamItems.put("스티로폼 완충재", "<h2>합성수지류</h2><h3>세부품목</h3><li>스티로폼 완충재, 발포스티렌상자</li><h3>배출방법</h3><ul><li>이물질을 제거하고 부착 상표를 제거한 후 배출</li><li>오염된 스티로폼은 종량제봉투로 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("스티로폼", styrofoamItems);

        // 7. 플라스틱
        Map<String, String> plasticItems = new LinkedHashMap<>();
        plasticItems.put("용기/트레이류", "<h2>합성수지류</h2><h3>세부품목</h3><li>PET, PVC, PE, PP, PS 재질 등의 용기·트레이류</li><h3>배출방법</h3><ul><li>내용물을 비우고 헹군 후 부착상표, 부속품 등 다른 재질은 제거한 후 배출</li></ul>");
        plasticItems.put("칫솔/장난감", "<h3>세부품목</h3><li>플라스틱 이외의 재질이 부착된 완구, 칫솔 등</li><h3>배출방법</h3><ul><li class=\"note\">* 종량제봉투 등으로 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("플라스틱", plasticItems);
        
        // 8. 기타
        Map<String, String> etcItems = new LinkedHashMap<>();
        etcItems.put("의류/원단류", "<h2>의류 및 원단류</h2><h3>세부품목</h3><li>면의류, 기타 의류</li><h3>배출방법</h3><ul><li>폐의류 전용수거함에 배출</li></ul>");
        etcItems.put("일반쓰레기", "<h3>세부품목</h3><li>음식물쓰레기, 담배꽁초, 폐건전지 등</li><h3>배출방법</h3><ul><li class=\"note\">* 종량제봉투 또는 전용수거함에 분리 배출</li></ul>");
        INITIAL_GUIDE_DATA.put("기타", etcItems);
    }
    public static class ItemDetail {
        public String itemName;
        public String categoryName;
        public String disposalGuide; 

        public ItemDetail(String itemName, String categoryName, String disposalGuide) {
            this.itemName = itemName;
            this.categoryName = categoryName;
            this.disposalGuide = disposalGuide;
        }
    }

    public static Map<String, Integer> getAllCategoryRewards() throws SQLException {
        Map<String, Integer> itemRewards = new LinkedHashMap<>();

        List<ItemDetail> allItems = getAllItems();

        for (ItemDetail item : allItems) {
            int points = CATEGORY_REWARDS.getOrDefault(item.categoryName, 0); 
            
            itemRewards.put(item.itemName, points); 
        }
        
        return itemRewards;
    }
    public static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = RecycleDB.connect();
            stmt = conn.createStatement();

            // 1. ITEMS 테이블 삭제
            stmt.execute("DROP TABLE IF EXISTS " + ITEMS_TABLE); 
            System.out.println(ITEMS_TABLE + " 테이블 삭제 (초기화 목적).");

            // 2. CATEGORIES 테이블 삭제 (데이터 완전 초기화)
            stmt.execute("DROP TABLE IF EXISTS " + CATEGORIES_TABLE); 
            System.out.println(CATEGORIES_TABLE + " 테이블 삭제 (초기화 목적).");


            // 3. CATEGORIES 테이블 생성 (새로 생성)
            String createCategoriesSQL = 
                    "CREATE TABLE IF NOT EXISTS " + CATEGORIES_TABLE + " (" +
                            "`CATEGORY_ID` INT NOT NULL AUTO_INCREMENT COMMENT '카테고리 고유 ID'," +
                            "`CATEGORY_NAME` VARCHAR(50) NOT NULL COMMENT '카테고리 이름'," +
                            "`REWARD_POINTS` INT NOT NULL DEFAULT 0 COMMENT '해당 카테고리 품목의 기본 획득 포인트'," +
                            "PRIMARY KEY (`CATEGORY_ID`)," +
                            "UNIQUE KEY `CATEGORY_NAME_UNIQUE` (`CATEGORY_NAME`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            
            // 4. ITEMS 테이블 생성 (새로 생성)
            String createItemsSQL = 
                    "CREATE TABLE IF NOT EXISTS " + ITEMS_TABLE + " (" +
                            "`ITEM_ID` INT NOT NULL AUTO_INCREMENT COMMENT '품목 고유 ID'," +
                            "`ITEM_NAME` VARCHAR(100) NOT NULL COMMENT '세부 품목 이름'," +
                            "`CATEGORY_ID` INT NOT NULL COMMENT '소속 카테고리 ID'," +
                            "`DISPOSAL_GUIDE` LONGTEXT NOT NULL COMMENT '폐기 지침'," +
                            "PRIMARY KEY (`ITEM_ID`)," +
                            "UNIQUE KEY `ITEM_NAME_UNIQUE` (`ITEM_NAME`)," +
                            "KEY `CATEGORY_ID_idx` (`CATEGORY_ID`)," +
                            "CONSTRAINT `FK_ITEMS_CATEGORY` FOREIGN KEY (`CATEGORY_ID`) REFERENCES " + CATEGORIES_TABLE + " (`CATEGORY_ID`) ON DELETE RESTRICT ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            
            
            stmt.execute(createCategoriesSQL);
            System.out.println(CATEGORIES_TABLE + " 테이블 생성 완료.");
            
            stmt.execute(createItemsSQL);
            System.out.println(ITEMS_TABLE + " 테이블 생성 완료.");
            
            // 5. 초기 데이터 삽입 (8개 카테고리만 삽입 보장)
            insertInitialData(conn);
            
        } catch (SQLException e) {
            System.err.println("GuideDAO DB 초기화 오류: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("DB 자원 해제 오류: " + e.getMessage());
            }
        }
    }

    /**
     * CATEGORIES 및 ITEMS 테이블에 초기 데이터를 삽입합니다. 
     */
    private static void insertInitialData(Connection conn) throws SQLException {
        conn.setAutoCommit(false); // 트랜잭션 시작

        // 1. CATEGORIES 테이블 초기 데이터 삽입
        String insertCategoriesSql = "INSERT INTO " + CATEGORIES_TABLE + " (CATEGORY_NAME, REWARD_POINTS) VALUES (?, ?) "
                                   + "ON DUPLICATE KEY UPDATE REWARD_POINTS=VALUES(REWARD_POINTS)"; 

        try (PreparedStatement pstmt = conn.prepareStatement(insertCategoriesSql)) {
            for (Map.Entry<String, Integer> entry : CATEGORY_REWARDS.entrySet()) {
                pstmt.setString(1, entry.getKey());
                pstmt.setInt(2, entry.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        
        // 2. ITEMS 테이블 초기 데이터 삽입
        String selectCategoryIdSql = "SELECT CATEGORY_ID FROM " + CATEGORIES_TABLE + " WHERE CATEGORY_NAME = ?";
        String insertItemsSql = "INSERT INTO " + ITEMS_TABLE + " (ITEM_NAME, CATEGORY_ID, DISPOSAL_GUIDE) VALUES (?, ?, ?) "
                              + "ON DUPLICATE KEY UPDATE CATEGORY_ID=VALUES(CATEGORY_ID), DISPOSAL_GUIDE=VALUES(DISPOSAL_GUIDE)";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectCategoryIdSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertItemsSql)) {
            
            for (Map.Entry<String, Map<String, String>> categoryEntry : INITIAL_GUIDE_DATA.entrySet()) {
                String categoryName = categoryEntry.getKey();
                
                selectStmt.setString(1, categoryName);
                int categoryId = -1;
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        categoryId = rs.getInt("CATEGORY_ID");
                    }
                }
                
                if (categoryId != -1) {
                    for (Map.Entry<String, String> itemEntry : categoryEntry.getValue().entrySet()) {
                        String itemName = itemEntry.getKey();
                        String guideContent = itemEntry.getValue();
                        
                        insertStmt.setString(1, itemName);
                        insertStmt.setInt(2, categoryId);
                        insertStmt.setString(3, guideContent);
                        insertStmt.addBatch();
                    }
                }
            }
            insertStmt.executeBatch();
        }
        
        conn.commit();
        conn.setAutoCommit(true);
        System.out.println("가이드 데이터(카테고리 및 세부 품목) 삽입/업데이트 완료.");
    }
 
    public Map<String, Integer> getAllCategoryPoints() throws SQLException {
        Map<String, Integer> categoryPoints = new LinkedHashMap<>();
        String selectSql = "SELECT CATEGORY_NAME, REWARD_POINTS FROM " + CATEGORIES_TABLE + " ORDER BY CATEGORY_ID ASC";
        
        try (Connection conn = RecycleDB.connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            while (rs.next()) {
                categoryPoints.put(rs.getString("CATEGORY_NAME"), rs.getInt("REWARD_POINTS"));
            }
        } 
        return categoryPoints;
    }

    /**
     * 카테고리 목록을 조회합니다. 
     */
    public static List<String> getAllCategoryNames() throws SQLException {
        List<String> categories = new ArrayList<>();
        String selectSql = "SELECT CATEGORY_NAME FROM " + CATEGORIES_TABLE + " ORDER BY CATEGORY_ID ASC";
        
        try (Connection conn = RecycleDB.connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("CATEGORY_NAME"));
            }
        } 
        return categories;
    }
    
    /**
     * 특정 카테고리의 획득 포인트를 조회합니다. 
     */
    public static int getRewardPointForCategory(Connection conn, String category) throws SQLException {
        String selectSql = "SELECT REWARD_POINTS FROM " + CATEGORIES_TABLE + " WHERE CATEGORY_NAME = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("REWARD_POINTS");
                }
            }
        } 
        return 0; 
    }
    
    /**
     * 특정 카테고리의 획득 포인트를 조회합니다. 
     */
    public static int getRewardPointForCategory(String category) throws SQLException {
        try (Connection conn = RecycleDB.connect()) {
            return getRewardPointForCategory(conn, category);
        }
    }

    public static Map<String, Integer> getAllCategoryNamesAndIds() throws SQLException {
        // LinkedHashMap을 사용하여 DB의 ID 순서대로 카테고리를 유지합니다.
        Map<String, Integer> categories = new LinkedHashMap<>();
        String selectSql = "SELECT CATEGORY_NAME, CATEGORY_ID FROM " + CATEGORIES_TABLE + " ORDER BY CATEGORY_ID ASC";
        
        try (Connection conn = RecycleDB.connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            while (rs.next()) {
                categories.put(rs.getString("CATEGORY_NAME"), rs.getInt("CATEGORY_ID"));
            }
        } 
        return categories;
    }

    public static List<ItemDetail> getItemsByCategory(int categoryId) throws SQLException {
        List<ItemDetail> items = new ArrayList<>();
        
        String selectSql = "SELECT i.ITEM_NAME, c.CATEGORY_NAME, i.DISPOSAL_GUIDE "
                         + "FROM " + ITEMS_TABLE + " i "
                         + "JOIN " + CATEGORIES_TABLE + " c ON i.CATEGORY_ID = c.CATEGORY_ID "
                         + "WHERE i.CATEGORY_ID = ? "
                         + "ORDER BY i.ITEM_ID ASC";
        
        try (Connection conn = RecycleDB.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            
            pstmt.setInt(1, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String itemName = rs.getString("ITEM_NAME");
                    String categoryName = rs.getString("CATEGORY_NAME");
                    String disposalGuide = rs.getString("DISPOSAL_GUIDE");
                    
                    items.add(new ItemDetail(itemName, categoryName, disposalGuide)); 
                }
            }
        } 
        return items;
    }
    
    
    public static List<ItemDetail> getAllItems() throws SQLException {
        List<ItemDetail> items = new ArrayList<>();
        
        String selectSql = "SELECT i.ITEM_NAME, c.CATEGORY_NAME, i.DISPOSAL_GUIDE "
                         + "FROM " + ITEMS_TABLE + " i "
                         + "JOIN " + CATEGORIES_TABLE + " c ON i.CATEGORY_ID = c.CATEGORY_ID "
                         + "ORDER BY c.CATEGORY_ID ASC, i.ITEM_ID ASC";
        
        try (Connection conn = RecycleDB.connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            while (rs.next()) {
                String itemName = rs.getString("ITEM_NAME");
                String categoryName = rs.getString("CATEGORY_NAME");
                String disposalGuide = rs.getString("DISPOSAL_GUIDE");
                
                items.add(new ItemDetail(itemName, categoryName, disposalGuide)); 
            }
        } 
        return items;
    }

    /**
     * CSS 스타일을 Guide UI에서 사용할 수 있도록 제공하는 메서드 (QuizPanel의 Type 2에서 사용)
     */
    public static String getCSSStyles() {
        return CSS_STYLES;
    }
    
    // --- 레거시 메서드 (사용되지 않을 수 있음) ---
    public static List<GuideDTO> getAllGuides() throws SQLException {
        List<GuideDTO> guides = new ArrayList<>();
        // ItemDetail을 조회하여 GuideDTO로 변환합니다.
        for (ItemDetail item : getAllItems()) {
            // TITLE 필드에는 '카테고리 - 세부 품목' 형식으로 표시
            String title = item.categoryName + " - " + item.itemName;
            // GuideDTO가 별도로 필요하다면 사용
            // guides.add(new GuideDTO(title, item.disposalGuide)); 
        }
        return guides;
    }
}