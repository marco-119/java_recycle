package db.DTO;

import java.time.LocalDateTime;

public class PointLogDTO {
    
    private final int logId;
    private final String userId;
    private final String type;    
    private final String detail;
    private final int amount;     
    private final LocalDateTime timestamp; 

    public PointLogDTO(int logId, String userId, String type, String detail, int amount, LocalDateTime timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.type = type;
        this.detail = detail;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getLogId() { return logId; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getDetail() { return detail; }
    public int getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
  
    @Override
    public String toString() {
        return "PointLogDTO{" +
                "logId=" + logId +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", detail='" + detail + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}