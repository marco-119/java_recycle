package recycle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// RankingManager의 내부 DTO를 임포트
import recycle.RankingManager.RankingDTO; 


public class RankingWindow extends JPanel { 
    
    private final String currentUserId; 
    
    private recycle.RankingManager rankingManager; 
    private JPanel rankListPanel;
    private JLabel totalPointsLabel; 
    
    // UI에 필요한 상수
    private static final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 30);
    private static final Font LABEL_FONT = new Font("맑은 고딕", Font.PLAIN, 16);
    private static final Color[] RANK_COLORS = {
        new Color(255, 223, 0), 
        new Color(192, 192, 192), 
        new Color(205, 127, 50), 
        Color.WHITE,
        Color.WHITE
    };

    public RankingWindow(String userId) { 
        this.currentUserId = (userId != null && !userId.isEmpty()) ? userId : "테스트ID";
        
        try {
            this.rankingManager = new recycle.RankingManager();
        } catch (RuntimeException e) {
            System.err.println("랭킹 관리자 초기화 실패: " + e.getMessage());
            this.rankingManager = null; 
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. 상단 패널 (TOP 5 타이틀 및 총 포인트)
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("TOP 5");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        this.totalPointsLabel = new JLabel("총 포인트: 0p"); 
        totalPointsLabel.setFont(LABEL_FONT);

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(totalPointsLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. 랭킹 목록 패널
        this.rankListPanel = new JPanel();
        rankListPanel.setLayout(new GridLayout(5, 1, 0, 10)); 
        rankListPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // 3. 내 랭킹 정보 표시 패널 (SOUTH에 먼저 추가)
        add(createMyRankPanel(), BorderLayout.SOUTH);
        
        // 4. 랭킹 정보 업데이트 및 표시 (초기 로드)
        updateRankingDisplay();
        
        add(rankListPanel, BorderLayout.CENTER);
    }


    public void updateRankingDisplay() {
        if (rankingManager == null) {
            rankListPanel.removeAll();
            rankListPanel.add(new JLabel("DB 연결 오류로 랭킹 정보를 표시할 수 없습니다.", SwingConstants.CENTER));
            rankListPanel.revalidate();
            rankListPanel.repaint();
            return;
        }
        
        rankListPanel.removeAll();
        List<RankingDTO> rankings = new ArrayList<>();
        
        try {
            rankings = rankingManager.getSortedRankingList();
        } catch (SQLException e) {
            System.err.println("랭킹 정보 로드 오류: " + e.getMessage());
        }

        for (int i = 0; i < 5; i++) {
            JPanel entryPanel;
            if (i < rankings.size()) {
                RankingDTO entry = rankings.get(i);
                entryPanel = createRankEntryPanel(i + 1, entry.getNickname(), entry.getBalancePoints(), RANK_COLORS[i]);
            } else {
                entryPanel = createRankEntryPanel(i + 1, "---", 0, Color.WHITE);
            }
            rankListPanel.add(entryPanel);
        }

        // '내 랭킹 정보' 패널 업데이트
        LayoutManager lm = getLayout();
        if (lm instanceof BorderLayout) {
            BorderLayout bl = (BorderLayout) lm;
            Component southComponent = bl.getLayoutComponent(this, BorderLayout.SOUTH);
            
            if (southComponent instanceof MyRankPanel) {
                ((MyRankPanel) southComponent).updateMyRank(rankings);
            }
        }
        
        rankListPanel.revalidate();
        rankListPanel.repaint();
    }

    //개별 순위 항목 패널을 생성
    private JPanel createRankEntryPanel(int rank, String name, int point, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        
        if (bgColor.equals(Color.WHITE)) {
            panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        } else {
            panel.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2)); 
        }

        // 1. 순위 번호 (WEST)
        JLabel rankLabel = new JLabel(String.valueOf(rank));
        rankLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        rankLabel.setPreferredSize(new Dimension(50, 50));
        rankLabel.setHorizontalAlignment(JLabel.CENTER);

        // 2. 닉네임 (CENTER)
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        nameLabel.setBorder(new EmptyBorder(0, 20, 0, 0));

        // 3. 포인트 (EAST)
        JLabel pointLabel = new JLabel((point == 0 && "---".equals(name)) ? "-" : (point + "P"));
        pointLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        pointLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        pointLabel.setHorizontalAlignment(SwingConstants.RIGHT); 

        panel.add(rankLabel, BorderLayout.WEST);
        panel.add(nameLabel, BorderLayout.CENTER);
        panel.add(pointLabel, BorderLayout.EAST);

        return panel;
    }
    
    // 내 랭킹 정보를 표시하는 패널을 생성하고 반환
    private MyRankPanel createMyRankPanel() {
        return new MyRankPanel(currentUserId, rankingManager);
    }
}


// 내부 클래스: 내 랭킹 정보 패널

class MyRankPanel extends JPanel {
    private final String userId;
    private final recycle.RankingManager manager;
    private final JLabel infoLabel;
    
    public MyRankPanel(String userId, recycle.RankingManager manager) {
        this.userId = userId;
        this.manager = manager;
        
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 0, 0, 0));

        this.infoLabel = new JLabel();
        this.infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        
        add(infoLabel, BorderLayout.CENTER);
        infoLabel.setText(manager != null ? 
            String.format("<html><p align='center'>[내 정보] 닉네임: %s (ID: %s) | 현재 포인트: 0점 | 순위: 로딩 중</p></html>", userId, userId) : 
            "<html><p align='center'>[내 정보] DB 연결 오류</p></html>");
    }
    
    public void updateMyRank(List<RankingDTO> rankingList) {
        if (manager == null) {
            infoLabel.setText("<html><p align='center'>[내 정보] DB 연결 오류</p></html>");
            return;
        }
        
        String myInfoHtml = manager.getMyRankInfo(userId, rankingList);
        infoLabel.setText(myInfoHtml);
    }
}