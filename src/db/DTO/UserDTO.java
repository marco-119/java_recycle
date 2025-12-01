package db.DTO;


public class UserDTO {
    private final String userId;
    private final String nickname;
    private final int balancePoints;
    private final int totalPoints;
    private final int attendanceStreak; 
    private final boolean isAdmin;      

     
    public UserDTO(String userId, String nickname, int balancePoints, int totalPoints) {
        this(userId, nickname, balancePoints, totalPoints, 0, false);
    }
    
   
    public UserDTO(String userId, String nickname, int balancePoints, int totalPoints, int attendanceStreak, boolean isAdmin) {
        this.userId = userId;
        this.nickname = nickname;
        this.balancePoints = balancePoints;
        this.totalPoints = totalPoints;
        this.attendanceStreak = attendanceStreak;
        this.isAdmin = isAdmin;
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

    public int getTotalPoints() {
        return totalPoints;
    }

 
    public int getAttendanceStreak() {
        return attendanceStreak;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
}