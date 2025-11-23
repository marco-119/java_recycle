package recycle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//분리수거 퀴즈 기능을 담당하는 패널 
public class QuizPanel extends JPanel {

    private JLabel questionLabel;
    private JPanel quizGrid;
    private JLabel messageLabel;

    private List<Quiz> quizList; 
    private int currentQuizIndex = 0; 
    private int correctCount = 0; 

    // 퀴즈 항목을 나타내는 내부 클래스 
    private class QuizItem extends JPanel {
        private String answer;

        public QuizItem(String answerText) {
            this.answer = answerText;
            setLayout(new BorderLayout());

            // 이미지 대신 빈 영역
            JPanel imageBox = new JPanel();
            imageBox.setPreferredSize(new Dimension(100, 100)); 
            imageBox.setBackground(Color.LIGHT_GRAY); 

            // 텍스트 라벨
            JLabel textLabel = new JLabel(answerText, SwingConstants.CENTER);
            textLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

            add(imageBox, BorderLayout.CENTER);
            add(textLabel, BorderLayout.SOUTH);
        }
    }
    
    // 퀴즈 데이터를 담는 클래스
    private class Quiz {
        String question;
        String correctAnswer;
        List<String> options;

        public Quiz(String question, String correctAnswer, List<String> wrongOptions) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.options = new ArrayList<>();
            this.options.add(correctAnswer);
            this.options.addAll(wrongOptions);
            Collections.shuffle(this.options); // 보기를 섞음
        }
    }

    public QuizPanel() {
        initializeQuizData();
        
        setLayout(new BorderLayout(10, 10));

        // 1. 퀴즈 질문 라벨 (북쪽)
        questionLabel = new JLabel("퀴즈가 로드될 예정입니다.", SwingConstants.LEFT);
        questionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(questionLabel, BorderLayout.NORTH);

        // 2. 퀴즈 항목 그리드 (중앙)
        quizGrid = new JPanel(new GridLayout(2, 2, 20, 20)); 
        add(quizGrid, BorderLayout.CENTER);

        // 3. 메시지 라벨 (남쪽)
        messageLabel = new JLabel("메시지 영역", SwingConstants.RIGHT);
        messageLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 14));
        messageLabel.setForeground(Color.BLACK);
        
        add(messageLabel, BorderLayout.SOUTH);

        // 첫 번째 퀴즈 로드 
        loadNextQuiz(); 
    }
    
    // 재활용 정보를 바탕으로 5개의 퀴즈를 생성
    private void initializeQuizData() {
        quizList = new ArrayList<>();

        // 퀴즈 1: 일반쓰레기 (빨대)
        quizList.add(new Quiz(
            "다음 중 일반쓰레기로 배출해야 하는 것은?",
            "일반쓰레기 (빨대)",
            List.of("투명 페트병", "플라스틱 (샴푸 용기)", "플라스틱 (일회용 용기)")
        ));
        
        // 퀴즈 2: 종이류 비해당 (벽지)
        quizList.add(new Quiz(
            "다음 중 종이류로 분리배출할 수 없는 것은?",
            "벽지",
            List.of("신문지", "골판지 상자", "노트")
        ));
        
        // 퀴즈 3: 유리병 비해당 (깨진 유리)
        quizList.add(new Quiz(
            "깨진 유리제품의 올바른 배출 방법은?",
            "종량제봉투 (신문지에 싸서)",
            List.of("유리병 수거함", "특수규격마대", "분리수거 안 함")
        ));

        // 퀴즈 4: 비닐류 비해당 (장판)
        quizList.add(new Quiz(
            "다음 중 비닐류 분리수거 대상이 아닌 것은?",
            "장판",
            List.of("1회용 비닐봉투", "라면 봉지 (깨끗하게 헹군)", "과자 봉지 (깨끗하게 헹군)")
        ));

        // 퀴즈 5: 플라스틱 비해당 (칫솔)
        quizList.add(new Quiz(
            "다음 중 플라스틱 분리수거함으로 배출할 수 없는 것은?",
            "칫솔",
            List.of("음료용기 (PET)", "세정용기 (PP)", "플라스틱 트레이")
        ));
        
        Collections.shuffle(quizList); // 퀴즈 순서를 랜덤으로 섞음
    }
    
    // 다음 퀴즈를 로드하거나 퀴즈 종료 처리
    private void loadNextQuiz() {
        quizGrid.removeAll(); // 기존 보기 제거

        if (currentQuizIndex < quizList.size()) {
            Quiz currentQuiz = quizList.get(currentQuizIndex);
            
            // 1. 질문 업데이트
            questionLabel.setText((currentQuizIndex + 1) + ". " + currentQuiz.question);
            
            // 2. 퀴즈 아이템 생성 및 그리드에 추가 
            for (String option : currentQuiz.options) {
                QuizItem item = new QuizItem(option);
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        currentQuizIndex++;
                        loadNextQuiz();
                    }
                });
                quizGrid.add(item);
            }
            
            quizGrid.revalidate();
            quizGrid.repaint();
            
        } else {
            // 퀴즈 종료
            finishQuiz();
        }
    }

    // 퀴즈가 모두 종료되었을 때 
    private void finishQuiz() {
        questionLabel.setText("퀴즈 완료! 총 " + quizList.size() + "문제를 풀었습니다.");
        quizGrid.removeAll();
        
        // 퀴즈 결과 메시지
        JLabel finalMessage = new JLabel("수고하셨습니다", SwingConstants.CENTER);
        finalMessage.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        quizGrid.setLayout(new BorderLayout());
        quizGrid.add(finalMessage, BorderLayout.CENTER);
        
        // 포인트 적립 문구 띄우기
        messageLabel.setText("퀴즈 완료!");
        messageLabel.setForeground(Color.BLACK);

        quizGrid.revalidate();
        quizGrid.repaint();
    }
}