package recycle.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import recycle.DAO.UsersDAO; // DAO 임포트
import recycle.DTO.UsersDTO; // DTO 임포트

/**
 * 회원가입 GUI 창 (이미지 기반)
 */
public class RegisterWindow extends JFrame implements ActionListener {

    private JTextField nicknameField, userIdField;
    private JPasswordField passwordField;
    private JButton checkUserIdButton, checkNicknameButton, registerButton;
    private boolean isUserIdChecked = false, isNicknameChecked = false; // ID 중복 확인 완료 여부

    public RegisterWindow() {
        setTitle("분리수거 안내 서비스");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫기
        setLocationRelativeTo(null); // 화면 중앙에
        setLayout(new BorderLayout(10, 10));

        // 1. 상단 "회원가입" 타이틀
        JLabel titleLabel = new JLabel("회원가입", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙 입력 폼 (GridBagLayout 사용)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간 여백
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 닉네임
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("닉네임"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        nicknameField = new JTextField(15);
        formPanel.add(nicknameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        checkNicknameButton = new JButton("중복 확인");
        checkNicknameButton.addActionListener(this);
        formPanel.add(checkNicknameButton, gbc);

        
        // 아이디
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("아이디"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        userIdField = new JTextField(15);
        formPanel.add(userIdField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        checkUserIdButton = new JButton("중복 확인");
        checkUserIdButton.addActionListener(this);
        formPanel.add(checkUserIdButton, gbc);

        // 암호
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("암 호"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. 하단 "회원가입 하기" 버튼
        JPanel bottomPanel = new JPanel();
        registerButton = new JButton("회원가입 하기");
        registerButton.addActionListener(this);
        bottomPanel.add(registerButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        UsersDAO dao = new UsersDAO();
       

        if (source == checkUserIdButton) {
            // "ID 중복 확인" 버튼
            String id = userIdField.getText();
            
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디를 입력하세요.");
                return;
            }

            boolean exist = dao.isIdExists(id);
            
            if(exist) {
            	JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
            	isUserIdChecked = false;
            }
            else{
            	JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
            	isUserIdChecked = true;
            }
        }
        
        if(source == checkNicknameButton) {
        	String nickname = nicknameField.getText();
        	
        	if(nickname.isEmpty()) {
        		JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.");
                return;
        	}
        	
        	boolean exist = dao.isNicknameExists(nickname);

            if(exist) {
            	JOptionPane.showMessageDialog(this, "이미 사용 중인 닉네임입니다.");
            	isNicknameChecked = false;
            }
            else{
            	JOptionPane.showMessageDialog(this, "사용 가능한 닉네임입니다.");
            	isNicknameChecked = true;
            }
        }
        
        if(source == registerButton)
        {
        	if(isUserIdChecked == true && isNicknameChecked == true) {
        		UsersDTO dto = new UsersDTO();
        		
        		dto.setUserId(userIdField.getText()); 
        		dto.setNickname(nicknameField.getText());
        		dto.setPassword(new String(passwordField.getPassword()));
        		
        		dao.addUser(dto);
        	}
        }
    }
    public static void main(String[] args) {
        new RegisterWindow();
    }
}