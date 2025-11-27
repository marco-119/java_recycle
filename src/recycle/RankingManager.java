package recycle;

import db.DAO.RankingDAO; 
import db.DAO.RecycleLogDAO; 
import db.DAO.UserDAO; 
import db.DTO.UserDTO; 

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;


public class RankingManager {
    

    public static class RankingDTO {
        private final String userId;
        private final String nickname;
        private final int balancePoints; 
        
        public RankingDTO(String userId, String nickname, int balancePoints) {
            this.userId = userId;
            this.nickname = nickname;
            this.balancePoints = balancePoints;
        }
        
        public String getUserId() { return userId; }
        public String getNickname() { return nickname; }
        public int getBalancePoints() { return balancePoints; }
    }

    private final RankingDAO rankingDAO; 
    private final RecycleLogDAO logDAO;
    private final UserDAO userDAO;

    public RankingManager() {
        try {
            this.rankingDAO = new RankingDAO(); 
            this.logDAO = new RecycleLogDAO(); 
            this.userDAO = new UserDAO(); 
        } catch (Exception e) {
             System.err.println("DAO 초기화 실패: " + e.getMessage());
             throw new RuntimeException("DB 연결 또는 DAO 초기화 실패", e); 
        }
    }

    //모든 사용자의 랭킹 리스트를 가져옵니다.
    public List<RankingDTO> getSortedRankingList() throws SQLException {
        
        // 1. RankingDAO를 통해 정렬된 DB DTO 리스트를 가져옵니다.
        List<db.DTO.RankingDTO> dbRankingList = rankingDAO.getTopRankings();
        
        List<RankingDTO> rankingList = new ArrayList<>();
        
        for (db.DTO.RankingDTO dbDto : dbRankingList) {
             rankingList.add(new RankingDTO(
                 dbDto.getUserId(), 
                 dbDto.getNickname(), 
                 dbDto.getBalancePoints() 
             ));
        }

        // 3. Manager 레벨의 최종 정렬 (포인트 내림차순)
        Collections.sort(rankingList, new Comparator<RankingDTO>() {
            @Override
            public int compare(RankingDTO r1, RankingDTO r2) {
                // 포인트 내림차순 (r2 - r1)
                int pointCompare = r2.getBalancePoints() - r1.getBalancePoints();
                if (pointCompare != 0) {
                    return pointCompare;
                }
                // 동점일 경우 ID 오름차순
                return r1.getUserId().compareTo(r2.getUserId());
            }
        });
        
        return rankingList;
    }
    
    //특정 사용자의 랭킹 정보 (닉네임, 포인트, 순위)를 포맷된 문자열로 반환
    public String getMyRankInfo(String userId, List<RankingDTO> rankingList) {
        int myRank = -1;
        int myPoints = 0;
        String myNickname = userId; 

        for (int i = 0; i < rankingList.size(); i++) {
        	RankingDTO entry = rankingList.get(i);
            
            if (entry.getUserId().equals(userId)) {
                myRank = i + 1; 
                myPoints = entry.getBalancePoints(); 
                myNickname = entry.getNickname();
                break;
            }
        }
        
        String rankStr = (myRank != -1) ? String.valueOf(myRank) : "정보 없음";

        // 전체 랭킹 리스트(TOP N)에 없을 경우 DB에서 사용자 정보를 직접 조회
        if (myRank == -1) {
            try {
                UserDTO userDto = userDAO.getUserById(userId); 
                
                if (userDto != null) {
                    myNickname = userDto.getNickname();
                    myPoints = userDto.getBalancePoints();
                    // TOP N 목록에 없으므로 '목록 밖'으로 표시
                    rankStr = "목록 밖"; 
                } else {
                    myNickname = "알 수 없음";
                    rankStr = "정보 없음";
                }
            } catch (SQLException e) {
                 System.err.println("UserDAO 조회 중 오류 발생: " + e.getMessage());
                 myNickname = "오류";
                 rankStr = "오류";
            }
        }

        return String.format(
            "<html><p align='center'>[내 정보] 닉네임: <strong>%s</strong> (ID: %s) | 현재 포인트: <strong>%d점</strong> | 순위: <strong>%s위</strong></p></html>", 
            myNickname, userId, myPoints, rankStr);
    }
}