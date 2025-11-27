package recycle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import db.DAO.UserDAO;
import db.DAO.RecycleLogDAO; 


public class QuizPanel extends JPanel {

    private JLabel questionLabel;
    private JPanel quizGrid;
    private JLabel messageLabel;

    private List<Quiz> quizList;
    private int currentQuizIndex = 0;
    private int correctCount = 0;

    private String userId;
    private RecycleLogDAO logDAO;
    private UserDAO userDAO;

    private boolean quizInProgress = true;
    private final int POINT_PER_QUIZ = 10;


    private class QuizItem extends JPanel {
        private String answer;
        private JLabel textLabel;
        private JPanel imageBox;

        public QuizItem(String answerText) {
            this.answer = answerText;
            setLayout(new BorderLayout());
            this.setBackground(Color.WHITE);
            this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // 텍스트 라벨 (정답 텍스트)
            textLabel = new JLabel(answerText, SwingConstants.CENTER);
            textLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));

            // 이미지 박스 (이미지 추가를 위해 남겨둠)
            imageBox = new JPanel();
            imageBox.setPreferredSize(new Dimension(80, 80));
            imageBox.setBackground(new Color(240, 240, 240));
            imageBox.setLayout(new GridBagLayout());
            // 이미지 자리에 텍스트 임시 삽입
            imageBox.add(new JLabel("Image", SwingConstants.CENTER));

            add(imageBox, BorderLayout.NORTH);
            add(textLabel, BorderLayout.CENTER);

            // 마우스 클릭 리스너 추가
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (quizInProgress) {
                        checkAnswer(answerText);
                    }
                }
            });

            // 마우스 오버 효과
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (quizInProgress) setBackground(new Color(230, 240, 255));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (quizInProgress) setBackground(Color.WHITE);
                }
            });
        }
    }

    // 퀴즈 데이터 모델
    private class Quiz {
        String question;
        String answer;
        List<String> choices;

        public Quiz(String q, String a, String... c) {
            this.question = q;
            this.answer = a;
            this.choices = new ArrayList<>();
            for (String choice : c) {
                choices.add(choice);
            }
            // 정답을 포함하여 보기를 구성
            choices.add(answer);
            Collections.shuffle(choices); // 보기 순서 섞기
        }
    }

    public QuizPanel(String userId) {
        this.userId = userId;


        try {

            this.userDAO = new UserDAO();
        } catch (Exception e) {
             System.err.println("DAO 초기화 실패: " + e.getMessage());
             JOptionPane.showMessageDialog(null, "DB 연결 또는 퀴즈 기능 초기화 실패", "DB Error", JOptionPane.ERROR_MESSAGE);
             throw new RuntimeException("DB 연결 또는 DAO 초기화 실패", e);
        }

        // 퀴즈 데이터 초기화
        initializeQuizzes();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. 질문 레이블
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(questionLabel, BorderLayout.NORTH);

        // 2. 퀴즈 선택지 그리드 패널
        quizGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        add(quizGrid, BorderLayout.CENTER);

        // 3. 메시지 레이블 (정답/오답 표시)
        messageLabel = new JLabel("분리수거 퀴즈를 시작합니다!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        messageLabel.setForeground(Color.BLUE);
        add(messageLabel, BorderLayout.SOUTH);

        // 첫 번째 퀴즈 로드
        loadQuiz(currentQuizIndex);
    }

    //퀴즈 데이터 생성 및 초기화
    private void initializeQuizzes() {
        quizList = new ArrayList<>();
        quizList.add(new Quiz("Q1. 신문지, 책 등 종이류 분리수거 시 가장 먼저 해야 할 것은?", "테이프나 스프링 제거하기", "코팅된 종이만 따로 모으기", "음식물 묻은 종이 버리기", "물기에 젖지 않게 묶기"));
        quizList.add(new Quiz("Q2. 페트병 분리수거 시 올바른 방법은?", "내용물을 비우고 라벨 제거", "뚜껑을 닫고 버리기", "찌그러뜨리지 않고 배출", "세척하지 않고 배출"));
        quizList.add(new Quiz("Q3. 플라스틱 용기에 음식물이 묻어 세척이 어려운 경우 처리 방법은?", "일반 쓰레기로 버리기", "페트병과 함께 분리수거", "플라스틱 재활용함에 넣기", "물에 담가두었다가 버리기"));
        quizList.add(new Quiz("Q4. 유리병 중 재활용이 불가능한 것은?", "깨진 유리", "음료수 병", "맥주병", "소주병"));
        quizList.add(new Quiz("Q5. 스티로폼 포장재 분리수거 시 올바른 방법은?", "내용물 제거 후 깨끗이 씻기", "테이프를 붙인 채 버리기", "색상이 있는 스티로폼만 따로 모으기", "음식물이 묻은 채로 버리기"));

        Collections.shuffle(quizList); // 퀴즈 순서 섞기
    }


    private void loadQuiz(int index) {
        if (index >= quizList.size()) {
            endQuiz();
            return;
        }

        Quiz currentQuiz = quizList.get(index);
        questionLabel.setText(currentQuiz.question);

        quizGrid.removeAll();

        for (String choice : currentQuiz.choices) {
            QuizItem item = new QuizItem(choice);
            quizGrid.add(item);
        }

        quizGrid.revalidate();
        quizGrid.repaint();
        messageLabel.setText(String.format("Q%d. 다음 퀴즈!", index + 1));
        messageLabel.setForeground(Color.BLUE);
    }

    //정답을 확인하고 다음 퀴즈로 넘어감
    private void checkAnswer(String selectedAnswer) {
        Quiz currentQuiz = quizList.get(currentQuizIndex);

        // 정답/오답 판별
        if (selectedAnswer.equals(currentQuiz.answer)) {
            correctCount++;
            messageLabel.setText("정답입니다! 🥳 (+10점)");
            messageLabel.setForeground(new Color(0, 100, 0)); 
        } else {
            messageLabel.setText(String.format("오답입니다. 😥 (정답: %s)", currentQuiz.answer));
            messageLabel.setForeground(Color.RED);
        }

        // 퀴즈 진행 상태 잠금
        quizInProgress = false;

        // 잠시 후 다음 퀴즈 로드 (마지막 퀴즈일 경우 endQuiz 호출)
        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentQuizIndex++;
                quizInProgress = true;
                loadQuiz(currentQuizIndex);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    //퀴즈를 종료하고 결과와 포인트를 표시 
    private void endQuiz() {
        quizInProgress = false;

        // 획득 포인트 계산 (정답 수 * 문제당 포인트)
        int rewardPoint = correctCount * POINT_PER_QUIZ;

        // DB에 포인트 적립
        if (userDAO != null) {
            try {
                userDAO.addPointsToUser(userId, rewardPoint);

                // 갱신된 전체 포인트 조회
                int currentPoints = userDAO.getUserPoints(userId);

                messageLabel.setText(String.format("퀴즈 완료! 총 %d점 획득 (현재 누적 포인트: %d점)", rewardPoint, currentPoints));
                messageLabel.setForeground(new Color(0, 100, 0)); 
            } catch (SQLException ex) {
                System.err.println("퀴즈 포인트 적립 DB 오류: " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                    "퀴즈 포인트 적립 중 DB 오류가 발생했습니다. (FK 확인)",
                    "DB 오류",
                    JOptionPane.ERROR_MESSAGE);
                messageLabel.setText("퀴즈 완료! (포인트 적립 실패)");
                messageLabel.setForeground(Color.RED);
            }
        } else {
            // DAO 초기화 실패 처리
            messageLabel.setText(String.format("퀴즈 완료! 총 %d점 획득 (DB 연결 오류)", rewardPoint));
            messageLabel.setForeground(new Color(0, 100, 0));
        }

        // 퀴즈 결과 메시지
        questionLabel.setText("퀴즈 종료!");
        quizGrid.removeAll();

        // 최종 결과 메시지 패널
        JLabel finalMessage = new JLabel(
            String.format("<html><p align='center'>🎉 **퀴즈 종료** 🎉</p><br>총 **%d문제** 중 **%d개 정답!**<br><strong>%d점</strong>을 획득했습니다.</html>", quizList.size(), correctCount, rewardPoint),
            SwingConstants.CENTER);
        finalMessage.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        // 최종 결과 패널
        JPanel resultPanel = new JPanel(new GridBagLayout()); 
        resultPanel.add(finalMessage);

        quizGrid.setLayout(new GridBagLayout()); 
        quizGrid.add(resultPanel);

        quizGrid.revalidate();
        quizGrid.repaint();
    }
}
