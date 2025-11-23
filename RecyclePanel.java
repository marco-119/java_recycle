package recycle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.sql.*;

import db.recycleDB;
import db.RecycleLogDAO; 

public class RecyclePanel extends JPanel {

    private JComboBox<String> itemComboBox;
    private JTextField pointField;
    private JTable table;
    private DefaultTableModel model;

    private JButton addButton;
    private JButton deleteButton;
    private JButton getPointButton;

    private int totalPoint = 0;
    private int rowCount = 0;

    private final Map<String, Integer> itemPoints;
    private String userId;
    
    private RecycleLogDAO logDAO;

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

        logDAO = new RecycleLogDAO();

        // DB 초기화 및 마스터 데이터 확인 (시간이 오래 걸릴 수 있으므로, 메인 클래스에서 한 번만 호출하는 것이 좋음)
        try {
            // recycle_items 테이블은 이 클래스에서 생성
            initializeDatabase(); 
            // logs와 user_points 테이블은 DAO에서 생성
            RecycleLogDAO.initializeDatabase(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 초기화 오류: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        ensureMasterItems();  

        // UI 컴포넌트 초기화 및 구성 (이전 코드와 동일)
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

        // 콤보박스 선택 시 예상 포인트 필드 업데이트 리스너 (이전 코드와 동일)
        itemComboBox.addActionListener(e -> {
            String item = (String) itemComboBox.getSelectedItem();
            if (item == null || item.equals("품목을 선택하세요")) {
                pointField.setText("0");
            } else {
                int point = itemPoints.getOrDefault(item, 0);
                pointField.setText(String.valueOf(point));
            }
        });

        // 테이블에 항목 추가 리스너 (이전 코드와 동일)
        addButton.addActionListener(e -> {
            String item = (String) itemComboBox.getSelectedItem();
            
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

        // 테이블에서 항목 제거 및 DB 갱신 리스너 (이전 코드와 동일)
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            
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

            int point = Integer.parseInt(model.getValueAt(row, 2).toString().replace("P", "").trim());
            totalPoint -= point;
            model.removeRow(row);

            updateRowNumbers();

            if (model.getRowCount() > 0 && "합계".equals(model.getValueAt(model.getRowCount() - 1, 1))) {
                model.removeRow(model.getRowCount() - 1);
            }
            model.addRow(new Object[]{"", "합계", totalPoint + "P"});

            rewriteDbData(); 

            JOptionPane.showMessageDialog(this, "선택한 항목이 삭제되었으며 기록이 갱신되었습니다.", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
        });

        // 포인트 적립 및 DB 저장 리스너 (이전 코드와 동일)
        getPointButton.addActionListener(e -> {

            if (totalPoint == 0) {
                JOptionPane.showMessageDialog(this, "적립할 포인트가 없습니다.", "포인트 없음", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Set<String> savedItems = loadSavedItemsSet(); 
            
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

            // 1. 기록 덮어쓰기 (DAO 사용)
            rewriteDbData();
            
            // 2. 사용자 누적 포인트 갱신 (DAO 사용)
            try {
                logDAO.addPointsToUser(userId, newPointTotal);
            } catch (SQLException ex) {
                System.err.println("포인트 적립 DB 오류: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "포인트 적립 DB 오류가 발생했습니다.", "DB Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String message;
            if (savedItems.isEmpty()) {
                message = "축하합니다! 총 " + totalPoint + "P를 획득했습니다!\n기록이 자동 저장되었습니다.";
            } else {
                message = "추가로 " + newPointTotal + "P를 획득했습니다!\n기록이 갱신되었습니다.";
            }
            JOptionPane.showMessageDialog(this, message, "포인트 적립 완료", JOptionPane.INFORMATION_MESSAGE);
        });

        // 💡 [수정] UI 블로킹 방지를 위해 SwingWorker를 사용하여 DB 로딩을 백그라운드에서 처리
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadSavedItemsToTable();
                return null;
            }
            @Override
            protected void done() {
                // 백그라운드 작업 완료 후, 필요하다면 여기에 UI 업데이트 로직 추가 가능
            }
        };
        worker.execute();
    }

    private void initializeDatabase() {
        String createItems =
                "CREATE TABLE IF NOT EXISTS recycle_items (" +
                        "item_name VARCHAR(50) PRIMARY KEY," +
                        "point INT NOT NULL" +
                        ");";
        
        try (Connection conn = recycleDB.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createItems); // 마스터 품목 테이블 생성
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 초기화 오류: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ensureMasterItems() {
        try (Connection conn = recycleDB.connect()) {
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

    private void loadComboBoxFromMaster() {
        itemComboBox.removeAllItems();
        itemComboBox.addItem("품목을 선택하세요");
        
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

    private void rewriteDbData() {
        List<String> currentItems = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String item = model.getValueAt(i, 1).toString();
            if (!"합계".equals(item)) {
                currentItems.add(item);
            }
        }

        try {
            logDAO.rewriteDbData(userId, currentItems, itemPoints);
        } catch (SQLException ex) {
            System.err.println("DB 저장 오류 (rewriteDbData): " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "기록 저장 중 DB 오류가 발생했습니다.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Set<String> loadSavedItemsSet() {
        // ⭐ [수정] 기술적인 오류 메시지를 콘솔로 보내고, 사용자에게는 일반적인 메시지 표시
        try {
            return logDAO.loadSavedItemsSet(userId);
        } catch (SQLException e) {
            System.err.println("DB 로드 오류 (loadSavedItemsSet): " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "이전에 저장된 기록을 불러오는데 실패했습니다. (DB 오류)", 
                "DB 로드 오류", 
                JOptionPane.ERROR_MESSAGE);
            return new HashSet<>();
        }
    }

    private void loadSavedItemsToTable() {
        Set<String> alreadySaved = loadSavedItemsSet(); 
        // 💡 이 메서드 자체가 SwingWorker의 doInBackground()에서 호출되므로, 
        // model.setRowCount(0) 등은 EDT에서 실행되도록 주의해야 하지만,
        // 이 로직은 SwingWorker의 done()에서 처리하는 것이 이상적입니다.
        // 현재는 DB 작업이 UI를 업데이트하므로, loadSavedItemsSet()을 doInBackground()에서 호출하고, 
        // done()에서 테이블 업데이트를 수행하는 것이 가장 안전합니다.

        // 현재는 loadSavedItemsToTable()에서 UI를 직접 업데이트하고 있으므로, 
        // SwingWorker의 doInBackground()에서 호출하더라도 UI 블로킹이 발생하지 않도록
        // SwingWorker의 done() 메서드에서 UI를 업데이트하는 것을 추천합니다. 

        if (!alreadySaved.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
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
            });
        }
    }
}