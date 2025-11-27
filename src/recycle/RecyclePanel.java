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


public class RecyclePanel extends JPanel {

    // 필수 필드
    private final String userId;
    private final RecycleLogDAO logDAO;
    private final Map<String, Integer> itemPoints; 

    // 왼쪽 영역 (입력 및 포인트 확인) 컴포넌트
    private JComboBox<String> itemComboBox;
    private JLabel pointLabel; 
    private JButton addButton;

    // 오른쪽 영역 (임시 목록) 컴포넌트
    private JTable currentTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private JButton removeButton;
    
    // 상태 변수
    private int totalPoint = 0; 
    
    // UI 로직에 필요한 상수
    private static final String DEFAULT_SELECTION_TEXT = "--- 품목 선택 ---";
    private static final String[] COLUMN_NAMES = {"순번", "품목", "포인트"};
    
    // 폰트 설정
    private static final Font KOREAN_FONT = new Font("맑은 고딕", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("맑은 고딕", Font.BOLD, 12);

    public RecyclePanel(String userId) throws Exception {
        this.userId = userId;
        
        this.logDAO = new RecycleLogDAO(); 
        this.itemPoints = logDAO.getAllItemPoints(); 
        
        // 1. 메인 레이아웃 (BorderLayout) 설정
        setLayout(new BorderLayout(5, 5));

        // 2. 좌우 분할 (JSplitPane) 설정
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        splitPane.setDividerLocation(210); 
        splitPane.setResizeWeight(0.0); 

        // 3. 좌/우 패널 생성 및 추가
        JPanel inputActionPanel = createInputActionPanel();
        splitPane.setLeftComponent(inputActionPanel);

        JPanel currentListPanel = createCurrentListPanel();
        splitPane.setRightComponent(currentListPanel);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        add(splitPane, BorderLayout.CENTER);
        
        // 4. DB에 저장된 오늘의 기록을 로드하고 합계 행을 업데이트합니다.
        loadTodayRecycleLogs();
    }
    
    /**
     *  DB에 저장된 오늘(당일)의 분리수거 기록을 테이블에 로드합니다.
     */
    public void loadTodayRecycleLogs() {
        tableModel.setRowCount(0); 
        totalPoint = 0;
        
        try {
            List<RecycleLogDAO.LogItem> todayLogs = logDAO.getTodayRecycleLogs(userId); 

            if (todayLogs != null && !todayLogs.isEmpty()) {
                for (int i = 0; i < todayLogs.size(); i++) {
                    RecycleLogDAO.LogItem log = todayLogs.get(i);
                    int point = itemPoints.getOrDefault(log.itemName, 0);
                    
                    tableModel.addRow(new Object[]{
                        i + 1,       
                        log.itemName, 
                        point + "P"   
                    });
                    totalPoint += point;
                }
            }
        } catch (Exception e) {
            System.err.println("오늘의 분리수거 기록 로드 중 DB 오류: " + e.getMessage());
        }
        
        updateTotalRow(); 
    }
    
    private JPanel createInputActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(2, 5, 2, 5), 
            "분리수거 항목 추가"
        ));

        JPanel inputFieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(3, 5, 3, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 품목 목록
        Vector<String> items = new Vector<>(itemPoints.keySet());
        items.insertElementAt(DEFAULT_SELECTION_TEXT, 0);

        // 1. "품목" Label
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; 
        inputFieldsPanel.add(new JLabel("품목", SwingConstants.LEFT), gbc);
        
        // 2. 품목 선택 (ComboBox)
        gbc.gridx = 1; gbc.weightx = 0.7; 
        itemComboBox = new JComboBox<>(items);
        itemComboBox.setFont(KOREAN_FONT);
        itemComboBox.setSelectedIndex(0);
        itemComboBox.addActionListener(e -> updatePointLabel());
        inputFieldsPanel.add(itemComboBox, gbc);

        // 3. "예상 포인트" Label
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        inputFieldsPanel.add(new JLabel("예상 포인트", SwingConstants.LEFT), gbc);
        
        // 4. 포인트 정보 (오른쪽 정렬)
        gbc.gridx = 1; gbc.weightx = 0.7;
        pointLabel = new JLabel("0", SwingConstants.RIGHT); 
        pointLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        pointLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            BorderFactory.createEmptyBorder(1, 5, 1, 5) 
        ));
        pointLabel.setBackground(Color.WHITE);
        pointLabel.setOpaque(true);
        inputFieldsPanel.add(pointLabel, gbc);
        
        // 5. 버튼
        addButton = new JButton("목록에 추가");
        addButton.setFont(BUTTON_FONT); 
        addButton.setEnabled(false);
        addButton.addActionListener(this::handleAddItem);
        
        // 6. 버튼 영역을 별도의 패널로 구성 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); 
        buttonPanel.add(addButton);
        
        // 7. GridBagLayout에 버튼을 추가합니다. 
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.NONE; 
        gbc.insets = new Insets(5, 5, 0, 5); 
        gbc.anchor = GridBagConstraints.EAST; 
        inputFieldsPanel.add(buttonPanel, gbc);
        

        panel.add(inputFieldsPanel); 

        return panel;
    }


    private JPanel createCurrentListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(2, 5, 2, 5), 
            "분리수거 목록"
        ));

        // JTable 설정
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };
        currentTable = new JTable(tableModel);
        currentTable.setFont(KOREAN_FONT);
        currentTable.setRowHeight(18); 
        currentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                

                if ("합계".equals(table.getValueAt(row, 1))) {
                    comp.setFont(comp.getFont().deriveFont(Font.BOLD, 12f)); 
                    comp.setBackground(new Color(240, 248, 255)); 
                    comp.setForeground(Color.BLACK); 
                } else {
                    comp.setFont(table.getFont());
                    comp.setBackground(Color.WHITE);
                }
                
                if (isSelected) {
                    comp.setBackground(currentTable.getSelectionBackground());
                    comp.setForeground(currentTable.getSelectionForeground());
                }
                
                return comp;
            }
        };

        for (int i = 0; i < currentTable.getColumnCount(); i++) {
            currentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // 컬럼 너비 설정
        currentTable.getColumnModel().getColumn(0).setPreferredWidth(10); 
        currentTable.getColumnModel().getColumn(1).setPreferredWidth(20); 
        currentTable.getColumnModel().getColumn(2).setPreferredWidth(10); 
        currentTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN); 


        panel.add(new JScrollPane(currentTable), BorderLayout.CENTER);

        // 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 3)); 
        removeButton = new JButton("선택 삭제");
        removeButton.setFont(KOREAN_FONT);
        removeButton.addActionListener(this::handleRemoveItem);
        saveButton = new JButton("포인트 얻기");
        saveButton.setFont(KOREAN_FONT);
        saveButton.addActionListener(this::handleSaveLogs);

        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    

    private void updatePointLabel() {
        String selectedItem = (String) itemComboBox.getSelectedItem();
        
        if (selectedItem != null && !selectedItem.equals(DEFAULT_SELECTION_TEXT)) {
            int point = itemPoints.getOrDefault(selectedItem, 0);
            pointLabel.setText(String.valueOf(point)); 
            addButton.setEnabled(true);
        } else {
            pointLabel.setText("0");
            addButton.setEnabled(false);
        }
    }
    
    private void handleAddItem(ActionEvent e) {
        String selectedItem = (String) itemComboBox.getSelectedItem();
        
        if (selectedItem == null || selectedItem.equals(DEFAULT_SELECTION_TEXT)) {
            return;
        }

        // 중복 체크 (합계 행 제외)
        int dataRowCount = tableModel.getRowCount();
        if (dataRowCount > 0 && "합계".equals(tableModel.getValueAt(dataRowCount - 1, 1))) {
            dataRowCount -= 1;
        }

        for (int i = 0; i < dataRowCount; i++) { 
            if (selectedItem.equals(tableModel.getValueAt(i, 1))) {
                JOptionPane.showMessageDialog(this,
                        "품목 **" + selectedItem + "**은 이미 목록에 추가되었습니다.",
                        "중복 항목",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int point = itemPoints.getOrDefault(selectedItem, 0);
        
        // 합계 행이 있으면 제거하고 데이터 행 추가
        if (tableModel.getRowCount() > 0 && "합계".equals(tableModel.getValueAt(tableModel.getRowCount() - 1, 1))) {
             tableModel.removeRow(tableModel.getRowCount() - 1);
        }

        int newRowNumber = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{
            newRowNumber,   
            selectedItem,   
            point + "P"     
        });
        
        totalPoint += point;
        updateTotalRow();
        
        itemComboBox.setSelectedIndex(0);
        updatePointLabel(); 
    }

    private void handleRemoveItem(ActionEvent e) {
        int selectedIndex = currentTable.getSelectedRow();
        
        if (selectedIndex == -1 || "합계".equals(tableModel.getValueAt(selectedIndex, 1))) { 
            JOptionPane.showMessageDialog(this, "제거할 항목을 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pointStr = tableModel.getValueAt(selectedIndex, 2).toString().replace("P", "").trim();
        int point = Integer.parseInt(pointStr);
        totalPoint -= point;
        
        // 합계 행을 먼저 제거
        if (tableModel.getRowCount() > 0 && "합계".equals(tableModel.getValueAt(tableModel.getRowCount() - 1, 1))) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }
        
        tableModel.removeRow(selectedIndex);
        
        updateRowNumbers(); 
        updateTotalRow();   
    }
    
    private void updateRowNumbers() {
        int limit = tableModel.getRowCount();
        if (limit > 0 && "합계".equals(tableModel.getValueAt(limit - 1, 1))) {
            limit -= 1;
        }
        
        for (int i = 0; i < limit; i++) {
            tableModel.setValueAt(i + 1, i, 0); 
        }
    }


    private void updateTotalRow() {
        // 이미 합계 행이 있으면 제거 (업데이트를 위해)
        if (tableModel.getRowCount() > 0 && "합계".equals(tableModel.getValueAt(tableModel.getRowCount() - 1, 1))) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }

        // 새로운 합계 행 추가
        tableModel.addRow(new Object[]{
            null,           
            "합계",         
            totalPoint + "P" 
        });
    }


    private void handleSaveLogs(ActionEvent e) {
        if (tableModel.getRowCount() <= 1) { 
            JOptionPane.showMessageDialog(this, "저장할 분리수거 품목이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> itemsToSave = new ArrayList<>();
        
        // 합계 행 제외하고 품목 목록 추출
        for (int i = 0; i < tableModel.getRowCount() - 1; i++) {
            String itemName = (String) tableModel.getValueAt(i, 1);
            itemsToSave.add(itemName);
        }
        
        try {
            int earnedPoints = logDAO.insertRecycleLogsAndEarn(userId, itemsToSave, itemPoints);
            
            String message;
            if (earnedPoints > 0) {
                 message = "총 " + itemsToSave.size() + "건의 기록이 저장되었습니다.\n" +
                           "새로운 항목에 대해 총 **" + earnedPoints + " 포인트**가 적립되었습니다.";
            } else {
                 message = "총 " + itemsToSave.size() + "건의 기록이 저장되었으나, 이미 오늘 적립된 항목이거나 포인트가 0점인 항목입니다.";
            }
            
            JOptionPane.showMessageDialog(this, message, "저장 완료", JOptionPane.INFORMATION_MESSAGE);
            

            loadTodayRecycleLogs();
            
        } catch (Exception ex) {
            System.err.println("로그 저장 중 DB 오류: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "기록 저장 중 DB 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}