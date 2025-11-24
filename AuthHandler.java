package Login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * users.txt 파일을 읽고 쓰며, 로그인/회원가입 인증을 처리합니다.
 * 파일 형식: id,password,nickname
 */
public class AuthHandler {

    private static final String USER_FILE_PATH = "users.txt";

    /**
     * 사용자가 입력한 ID와 PW로 로그인을 시도합니다.
     * @return 0: 로그인 성공, 1: 비밀번호 틀림, 2: ID 없음
     */
    public static int loginUser(String id, String password) {
        File file = new File(USER_FILE_PATH);
        if (!file.exists()) {
            return 2; // ID 없음 (파일 자체가 없음)
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // id,password,nickname
                if (parts.length >= 2 && parts[0].equals(id)) {
                    // ID 일치
                    if (parts[1].equals(password)) {
                        return 0; // 0: 로그인 성공
                    } else {
                        return 1; // 1: 비밀번호 틀림
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 2; // 2: ID 없음
    }

    /**
     * ID 중복을 확인합니다.
     * @return true: ID가 이미 존재함, false: ID 사용 가능
     */
    public static boolean checkIdDuplicate(String id) {
        File file = new File(USER_FILE_PATH);
        if (!file.exists()) {
            return false; // 파일이 없으므로 중복 아님
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(id)) {
                    return true; // ID가 존재함
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // ID가 존재하지 않음
    }
    
    /**
     * 새 사용자를 등록합니다. (회원가입)
     * @return 0: 회원가입 성공, 1: ID 중복, -1: 오류
     */
    public static int registerUser(String id, String password, String nickname) {
        if (checkIdDuplicate(id)) {
            return 1; // 1: ID 중복
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
            // "id,password,nickname" 형식으로 저장
            writer.write(id + "," + password + "," + nickname);
            writer.newLine();
            
            // 새 유저의 개인 데이터 파일 (포인트) 생성
            UserDataHandler.saveData(id, 0); // 0포인트로 시작
            
            return 0; // 0: 회원가입 성공
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // -1: 파일 쓰기 오류
        }
    }
    
    /**
     * 랭킹을 위해 모든 사용자 ID 목록을 반환합니다.
     */
    public static List<String> getAllUserIDs() {
        List<String> userIDs = new ArrayList<>();
        File file = new File(USER_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    userIDs.add(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userIDs;
    }
}