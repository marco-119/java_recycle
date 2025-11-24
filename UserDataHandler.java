package Login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 사용자의 개인 데이터 파일 (예: userID.txt)을 관리합니다.
 * 주로 포인트를 저장하고 불러옵니다. 
 */
public class UserDataHandler {

    private static String getFilePath(String userID) {
        return userID + ".txt";
    }

    /**
     * 사용자의 포인트를 파일에서 불러옵니다.
     * 파일이 없으면 0을 반환합니다.
     */
    public static int loadData(String userID) {
        File file = new File(getFilePath(userID));
        if (!file.exists()) {
            return 0; // 파일이 없으면 0포인트 시작
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // 파일의 첫 번째 줄에 포인트가 저장되어 있다고 가정
            String line = reader.readLine();
            return Integer.parseInt(line);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0; // 파일 읽기 오류 시 0 반환
        }
    }

    /**
     * 사용자의 현재 포인트를 파일에 덮어쓰기 저장합니다.
     */
    public static void saveData(String userID, int points) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath(userID), false))) { // false: 덮어쓰기
            writer.write(String.valueOf(points));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}