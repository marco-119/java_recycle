package Login;

import Main.MainAppWindow; // 메인 윈도우 임포트
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 로그인 GUI 창 (시작 화면)
 * 이미지와 요구사항에 맞게 수정됨
 */
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

        if (source == loginButton) {
            // "로그인" 버튼 클릭
            String id = idField.getText();
            String password = new String(passwordField.getPassword());

            if (id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID와 비밀번호를 모두 입력하세요.");
                return;
            }

            // AuthHandler의 loginUser 메소드 호출
            int loginResult = AuthHandler.loginUser(id, password);

            switch (loginResult) {
                case 0: // 로그인 성공
                    JOptionPane.showMessageDialog(this, "로그인 성공!");
                    showMainWindow(id);
                    break;
                case 1: // 비밀번호 틀림
                    JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
                    break;
                case 2: // ID 없음
                    JOptionPane.showMessageDialog(this, "존재하지 않는 ID입니다. 회원가입을 진행해주세요.");
                    break;
                default: // 오류
                    JOptionPane.showMessageDialog(this, "오류가 발생했습니다.");
                    break;
            }
        } else if (source == registerButton) {
            // "회원가입" 버튼 클릭
            new RegisterWindow(); // 회원가입 창 띄우기
        }
    }

    /**
     * 로그인 성공 시 메인 창을 띄우고 현재 창을 닫습니다.
     * @param userID 로그인한 사용자의 ID
     */
    private void showMainWindow(String userID) {
        new Main.MainAppWindow(userID); 
        this.dispose(); // 로그인 창 닫기
    }

    /**
     * 프로그램 시작점
     */
    public static void main(String[] args) {
        new LoginWindow();
    }
}