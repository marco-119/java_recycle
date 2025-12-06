package recycle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.sql.SQLException; 

import db.DAO.ProductsDAO;
import db.DTO.ProductsDTO;
import db.DAO.UserDAO;
import db.DTO.UserDTO;


public class ProductWindow extends JPanel { 

    private List<ProductsDTO> productsData; 
    private UserDTO currentUser; 

    // 우측 상세 화면 컴포넌트들
    private JLabel nameLabel;
    private JLabel pointsLabel;
    private JTextArea guideArea;
    private JButton purchaseButton;
    private ProductsDTO selectedProduct;


    private static final Color LIGHT_BLUE = new Color(204, 229, 255); 

    // 로그인한 유저 정보를 받아옴
    public ProductWindow(UserDTO user) {
        this.currentUser = user;

        setLayout(new BorderLayout());

        // 상품 데이터 가져오기 
        ProductsDAO dao = new ProductsDAO();
        productsData = dao.getAllProducts();

        // 좌측 패널 상품 목록
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
                
        
                productBtn.setBackground(LIGHT_BLUE); 

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
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
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
        guideArea.setEditable(false);
        guideArea.setLineWrap(true);
        guideArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        guideArea.setMargin(new Insets(15, 15, 15, 15));
        purchasePanel.add(guideArea, BorderLayout.CENTER);

        // 하단 구매 버튼
        purchaseButton = new JButton("구매하기");
        purchaseButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        purchaseButton.setPreferredSize(new Dimension(0, 50));
        purchaseButton.setEnabled(false); 

        // 구매 버튼 
        purchaseButton.addActionListener(e -> handlePurchase());

        purchasePanel.add(purchaseButton, BorderLayout.SOUTH);

        add(purchasePanel, BorderLayout.CENTER);
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

        int userPoint = currentUser.getBalancePoints(); 
        int price = selectedProduct.getRequiredPoints(); 

        if (userPoint >= price) {
            int newBalance = userPoint - price;
            currentUser.setBalancePoints(newBalance); 

            try {
                UserDAO dao = new UserDAO();
                dao.updateUserPoint(currentUser); 

                JOptionPane.showMessageDialog(this,
                        selectedProduct.getProductName() + " 구매가 완료되었습니다!\n" +
                                "남은 포인트: " + currentUser.getBalancePoints());

                updatepurchasePanel(selectedProduct); 
                
            } catch (SQLException e) {
                 JOptionPane.showMessageDialog(this, 
                     "상품 구매 처리 중 데이터베이스 오류가 발생했습니다.\n" + e.getMessage(), 
                     "DB 오류", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
                 
                 currentUser.setBalancePoints(userPoint); 
                 System.err.println("DB 오류로 인해 DTO의 포인트 변경이 롤백되었습니다.");
            }

        } else {
            JOptionPane.showMessageDialog(this, "포인트가 부족합니다!", "구매 실패", JOptionPane.ERROR_MESSAGE);
        }
    }
}