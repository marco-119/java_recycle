package recycle.DTO;

public class UsersDTO {
	private String userId;
    private String nickname;
    private String password;
    private int totalPoints;
    private int balancePoints;
    private int attendanceStreak;
    private boolean isAdmin;
    
    public UsersDTO() { }
    
    public UsersDTO(String userId, String nickname, String password, int totalPoints, int balancePoints, int attendanceStreak, boolean isAdmin) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.totalPoints = totalPoints;
        this.balancePoints = balancePoints;
        this.attendanceStreak = attendanceStreak;
        this.isAdmin = isAdmin;
    }
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getTotalPoints() {
        return totalPoints;
    }
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public int getBalancePoints() {
        return balancePoints;
    }
    public void setBalancePoints(int balancePoints) {
        this.balancePoints = balancePoints;
    }
    
    public int getAttendanceStreak() {
        return attendanceStreak;
    }
    public void setAttendanceStreak(int attendanceStreak) {
        this.attendanceStreak = attendanceStreak;
    }
    
    public boolean isAdmin() { 
        return isAdmin;
    }
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    //디버깅용
    @Override
    public String toString() {
        return "UsersDTO [아이디=" + userId + ", 닉네임=" + nickname + ", 잔액=" + balancePoints + ", 관리자=" + isAdmin + "]";
    }
}
