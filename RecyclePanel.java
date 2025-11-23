package recycle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.sql.*;

// DB 연결을 위해 db 패키지의 recycleDB 클래스를 import 
import db.recycleDB;

public class RecyclePanel extends JPanel {

    // UI 컴포넌트 변수
    private JComboBox<String> itemComboBox; 
    private JTextField pointField;        
    private JTable table;                   
    private DefaultTableModel model;       

    //버튼 필드로 선언
    private JButton addButton;
    private JButton deleteButton;
    private JButton getPointButton;

    // 데이터 및 상태 변수
    private int totalPoint = 0; 
    private int rowCount = 0;   

    // 품목별 기본 포인트 정보
    private final Map<String, Integer> itemPoints; 
    private String userId; 

    public RecyclePanel() {

        setLayout(new BorderLayout());

        // 사용자 로그인 처리 및 ID 설정
        userId = JOptionPane.showInputDialog(null, "아이디를 입력하세요:", "로그인", JOptionPane.QUESTION_MESSAGE);
        if (userId == null || userId.trim().isEmpty()) {
            userId = "guest";
        }

        // 품목별 포인트 초기화
        itemPoints = new LinkedHashMap<>();
        itemPoints.put("비닐", 10);
        itemPoints.put("종이", 5);
        itemPoints.put("유리병", 30);
        itemPoints.put("종이팩", 25);
        itemPoints.put("캔/고철", 40);
        itemPoints.put("플라스틱", 15);
        itemPoints.put("기타", 5);

        //  DB 초기화 및 마스터 데이터 확인
        initializeDatabase(); 
        ensureMasterItems();  

        // UI 컴포넌트 초기화 및 구성       
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("품목"), gbc);
        gbc.gridx = 1;
        itemComboBox = new JComboBox<>();
        itemComboBox.setPreferredSize(new Dimension(130, 25));
        inputPanel.add(itemComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("포인트"), gbc);
        gbc.gridx = 1;
        pointField = new JTextField(10);
        pointField.setEditable(false);
        pointField.setText("0");
        pointField.setHorizontalAlignment(JTextField.RIGHT);
        inputPanel.add(pointField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addButton = new JButton("추가");
        inputPanel.add(addButton, gbc);
        add(inputPanel, BorderLayout.WEST);

        String[] columns = {"번호", "품목", "포인트"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(table.getFont());
                int lastRow = table.getRowCount() - 1;
                if (row == lastRow && table.getRowCount() > 0) {
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    lbl.setBackground(new Color(240, 240, 250));
                    lbl.setOpaque(true);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setOpaque(true);
                }
                return lbl;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton = new JButton("선택 삭제");
        getPointButton = new JButton("포인트 얻기");
        
        bottomPanel.add(deleteButton);
        bottomPanel.add(getPointButton);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // 초기 데이터 로드 및 리스너 설정
        loadComboBoxFromMaster(); 

        // 콤보박스 선택 시 예상 포인트 필드 업데이트 리스너
        itemComboBox.addActionListener(e -> {
            String item = (String) itemComboBox.getSelectedItem();
            if (item == null || item.equals("품목을 선택하세요")) {
                pointField.setText("0");
            } else {
                int point = itemPoints.getOrDefault(item, 0);
                pointField.setText(String.valueOf(point));
            }
        });

        //  테이블에 항목 추가
        addButton.addActionListener(e -> {
            String item = (String) itemComboBox.getSelectedItem();
            
            // 유효성 검사 및 중복 항목 체크 로직 복원
            if (item == null || item.equals("품목을 선택하세요")) {
                JOptionPane.showMessageDialog(this, "품목을 선택하세요!", "오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (int i = 0; i < model.getRowCount(); i++) {
                if ("합계".equals(model.getValueAt(i, 1))) continue;
                if (item.equals(model.getValueAt(i, 1))) {
                    JOptionPane.showMessageDialog(this,
                            "품목 " + item + "은 이미 추가되었습니다.",
                            "중복 항목",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            int point = itemPoints.getOrDefault(item, 0);

            // 합계 행이 있으면 제거하고 새 항목 추가 후 합계 갱신
            int lastRowIndex = model.getRowCount() - 1;
            if (lastRowIndex >= 0 && "합계".equals(model.getValueAt(lastRowIndex, 1))) {
                model.removeRow(lastRowIndex);
            }
            
            rowCount = model.getRowCount() + 1;
            model.addRow(new Object[]{rowCount, item, point + "P"});
            
            totalPoint += point;
            model.addRow(new Object[]{"", "합계", totalPoint + "P"});
            pointField.setText(String.valueOf(point));
        });

        // 테이블에서 항목 제거 및 DB 갱신 
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            
            // 유효성 검사 및 합계 행 체크 로직 복원
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 행을 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String itemName = model.getValueAt(row, 1).toString();
            if ("합계".equals(itemName)) {
                JOptionPane.showMessageDialog(this, "합계 행은 삭제할 수 없습니다.", "오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "선택한 항목을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // 포인트 차감 및 테이블 행 제거
            int point = Integer.parseInt(model.getValueAt(row, 2).toString().replace("P", "").trim());
            totalPoint -= point;
            model.removeRow(row);

            updateRowNumbers(); // 테이블 번호 재정렬

            // 합계 행 갱신
            if (model.getRowCount() > 0 && "합계".equals(model.getValueAt(model.getRowCount() - 1, 1))) {
                model.removeRow(model.getRowCount() - 1);
            }
            model.addRow(new Object[]{"", "합계", totalPoint + "P"});

            rewriteDbData(); 

            JOptionPane.showMessageDialog(this, "선택한 항목이 삭제되었으며 기록이 갱신되었습니다.", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
        });

        // 포인트 적립 및 DB 저장 
        getPointButton.addActionListener(e -> {

            if (totalPoint == 0) {
                JOptionPane.showMessageDialog(this, "적립할 포인트가 없습니다.", "포인트 없음", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // DB에 이미 저장된 품목 리스트를 불러와 중복 여부 체크
            Set<String> savedItems = loadSavedItemsSet(); 
            
            // 현재 테이블 항목 추출 및 새 항목 필터링 로직 복원
            List<String> currentTableItems = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                String item = model.getValueAt(i, 1).toString();
                if (!"합계".equals(item)) {
                    currentTableItems.add(item);
                }
            }

            List<String> newItems = new ArrayList<>();
            for (String item : currentTableItems) {
                if (!savedItems.contains(item)) {
                    newItems.add(item);
                }
            }

            if (newItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "추가로 적립할 새 품목이 없습니다.", "적립 불가", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int newPointTotal = newItems.stream()
                    .mapToInt(item -> itemPoints.getOrDefault(item, 0))
                    .sum();

            rewriteDbData();

            // 적립 완료 메시지 출력 로직 복원
            String message;
            if (savedItems.isEmpty()) {
                message = "축하합니다! 총 " + totalPoint + "P를 획득했습니다!\n기록이 자동 저장되었습니다.";
            } else {
                message = "추가로 " + newPointTotal + "P를 획득했습니다!\n기록이 갱신되었습니다.";
            }
            JOptionPane.showMessageDialog(this, message, "포인트 적립 완료", JOptionPane.INFORMATION_MESSAGE);
        });

        loadSavedItemsToTable();
    }

    //  DB에  테이블을 생성합니다.
    private void initializeDatabase() {
        String createItems =
                "CREATE TABLE IF NOT EXISTS recycle_items (" +
                        "item_name VARCHAR(50) PRIMARY KEY," +
                        "point INT NOT NULL" +
                        ");";

        String createLogs =
                "CREATE TABLE IF NOT EXISTS recycle_logs (" +
                        "log_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "item_name VARCHAR(50) NOT NULL," +
                        "point INT NOT NULL," +
                        "timestamp DATETIME NOT NULL" +
                        ");";

        //  DB 연결 시 recycleDB.connect()를 사용합니다.
        try (Connection conn = recycleDB.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createItems); // 마스터 품목 테이블 생성
            stmt.execute(createLogs);  // 사용자 기록 테이블 생성
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 초기화 오류: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 마스터 품목 테이블(`recycle_items`)에 초기 데이터(`itemPoints` 맵)가 없으면 삽입합니다.
    private void ensureMasterItems() {
        //DB 연결 시 recycleDB.connect()를 사용합니다.
        try (Connection conn = recycleDB.connect()) {
            // DB에 데이터가 없으면 초기 데이터 삽입
            String countSql = "SELECT COUNT(*) AS cnt FROM recycle_items";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(countSql)) {
                int cnt = rs.next() ? rs.getInt("cnt") : 0;
                if (cnt == 0) {
                    String insertSql = "INSERT INTO recycle_items(item_name, point) VALUES(?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        for (Map.Entry<String, Integer> e : itemPoints.entrySet()) {
                            pstmt.setString(1, e.getKey());
                            pstmt.setInt(2, e.getValue());
                            pstmt.addBatch(); 
                        }
                        pstmt.executeBatch();
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "마스터 테이블 초기화 오류: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 마스터 품목 테이블(`recycle_items`)에서 품목 이름을 로드하여 콤보박스에 채웁니다.
    private void loadComboBoxFromMaster() {
        itemComboBox.removeAllItems();
        itemComboBox.addItem("품목을 선택하세요");
        
        //DB 연결 시 recycleDB.connect()를 사용합니다.
        try (Connection conn = recycleDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT item_name FROM recycle_items ORDER BY item_name")) { 
            while (rs.next()) {
                itemComboBox.addItem(rs.getString("item_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "콤보박스 로드 오류: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            // DB 로드 실패 시, 하드코딩된 맵에서 로드하는 폴백 로직
            for (String key : itemPoints.keySet()) {
                itemComboBox.addItem(key);
            }
        }
    }

    // 테이블에서 행이 삭제되거나 추가될 때 '번호' 열의 순서를 1부터 다시 매깁니다.
    private void updateRowNumbers() {
        int currentCount = 1;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (!"합계".equals(model.getValueAt(i, 1))) { 
                model.setValueAt(currentCount, i, 0); 
                currentCount++;
            }
        }
        rowCount = currentCount - 1;
    }

    //현재 테이블에 표시된 모든 분리수거 항목을 DB의 `recycle_logs` 테이블에 덮어씁니다. 
    private void rewriteDbData() {
        String insertSql =
                "INSERT INTO recycle_logs(item_name, point, timestamp) VALUES(?, ?, ?)";

        List<String> currentItems = new ArrayList<>();
        // 테이블에서 합계 행을 제외한 모든 품목 추출
        for (int i = 0; i < model.getRowCount(); i++) {
            String item = model.getValueAt(i, 1).toString();
            if (!"합계".equals(item)) {
                currentItems.add(item);
            }
        }

        // DB 연결 시 recycleDB.connect()를 사용합니다.
        try (Connection conn = recycleDB.connect();
             Statement deleteStmt = conn.createStatement();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            deleteStmt.executeUpdate("DELETE FROM recycle_logs"); 
            conn.setAutoCommit(false); 

            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            for (String item : currentItems) {
                int point = itemPoints.getOrDefault(item, 0);
                insertStmt.setString(1, item);
                insertStmt.setInt(2, point);
                insertStmt.setString(3, timestamp);
                insertStmt.addBatch(); 
            }

            insertStmt.executeBatch(); 
            conn.commit(); 
            conn.setAutoCommit(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB 저장 오류: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // DB의 `recycle_logs` 테이블에서 저장된 모든 품목 이름을 Set으로 반환합니다.
    private Set<String> loadSavedItemsSet() {
        Set<String> items = new HashSet<>();
        String sql = "SELECT DISTINCT item_name FROM recycle_logs";
        
        // DB 연결 시 recycleDB.connect()를 사용합니다.
        try (Connection conn = recycleDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(rs.getString("item_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 로드 오류: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return items;
    }

    //  DB에 저장된 품목 기록을 불러와 테이블에 초기 상태로 설정합니다.
    private void loadSavedItemsToTable() {
        Set<String> alreadySaved = loadSavedItemsSet(); 
        if (!alreadySaved.isEmpty()) {
            model.setRowCount(0); 
            totalPoint = 0;
            rowCount = 0;

            int currentCount = 1;
            for (String item : alreadySaved) {
                if ("합계".equals(item)) continue;
                int point = itemPoints.getOrDefault(item, 0);
                model.addRow(new Object[]{currentCount, item, point + "P"}); 
                totalPoint += point;
                currentCount++;
            }
            rowCount = currentCount - 1;
            model.addRow(new Object[]{"", "합계", totalPoint + "P"}); 
        }
    }
}