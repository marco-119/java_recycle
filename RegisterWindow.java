package Login;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 회원가입 GUI 창 (이미지 기반)
 */
public class RegisterWindow extends JFrame implements ActionListener {

    private JTextField nicknameField, idField;
    private JPasswordField passwordField;
    private JButton checkIdButton, registerButton;
    private boolean isIdChecked = false; // ID 중복 확인 완료 여부

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
        
        // 아이디
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("아이디"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        idField = new JTextField(15);
        formPanel.add(idField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        checkIdButton = new JButton("중복 확인");
        checkIdButton.addActionListener(this);
        formPanel.add(checkIdButton, gbc);

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

        if (source == checkIdButton) {
            // "ID 중복 확인" 버튼
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디를 입력하세요.");
                return;
            }

            // AuthHandler를 통해 ID 중복 검사
            if (AuthHandler.checkIdDuplicate(id)) {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
                isIdChecked = false;
            } else {
                JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
                isIdChecked = true;
            }

        } else if (source == registerButton) {
            // "회원가입 하기" 버튼
            String nickname = nicknameField.getText();
            String id = idField.getText();
            String password = new String(passwordField.getPassword());

            if (nickname.isEmpty() || id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
                return;
            }

            if (!isIdChecked) {
                JOptionPane.showMessageDialog(this, "아이디 중복 확인을 먼저 해주세요.");
                return;
            }

            // AuthHandler를 통해 회원가입 시도
            int result = AuthHandler.registerUser(id, password, nickname);

            if (result == 0) {
                JOptionPane.showMessageDialog(this, "회원가입 성공! 로그인 창으로 돌아가 로그인해주세요.");
                this.dispose(); // 회원가입 창 닫기
            } else if (result == 1) {
                JOptionPane.showMessageDialog(this, "오류: 아이디가 중복됩니다. 다시 확인해주세요.");
                isIdChecked = false;
            } else {
                JOptionPane.showMessageDialog(this, "파일 쓰기 오류가 발생했습니다.");
            }
        }
    }
}