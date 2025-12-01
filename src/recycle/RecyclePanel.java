package recycle;

import db.DAO.RecycleLogDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.LinkedHashMap;
import java.sql.SQLException; 

public class RecyclePanel extends JPanel {

    // 필수 필드
    private final String userId;
    private final RecycleLogDAO logDAO;
    private final Map<String, Integer> itemPoints; 

    // 왼쪽 영역 
    private JComboBox<String> itemComboBox;
    private JLabel pointLabel; 
    private JButton addButton;

    // 오른쪽 영역 
    private JTable currentTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private JButton removeButton;
    
    // 상태 변수
    private int totalPoint = 0; 
    
    // 합계 행 배경색
    private static final Color TOTAL_ROW_BACKGROUND = new Color(220, 240, 255); // 연한 푸른색
    
    // UI 로직에 필요한 상수
    private static final String DEFAULT_SELECTION_TEXT = "--- 품목 선택 ---";
    private static final String[] CURRENT_LOG_COLUMN_NAMES = {"순번", "품목", "포인트"};
    
    // 폰트 설정
    private static final Font KOREAN_FONT = new Font("맑은 고딕", Font.PLAIN, 15);
    private static final Font KOREAN_BOLD_FONT = new Font("맑은 고딕", Font.BOLD, 15);


    public RecyclePanel(String userId) {
        this.userId = userId;
        this.itemPoints = initializeItemPoints(); 

        RecycleLogDAO dao = null;
        try {
            dao = new RecycleLogDAO();
        } catch (Exception e) {
            System.err.println("RecycleLogDAO 초기화 오류: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "DB 초기화 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
        this.logDAO = dao;

        if (this.logDAO != null) {
            createUILayout(); 
        } else {
            removeAll();
            setLayout(new GridBagLayout());
            add(new JLabel("DB 연결 오류로 패널을 사용할 수 없습니다.", SwingConstants.CENTER));
        }
    }
    

    private Map<String, Integer> initializeItemPoints() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("종이", 15);
        map.put("비닐", 10);
        map.put("유리병", 25);
        map.put("종이팩", 20);
        map.put("캔ㆍ고철", 40);
        map.put("스티로폼", 10);
        map.put("플라스틱", 10);
        map.put("기타", 5); 
        return map;
    }

    private void createUILayout() {
        setLayout(new BorderLayout(10, 10));
        
        // 1. 왼쪽 패널 
        JPanel leftPanel = createInputPanel();
        leftPanel.setPreferredSize(new Dimension(200, 400)); 

        // 2. 오른쪽 패널 
        JPanel rightPanelContainer = new JPanel(new BorderLayout());
        rightPanelContainer.setPreferredSize(new Dimension(350, 400));
        
        // 2-1. 제목 레이블 추가 
        JLabel titleLabel = new JLabel("분리수거 목록", SwingConstants.CENTER);
        titleLabel.setFont(KOREAN_BOLD_FONT.deriveFont(Font.BOLD, 20)); 
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 2-2. 목록 패널 생성
        JPanel currentLogPanel = createCurrentLogPanel(); 
        currentLogPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        rightPanelContainer.add(titleLabel, BorderLayout.NORTH);
        rightPanelContainer.add(currentLogPanel, BorderLayout.CENTER);
        
        // 3. 전체 레이아웃 배치
        add(leftPanel, BorderLayout.WEST);
        add(rightPanelContainer, BorderLayout.CENTER);
        
        // 초기 합계 행 추가
        calculateTotalPoints(); 
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        
        // 콤보박스 초기화
        itemComboBox = new JComboBox<>(itemPoints.keySet().toArray(new String[0]));
        itemComboBox.insertItemAt(DEFAULT_SELECTION_TEXT, 0);
        itemComboBox.setSelectedIndex(0);
        itemComboBox.setFont(KOREAN_FONT);
        itemComboBox.setMaximumSize(new Dimension(200, 30)); 
        
        itemComboBox.setRenderer(new CenterAlignedRenderer());

        pointLabel = new JLabel("선택 포인트: 0 P", SwingConstants.CENTER);
        pointLabel.setFont(KOREAN_FONT);
        pointLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 

        addButton = new JButton("목록에 추가");
        addButton.setFont(KOREAN_BOLD_FONT);
        addButton.addActionListener(e -> addRecycleItemToTable((String)itemComboBox.getSelectedItem()));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(200, 40));


        panel.add(Box.createVerticalGlue()); 
        
        panel.add(itemComboBox);
 
        panel.add(Box.createVerticalStrut(25)); 
        panel.add(pointLabel);
    
        panel.add(Box.createVerticalStrut(25)); 
        panel.add(addButton);
        
        panel.add(Box.createVerticalGlue()); 
        
        itemComboBox.addActionListener(e -> {
            String selectedItem = (String) itemComboBox.getSelectedItem();
            int point = (selectedItem == null || selectedItem.equals(DEFAULT_SELECTION_TEXT)) 
                        ? 0 
                        : itemPoints.getOrDefault(selectedItem, 0);
            pointLabel.setText("선택 포인트: " + point + " P");
        });
        
        return panel;
    }
    
    private JPanel createCurrentLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        tableModel = new DefaultTableModel(CURRENT_LOG_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        currentTable = new JTable(tableModel);
        currentTable.setFont(KOREAN_FONT);
        currentTable.setRowHeight(25);
        currentTable.getTableHeader().setFont(KOREAN_BOLD_FONT);
        
        currentTable.setDefaultRenderer(Object.class, new TotalRowRenderer());
        
        JScrollPane scrollPane = new JScrollPane(currentTable);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeButton = new JButton("선택 항목 제거");
        saveButton = new JButton("포인트 얻기 (저장)");
        saveButton.addActionListener(this::handleSaveLogs); 

        removeButton.addActionListener(e -> {
            int selectedRow = currentTable.getSelectedRow();
         
            if (selectedRow != -1 && selectedRow < tableModel.getRowCount() - 1) {
                tableModel.removeRow(selectedRow);
         
                renumberSequence();
                calculateTotalPoints();
            } else if (selectedRow == tableModel.getRowCount() - 1) {
                 JOptionPane.showMessageDialog(this, "합계 행은 제거할 수 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "제거할 항목을 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(removeButton); 
        buttonPanel.add(saveButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void renumberSequence() {
    
        int dataRowCount = tableModel.getRowCount();
        if (dataRowCount > 0 && tableModel.getValueAt(dataRowCount - 1, 1).equals("합계")) {
            dataRowCount--;
        }

        for (int i = 0; i < dataRowCount; i++) {
       
            tableModel.setValueAt(i + 1, i, 0); 
        }
    }

    // 목록에 품목 추가 
    private void addRecycleItemToTable(String itemName) {
        if (itemName == null || itemName.equals(DEFAULT_SELECTION_TEXT)) {
            return;
        }

        int point = itemPoints.getOrDefault(itemName, 0);
        
  
        if (tableModel.getRowCount() > 0) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }

  
        Vector<Object> row = new Vector<>();

        row.add(tableModel.getRowCount() + 1); 
        row.add(itemName);
    
        row.add(point + "P"); 
        tableModel.addRow(row);
        
  
        renumberSequence();
        calculateTotalPoints(); 
    }

    // 테이블의 합계 업데이트 
    private void calculateTotalPoints() {

        if (tableModel.getRowCount() > 0 && tableModel.getValueAt(tableModel.getRowCount() - 1, 1).equals("합계")) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }

        totalPoint = 0;
 
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object pointValue = tableModel.getValueAt(i, 2);
            
            if (pointValue instanceof String) {
                try {
            
                    totalPoint += Integer.parseInt(((String) pointValue).replace("P", "").trim());
                } catch (NumberFormatException ignored) {
             
                }
            }
        }
        
        tableModel.addRow(new Vector<>(List.of(
            "", "합계", totalPoint + "P" 
        )));
    }
    
    private void clearTable() {
    
        int rowCount = tableModel.getRowCount();
        if (rowCount > 0) {
         
            tableModel.removeRow(rowCount - 1);
       
            for (int i = rowCount - 2; i >= 0; i--) {
                tableModel.removeRow(i);
            }
        }
        totalPoint = 0;
        calculateTotalPoints(); 
    }

    private void loadTodayRecycleLogs() {
    
    }

  
    private void handleSaveLogs(ActionEvent e) {
        if (logDAO == null) {
             JOptionPane.showMessageDialog(this, "DB 연결 오류로 기능을 사용할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
             return;
        }

        if (tableModel.getRowCount() <= 1) { 
            JOptionPane.showMessageDialog(this, "저장할 분리수거 품목이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> itemsToSave = new ArrayList<>();
        
        for (int i = 0; i < tableModel.getRowCount() - 1; i++) {
            String itemName = (String) tableModel.getValueAt(i, 1);
            itemsToSave.add(itemName);
        }
        
        try {
            int earnedPoints = logDAO.insertRecycleLogsAndEarn(userId, itemsToSave, itemPoints);
            
            String message;
            if (earnedPoints > 0) {
                 message = String.format("총 %d건의 기록이 저장되었습니다.\n새로운 항목에 대해 총 **%d 포인트**가 적립되었습니다.", itemsToSave.size(), earnedPoints);
            } else {
                 message = String.format("총 %d건의 기록이 저장되었으나, 이미 오늘 적립된 품목이거나 포인트가 0점인 항목입니다. (획득 포인트: 0 P)", itemsToSave.size());
            }
            
            JOptionPane.showMessageDialog(this, message, "저장 완료", JOptionPane.INFORMATION_MESSAGE);
            
            // ⭐ 테이블 초기화 로직 제거/주석 처리
            // clearTable();
            
        } catch (SQLException ex) { 
            System.err.println("분리수거 로그 저장 및 포인트 적립 DB 오류: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "DB 오류로 저장 및 포인트 적립에 실패했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { // 일반 Exception 처리
            System.err.println("시스템 오류: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "시스템 오류로 저장 및 포인트 적립에 실패했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class TotalRowRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
            
   
            setHorizontalAlignment(SwingConstants.CENTER);
            
         
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

       
            if (row == table.getRowCount() - 1) {
            
                c.setBackground(TOTAL_ROW_BACKGROUND);
                
                c.setFont(KOREAN_BOLD_FONT);
                
                if (!isSelected) {
                    c.setForeground(table.getForeground()); 
                }

            } else {
               
                c.setBackground(table.getBackground());
                c.setFont(KOREAN_FONT);
                
       
                if (!isSelected) {
                    c.setForeground(table.getForeground()); 
                }
            }

            return c;
        }
    }
	private class CenterAlignedRenderer extends DefaultListCellRenderer {
	        
	        @Override
	        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
	                                                      boolean isSelected, boolean cellHasFocus) {
	            // 기본 렌더링 컴포넌트(JLabel)를 가져옵니다.
	            JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	            
	            // 텍스트를 가운데 정렬로 설정합니다.
	            renderer.setHorizontalAlignment(CENTER);
	            
	            return renderer;
	        }
	}
}