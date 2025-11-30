package recycle.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import recycle.DAO.UsersDAO; // DAO 임포트
import recycle.DTO.UsersDTO; // DTO 임포트

public class LoginWindow extends JFrame implements ActionListener {

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginWindow() {
        setTitle("분리수거 안내 서비스");
        setSize(350, 300); // UI에 맞게 크기 조절
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // 전체 레이아웃

        // 1. 상단 "로그인" 타이틀
        JLabel titleLabel = new JLabel("로그인", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙 입력 폼 (GridBagLayout 사용)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간 여백

        // 아이디
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("아이디"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        idField = new JTextField(15);
        formPanel.add(idField, gbc);

        // 암호
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("암 호"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. 하단 버튼 패널 (회원가입, 로그인)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("회원가입");
        loginButton = new JButton("로그인");
        
        registerButton.addActionListener(this);
        loginButton.addActionListener(this);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // 로그인 버튼
        if (source == loginButton) {
            String id = idField.getText();
            String password = new String(passwordField.getPassword());

            if (id.isEmpty() == true || password.isEmpty() == true) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.");
                return;
            }

            UsersDAO dao = new UsersDAO();
            
            // 2. DB에서 해당 아이디의 회원 정보 가져오기
            UsersDTO user = dao.getUser(id);
            
            if (user == null) {
            	JOptionPane.showMessageDialog(this, "존재하지 않는 아이디입니다.");
            }
            
            else {
            	if(user.getPassword().equals(password) == true) // 성공
            	{
            		JOptionPane.showMessageDialog(this, "로그인 성공!");
            		this.dispose();
            		// MainWindow 만든 후 주석 해제!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            		//new MainWindow(user); // main GUI 실행
            	}
            	else {// 실패
                    JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
                }
            }
        } 
        
        // 회원가입 버튼
        else if (source == registerButton) {
        	this.dispose(); // 로그인 창 닫기
            new RegisterWindow(); // 회원가입 창 띄우기
        }
    }

    public static void main(String[] args) {
        new LoginWindow();
    }
}