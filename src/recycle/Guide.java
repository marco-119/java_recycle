package recycle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import db.DAO.GuideDAO; // DB 접근을 위한 DAO 임포트 (패키지 구조 변경)
import db.DTO.GuideDTO; // 가이드 데이터를 담는 DTO 임포트 (패키지 구조 변경)


public class Guide extends JPanel {

    public Guide() {
        // DB 테이블 생성 및 초기 데이터 삽입
        GuideDAO.initializeDatabase(); 
        
        // DB에서 모든 분리수거 가이드 데이터 로드
        List<GuideDTO> guides = null;
        try {
            guides = GuideDAO.getAllGuides();
        } catch (Exception e) {
            System.err.println("가이드 데이터 로드 중 오류 발생: " + e.getMessage());
            // UI에 오류 메시지를 표시합니다.
            displayErrorUI("가이드 정보를 불러올 수 없습니다. DB 오류: " + e.getMessage());
            return;
        }
        
        // UI 구성
        setLayout(new BorderLayout()); 

        // 탭 패널 생성
        JTabbedPane jtpLeft = new JTabbedPane();
        jtpLeft.setTabPlacement(JTabbedPane.LEFT); 
        
        // DTO 리스트를 순회하며 탭에 추가
        for (GuideDTO guide : guides) {
            JPanel tabPanel = createGuidePanel(guide.getContent()); 
            jtpLeft.addTab(guide.getCategory(), tabPanel);
        }


        add(jtpLeft, BorderLayout.CENTER); 
    }
    
 
    private JPanel createGuidePanel(String htmlContent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        
        String cssStyles = "<style>"
    
                + "h2{ margin-top: 10px; margin-bottom: 15px; }"
                + "h3 { margin-top: 10px; margin-bottom: 5px; }"
                + "ul { margin-top: 5px; margin-left: 20px; }"
                + "li { margin-bottom: 5px; }"
                + ".note { padding-left: 15px; list-style-type: none; }"
  
                + "body { font-family: 'Malgun Gothic', '맑은 고딕'; font-size: 10pt; line-height: 1.6; padding: 10px; }" 
                + "</style>";

        // HTML 내용 설정
        String styledHtml = String.format(
            "<html><head>%s</head><body>%s</body></html>",
            cssStyles,
            htmlContent
        );
        
        editorPane.setText(styledHtml);
        
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE); 
        
        // JEditorPane을 JScrollPane에 넣어 스크롤 가능하게 만듭니다.
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); 

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
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