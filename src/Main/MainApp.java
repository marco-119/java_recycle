package Main;

import javax.swing.*;
import java.awt.*;
// import java.sql.SQLException; // Exception으로 대체되었으므로 제거됨

// DB 초기화 및 연결 유틸리티 임포트
//import db.RecycleDB; 
import db.DAO.RecycleLogDAO; 
import db.DAO.GuideDAO; 
import db.DAO.UserDAO; 
import db.DTO.UserDTO; // UserDTO 임포트

// UI 패널 임포트
import recycle.LoginPanel;
import recycle.RecyclePanel;
import recycle.Guide;
import recycle.QuizPanel;
import recycle.RankingWindow;


public class MainApp extends JFrame {

    private final String userId; 

    //메인 애플리케이션 창 생성자
   
    public MainApp(UserDTO user) { 
        this.userId = user.getUserId(); 
        
        // 닉네임과 ID를 모두 포함하여 창 제목 설정
        setTitle("분리수거 포인트 서비스 - [사용자: " + user.getNickname() + " (" + userId + ")]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600); 
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        
        // 탭 패널 초기화 및 추가
        JTabbedPane tabbedPane = createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // 창을 화면에 표시
        setVisible(true); 
    }
    
  
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        try {
            // 1. 분리수거 및 기록 패널
            RecyclePanel recyclePanel = new RecyclePanel(userId); 
            tabbedPane.addTab("분리수거 및 기록", new JScrollPane(recyclePanel));
            
            // 2. 가이드 패널
            Guide guidePanel = new Guide();
            tabbedPane.addTab("분리수거 가이드", guidePanel);
            
            // 3. 퀴즈 패널
            QuizPanel quizPanel = new QuizPanel(userId); 
            tabbedPane.addTab("분리수거 퀴즈", quizPanel);
            
            // 4. 랭킹 패널
            RankingWindow rankingPanel = new RankingWindow(userId); 
            tabbedPane.addTab("포인트 랭킹", rankingPanel);

        } catch (Exception e) {
             System.err.println("메인 프레임 패널 초기화 오류: " + e.getMessage());
             JOptionPane.showMessageDialog(this, "애플리케이션 초기화 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
             System.exit(1); 
        }
        
        return tabbedPane;
    }


    public static void main(String[] args) {
        // Look and Feel 설정
    	try {
    	     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    	} catch (Exception e) {
    	     System.err.println("Look and Feel 설정 실패: " + e.getMessage());
    	}
        
        // 1. DB 초기화 
        try {
        	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
     
            UserDAO.initializeDatabase();     
            RecycleLogDAO.initializeDatabase(); 
            GuideDAO.initializeDatabase();     
            
            System.out.println("DB 테이블 초기화 완료.");
            
        } catch (Exception e) { 
            System.err.println("심각한 DB 초기화 오류 발생: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "프로그램 시작 전 DB 초기화에 실패했습니다. 프로그램을 종료합니다.\n" + e.getMessage(), 
                "심각한 오류", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // 2. 로그인 창을 띄워 애플리케이션 시작
        SwingUtilities.invokeLater(() -> {
            new LoginPanel(); 
        });
    }
}