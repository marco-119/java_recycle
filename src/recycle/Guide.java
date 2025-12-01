package recycle;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap; 

import db.DAO.GuideDAO; 
import db.DAO.GuideDAO.ItemDetail; 

public class Guide extends JPanel {
    

    private Map<String, Integer> categoryMap; 
    
 
    private JEditorPane editorPane;
    private JScrollPane scrollPane;

    public Guide() {
     
        GuideDAO.initializeDatabase(); 
        
    
        try {
   
            this.categoryMap = GuideDAO.getAllCategoryNamesAndIds();
        } catch (Exception e) {
            System.err.println("카테고리 데이터 로드 중 오류 발생: " + e.getMessage());
            displayErrorUI("가이드 정보를 불러올 수 없습니다. DB 오류: " + e.getMessage());
            return;
        }
        
 
        setLayout(new BorderLayout(5, 5)); 
        
     
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(150); 
        splitPane.setResizeWeight(0.0); 

   
        JList<String> categoryList = new JList<>(categoryMap.keySet().toArray(new String[0]));
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        categoryList.setFixedCellHeight(25); 
        
  
        if (!categoryMap.isEmpty()) {
            categoryList.setSelectedIndex(0); 
        }

  
        JPanel detailPanel = createDetailViewPanel();
        
       
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { 
                String selectedCategory = categoryList.getSelectedValue();
                if (selectedCategory != null) {
                    int categoryId = categoryMap.get(selectedCategory);
                    updateDetailContent(categoryId, selectedCategory);
                }
            }
        });

        splitPane.setLeftComponent(new JScrollPane(categoryList));
        splitPane.setRightComponent(detailPanel);
        
        add(splitPane, BorderLayout.CENTER);
    
        if (!categoryMap.isEmpty()) {
            Map.Entry<String, Integer> firstEntry = categoryMap.entrySet().iterator().next();
            updateDetailContent(firstEntry.getValue(), firstEntry.getKey());
        }
    }
    
   
    private JPanel createDetailViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE); 
        
        scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); 

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

 
    private void updateDetailContent(int categoryId, String categoryName) {
        try {
    
            List<ItemDetail> details = GuideDAO.getItemsByCategory(categoryId);
            
            StringBuilder contentBuilder = new StringBuilder();
            
      
            String cssStyles = "<style>"
                    + "h1 { color: #008000; margin-top: 0px; margin-bottom: 20px; font-size: 16pt; border-bottom: 2px solid #008000; padding-bottom: 5px; }"
                    + "h2 { font-size: 14pt; color: #333333; margin-top: 15px; margin-bottom: 5px; }"
                    + "h3 { font-size: 12pt; color: #555555; margin-top: 10px; margin-bottom: 5px; }"
                    + "ul { margin-top: 5px; margin-left: 20px; }"
                    + "li { margin-bottom: 5px; }"
                    + ".note { padding-left: 15px; list-style-type: none; font-style: italic; color: #cc0000; font-weight: bold; }"
                    + "hr { border: 0; height: 1px; background-color: #eee; margin: 20px 0; }"
                    + "body { font-family: 'Malgun Gothic', '맑은 고딕'; font-size: 10pt; line-height: 1.6; padding: 10px; }" 
                    + "</style>";
            
            contentBuilder.append(String.format("<h1>%s 분리수거 가이드</h1>", categoryName));
            
            for (ItemDetail item : details) {
        
                contentBuilder.append(String.format("<h2>%s</h2>", item.itemName)); 
      
                contentBuilder.append(item.disposalGuide); 
                contentBuilder.append("<hr>"); 
            }
            
       
            String styledHtml = String.format(
                "<html><head>%s</head><body>%s</body></html>",
                cssStyles,
                contentBuilder.toString()
            );
            
            editorPane.setText(styledHtml);
     
            editorPane.setCaretPosition(0); 

        } catch (Exception e) {
            editorPane.setText(String.format("<html><body><h1>오류 발생</h1><p style='color:red;'>%s 가이드 정보를 로드하는 중 오류가 발생했습니다: %s</p></body></html>", categoryName, e.getMessage()));
            System.err.println(String.format("%s 가이드 로드 오류: %s", categoryName, e.getMessage()));
        }
    }


    
    private void displayErrorUI(String message) {
        removeAll();
        setLayout(new GridBagLayout()); 
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(errorLabel);
        revalidate();
        repaint();
    }
}