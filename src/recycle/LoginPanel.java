package recycle;

import Main.MainApp; 
import db.DAO.UserDAO; 
import db.DTO.UserDTO; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class LoginPanel extends JFrame implements ActionListener {

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    
    private UserDAO userDAO;
    
    public LoginPanel() {
        try {
            this.userDAO = new UserDAO();
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(null, "DB 연결 또는 초기화 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            System.err.println("DB 연결 오류: " + e.getMessage());
            System.exit(1); 
        }
        
        setTitle("분리수거 안내 서비스");
        setSize(350, 300); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10));

        // 1. 타이틀
        JLabel titleLabel = new JLabel("로그인", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 입력 패널 
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("ID:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.gridy = 0; idField = new JTextField(15); inputPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("PW:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.gridy = 1; passwordField = new JPasswordField(15); inputPanel.add(passwordField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // 3. 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        loginButton = new JButton("로그인");
        registerButton = new JButton("회원가입");
        
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == loginButton) {
            handleLogin();
        } else if (source == registerButton) {
            new RegisterPanel(userDAO); 
        }
    }

    //로그인 버튼 처리 로직 
     
    private void handleLogin() {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        
        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID와 비밀번호를 모두 입력하세요.");
            return;
        }

        try {
            // UserDAO를 통해 로그인 시도
            UserDTO user = userDAO.loginUser(id, password);

            if (user != null) { 
                JOptionPane.showMessageDialog(this, user.getNickname() + "님, 로그인 성공!");
                
                SwingUtilities.invokeLater(() -> {
                     // MainApp 생성자에 UserDTO 객체를 전달
                     new MainApp(user); 
                });
                
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "ID 또는 비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception ex) { 
            System.err.println("로그인 DB 오류: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "로그인 처리 중 DB 오류가 발생했습니다.");
        }
    }
}


//회원가입 GUI 창입니다. 
 
class RegisterPanel extends JFrame implements ActionListener { 
    
    private JTextField nicknameField, idField;
    private JPasswordField passwordField;
    private JButton checkIdButton, registerButton;
    private boolean isIdChecked = false; 
    
    private UserDAO userDAO; 
    
    public RegisterPanel(UserDAO userDAO) { 
        this.userDAO = userDAO;
        
        setTitle("회원가입");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10));

        // 1. 타이틀
        JLabel titleLabel = new JLabel("회원가입", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 입력 패널
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 닉네임
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("닉네임:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; nicknameField = new JTextField(15); inputPanel.add(nicknameField, gbc);
        
        // ID 및 중복 확인 버튼
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; inputPanel.add(new JLabel("ID:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.gridy = 1; idField = new JTextField(10); inputPanel.add(idField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; checkIdButton = new JButton("중복확인"); inputPanel.add(checkIdButton, gbc);

        // 비밀번호
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("PW:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; passwordField = new JPasswordField(15); inputPanel.add(passwordField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // 3. 회원가입 버튼
        registerButton = new JButton("회원가입 하기");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 리스너 등록
        checkIdButton.addActionListener(this);
        registerButton.addActionListener(this);
        
        // ID 필드 내용이 변경되면 중복 확인 상태 초기화 
        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                isIdChecked = false;
            }
        });

        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == checkIdButton) {
            handleIdCheck();
        } else if (source == registerButton) {
            handleRegister();
        }
    }
    
    //ID 중복 확인 로직 
    private void handleIdCheck() {
        String id = idField.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.");
            return;
        }

        try {
            // UserDAO.isIdDuplicate()를 사용
            if (userDAO.isIdDuplicate(id)) { 
                JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
                isIdChecked = false;
            } else {
                JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
                isIdChecked = true;
            }
        } catch (Exception ex) { 
             System.err.println("ID 중복 확인 DB 오류: " + ex.getMessage());
             JOptionPane.showMessageDialog(this, "중복 확인 중 DB 오류 발생.");
        }
    }
    
    //회원가입 최종 처리 로직
    private void handleRegister() {
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
        
        try {
            // UserDAO.registerUser()를 사용
            boolean success = userDAO.registerUser(id, password, nickname);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "회원가입 성공! 로그인 창으로 돌아가 로그인해주세요.");
                this.dispose(); 
            } else {
                 JOptionPane.showMessageDialog(this, "회원가입 중 알 수 없는 오류가 발생했습니다.");
            }
        } catch (Exception ex) { 
            System.err.println("회원가입 DB 오류: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "회원가입 중 DB 오류가 발생했습니다.");
        }
    }
}