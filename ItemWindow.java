package recycle.GUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.util.Vector;

import recycle.DAO.ItemsDAO;
import recycle.DTO.ItemsDTO;

// 각 카테고리(탭)마다 들어갈 패널
//CategoryPanel 클래스 전체를 이걸로 교체해보세요.
class CategoryPanel extends JPanel {
 private JTextArea guideArea;    // 배출 방법 설명 나오는 곳
 private List<ItemsDTO> itemsData; // DB 데이터

 public CategoryPanel(String categoryId) {
     setLayout(new BorderLayout());

     // DAO 데이터 가져오기
     ItemsDAO dao = new ItemsDAO();
     itemsData = dao.getItemsByCategory(categoryId);

     // 품목 버튼을 담을 패널
     JPanel itemButtonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
     
     if (itemsData.isEmpty()) { //DB에 품목 데이터가 비어있다면 실행
         JLabel emptyLabel = new JLabel("등록된 품목이 없습니다.", JLabel.CENTER);
         itemButtonPanel.add(emptyLabel);
     } 
     else {
    	 // 데이터 개수만큼 반복하며 버튼 생성
         for (ItemsDTO item : itemsData) {
             JButton itemButton = new JButton(item.getItemName()); //품목명으로 버튼 생성
             itemButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
             itemButton.setFocusPainted(false); // 클릭 시 생기는 테두리 제거
             itemButton.setPreferredSize(new Dimension(0, 40)); // 버튼 높이 고정

             // 품목 버튼 클릭 이벤트
             itemButton.addActionListener(e -> {
                 // 버튼을 누르면 설명창 업데이트
                 String content = "[ " + item.getItemName() + " ]\n\n"; // 품목명
                 content += "■ 배출 방법:\n" + item.getDisposalGuide();	// 배출 방법 설명
                 
                 guideArea.setText(content);  	// content를 우측 설명창에 넣음
             });

             itemButtonPanel.add(itemButton); // 패널에 버튼 추가
         }
     }

     // 설명창 (JTextArea) 설정
     guideArea = new JTextArea();
     guideArea.setEditable(false); 								// 읽기 전용(수정 금지)
     guideArea.setLineWrap(true); 								// 글자가 길어지면 다음 줄로 이동(= "\n")
     guideArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));	
     guideArea.setMargin(new Insets(15, 15, 15, 15));			// 글자 주변 여백(위, 왼쪽, 아래, 오른쪽)
     guideArea.setText("왼쪽 목록에서 품목을 클릭하세요.");
     guideArea.setBackground(new Color(245, 245, 245)); 		// 배경색을 연한 회색으로 변경

     
     // 품목 버튼을 스크롤 패널에 바로 넣으면 품목 개수가 적은 경우, 스크롤 패널을 전부 채워버림
     // 그래서 품목 버튼을 위쪽으로 정렬해서 한 번 감싸주는 패널을 생성
     JPanel itemButtonListNorth = new JPanel(new BorderLayout());
     itemButtonListNorth.add(itemButtonPanel, BorderLayout.NORTH);

     JScrollPane listScroll = new JScrollPane(itemButtonListNorth); // 버튼 패널 스크롤
     
     // 스크롤바 속도 조절
     listScroll.getVerticalScrollBar().setUnitIncrement(15);
     listScroll.setPreferredSize(new Dimension(220, 0));
     add(listScroll, BorderLayout.WEST);

     JScrollPane guideScroll = new JScrollPane(guideArea);  		// 설명창 스크롤
     add(guideScroll, BorderLayout.CENTER);
 }
}

public class ItemWindow extends JFrame {

    public ItemWindow() {
        setTitle("분리수거 안내 서비스");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 탭 패널 생성 (왼쪽에 탭 배치)
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        
        // 각 분류 탭 추가
        tabbedPane.addTab("종이", new CategoryPanel("C01"));
        tabbedPane.addTab("비닐", new CategoryPanel("C02"));
        tabbedPane.addTab("유리병", new CategoryPanel("C03"));
        tabbedPane.addTab("종이팩", new CategoryPanel("C04"));
        tabbedPane.addTab("캔·고철", new CategoryPanel("C05"));
        tabbedPane.addTab("스티로폼", new CategoryPanel("C06"));
        tabbedPane.addTab("플라스틱", new CategoryPanel("C07"));
        tabbedPane.addTab("기타", new CategoryPanel("C08"));

        add(tabbedPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ItemWindow();
    }
}