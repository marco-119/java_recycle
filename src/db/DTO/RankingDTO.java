package db.DTO;


public class RankingDTO {
    
    // DB 쿼리 결과에 맞게 필드 정의
    private final String userId;
    private final String nickname;
    private final int balancePoints; // 랭킹의 기준이 되는 보유 포인트

    public RankingDTO(String userId, String nickname, int balancePoints) {
        this.userId = userId;
        this.nickname = nickname;
        this.balancePoints = balancePoints;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getBalancePoints() {
        return balancePoints;
    }
}