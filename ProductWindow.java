package recycle.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import recycle.DAO.ProductsDAO;
import recycle.DTO.ProductsDTO;
import recycle.DAO.UsersDAO; 
import recycle.DTO.UsersDTO; 

public class ProductWindow extends JFrame {
    
    private List<ProductsDTO> productsData; // DB 데이터 리스트
    private UsersDTO currentUser; // 현재 로그인한 유저
    
    // 우측 상세 화면 컴포넌트들
    private JLabel nameLabel;
    private JLabel pointsLabel;
    private JTextArea guideArea;
    private JButton purchaseButton;
    private ProductsDTO selectedProduct;

    // 로그인한 유저 정보를 받아옴
    public ProductWindow(UsersDTO user) {
        this.currentUser = user;
        
        setTitle("분리수거 안내 서비스");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상품 데이터 가져오기
        ProductsDAO dao = new ProductsDAO();
        productsData = dao.getAllProducts();

        // 좌측 패널 상품 목록(스크롤)
        JPanel productListPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        if (productsData.isEmpty()) {
            JLabel emptyLabel = new JLabel("등록된 상품이 없습니다.", JLabel.CENTER);
            productListPanel.add(emptyLabel);
        } 
        else {
            for (ProductsDTO product : productsData) {
                String buttonText = "<html><center><b>" 
                        + product.getProductName() + "</b><br>"
                        + product.getRequiredPoints() + " P"
                        + "</center></html>";
                
                JButton productBtn = new JButton(buttonText);
                productBtn.setPreferredSize(new Dimension(180, 60));
                productBtn.setFocusPainted(false);
                productBtn.setBackground(Color.WHITE);

                // 상품 버튼 클릭 시 우측 패널 갱신
                productBtn.addActionListener(e -> updatepurchasePanel(product));
                
                productListPanel.add(productBtn);
            }
        }

        // 목록 패널을 상단 정렬하기 위한 패널
        JPanel productButtonListNorth = new JPanel(new BorderLayout());
        productButtonListNorth.add(productListPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(productButtonListNorth);
        scrollPane.setPreferredSize(new Dimension(220, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(15); // 스크롤 속도
        add(scrollPane, BorderLayout.WEST);

        // 우측 상세 정보 및 구매 패널 
        JPanel purchasePanel = new JPanel(new BorderLayout());

        // 상단 상품명 표시
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        nameLabel = new JLabel("상품을 선택해주세요", JLabel.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
      
        // 상단 포인트 표시
        pointsLabel = new JLabel("", JLabel.CENTER);
        pointsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        pointsLabel.setForeground(Color.BLUE);
        
        infoPanel.add(nameLabel);
        infoPanel.add(pointsLabel);
        purchasePanel.add(infoPanel, BorderLayout.NORTH);

        // 중앙 상품 설명
        guideArea = new JTextArea();
        guideArea.setEditable(false); // 읽기 전용(수정 금지)
        guideArea.setLineWrap(true); // 글자가 길어지면 다음 줄로 이동(= "\n")
        guideArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));  
        guideArea.setMargin(new Insets(15, 15, 15, 15)); // 글자 주변 여백(위, 왼쪽, 아래, 오른쪽)
        purchasePanel.add(guideArea, BorderLayout.CENTER); 

        // 하단 구매 버튼
        purchaseButton = new JButton("구매하기");
        purchaseButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        purchaseButton.setPreferredSize(new Dimension(0, 50));
        purchaseButton.setEnabled(false); // 상품 선택 전에는 비활성화
        
        // 구매 버튼 이벤트 (예시 로직)
        purchaseButton.addActionListener(e -> handlePurchase());
        
        purchasePanel.add(purchaseButton, BorderLayout.SOUTH);

        add(purchasePanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // 우측 구매 패널 정보 갱신 메서드
    private void updatepurchasePanel(ProductsDTO product) {
        this.selectedProduct = product;
        
        nameLabel.setText(product.getProductName());
        pointsLabel.setText("필요 포인트: " + product.getRequiredPoints() + " P");
        
        // 간단한 설명 생성
        String guide = "[ " + product.getProductName() + " ]\n\n";
        guide += "이 상품은 친환경 상품 또는 기부를 위한 상품입니다.\n";
        guide += "보유 포인트를 사용하여 교환할 수 있습니다.\n\n";
        guide += "현재 나의 보유 포인트: " + (currentUser != null ? currentUser.getBalancePoints() : 0) + " P";
        
        guideArea.setText(guide);
        purchaseButton.setEnabled(true);
    }

    // 구매 처리 메서드
    private void handlePurchase() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "로그인 정보가 없습니다.");
            return;
        }

        int userPoint = currentUser.getBalancePoints(); // 회원 보유포인트
        int price = selectedProduct.getRequiredPoints(); // 상품 구매 필요포인트

        if (userPoint >= price) {
            currentUser.setBalancePoints(userPoint - price); // 메모리 상에서 포인트 차감
            
            UsersDAO dao = new UsersDAO();
            dao.updateUserPoint(currentUser); // DB에서 포인트 차감

            JOptionPane.showMessageDialog(this, 
                selectedProduct.getProductName() + " 구매가 완료되었습니다!\n" +
                "남은 포인트: " + currentUser.getBalancePoints());
            
            updatepurchasePanel(selectedProduct); // 포인트 갱신을 위한 화면 갱신
            
        } else {
            JOptionPane.showMessageDialog(this, "포인트가 부족합니다!", "구매 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 테스트용 메인
    /*
    public static void main(String[] args) {        
    	UsersDTO testUser = new UsersDTO();
        testUser.setUserId("test");
        testUser.setBalancePoints(10000); 
        
        new ProductWindow(testUser);
    }
    */
}