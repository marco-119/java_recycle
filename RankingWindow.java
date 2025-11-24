// RankingWindow.java (main 메소드 포함 최종본)
package recycle; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import recycle.RecyclePanel; 
import recycle.QuizPanel;    
import recycle.DatabaseManager; 

public class RankingWindow extends JFrame {

    // --- 멤버 변수 ---
    private RankingManager rankingManager;
    private JPanel rankListPanel;
    private JLabel totalPointsLabel;
    // ---------------------------------

    public RankingWindow() {
        
        DatabaseManager.initializeDatabase(); 
        this.rankingManager = new RankingManager();

        setTitle("분리수거 안내 서비스");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();

        RecyclePanel recyclePanel = new RecyclePanel(this);
        JPanel panel2 = new JPanel();
        panel2.add(new JLabel("가이드 탭 내용"));
        QuizPanel quizPanel = new QuizPanel(this);
        JPanel rankingPanel = createRankingPanel();

        tabbedPane.addTab("분리수거", recyclePanel);
        tabbedPane.addTab("가이드", panel2);
        tabbedPane.addTab("퀴즈", quizPanel);
        tabbedPane.addTab("랭킹", rankingPanel);
        
        tabbedPane.setSelectedComponent(rankingPanel);

        add(tabbedPane);
        setVisible(true);
    }


    // 랭킹 화면을 구성하는 메소드
    private JPanel createRankingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("TOP 5");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        this.totalPointsLabel = new JLabel("총 포인트: 30p"); 
        totalPointsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(totalPointsLabel, BorderLayout.EAST);

        this.rankListPanel = new JPanel();
        rankListPanel.setLayout(new GridLayout(5, 1, 0, 10));
        rankListPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        updateRankingDisplay(); 

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(rankListPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * (공개 창구) 다른 탭(분리수거, 퀴즈)에서 
     * 랭킹 등록을 요청할 때 호출하는 메소드
     */
    public void registerPoints(String userName, int points) {
        if (userName == null || userName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "사용자 이름이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        rankingManager.accumulatePoints(userName, points);
        updateRankingDisplay();
        
        JOptionPane.showMessageDialog(this, points + "P가 " + userName + "님에게 누적되었습니다.");
    }

    /**
     * DB에서 랭킹을 읽어와 화면을 업데이트하는 메소드
     */
    private void updateRankingDisplay() {
        rankListPanel.removeAll();
        List<ScoreEntry> rankings = rankingManager.getRankings();

        Color[] colors = {
            new Color(255, 223, 0), // 1위: 금색
            new Color(192, 192, 192), // 2위: 은색
            new Color(205, 127, 50),  // 3위: 동색
            Color.WHITE,             // 4위
            Color.WHITE              // 5위
        };

        for (int i = 0; i< 5; i++) {
            JPanel entryPanel;
            if (i < rankings.size()) {
                ScoreEntry entry = rankings.get(i);
                entryPanel = createRankEntryPanel(i + 1, entry.getName(), entry.getPoints(), colors[i]);
            } else {
                entryPanel = createRankEntryPanel(i + 1, "---", 0, Color.WHITE);
            }
            rankListPanel.add(entryPanel);
        }

        rankListPanel.revalidate();
        rankListPanel.repaint();
    }

    /**
     * 각 랭킹 항목(한 줄)을 만드는 메소드
     */
    private JPanel createRankEntryPanel(int rank, String name, int point, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel rankLabel = new JLabel(String.valueOf(rank));
        rankLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        rankLabel.setPreferredSize(new Dimension(50, 50));
        rankLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        nameLabel.setBorder(new EmptyBorder(0, 20, 0, 0));

        String pointText = (point == 0) ? "-" : (point + "P");
        JLabel pointLabel = new JLabel(pointText);
        pointLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        pointLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        panel.add(rankLabel, BorderLayout.WEST);
        panel.add(nameLabel, BorderLayout.CENTER);
        panel.add(pointLabel, BorderLayout.EAST);

        return panel;
    }

    // ##### (오류 지점) 이 메소드가 없으면 오류가 납니다 #####
    /**
     * 메인 메소드 (프로그램 시작점)
     */
    public static void main(String[] args) {
        // 기본 UI로 즉시 실행
        SwingUtilities.invokeLater(() -> new RankingWindow());
    }
}