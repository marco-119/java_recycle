package db.DTO;


public class RecycleLogDTO {

    private final int logId;
    private final String logDate;
    private final String itemName;
    private final int quantity;
    private final int points;

    // --- 1. 전체 필드 생성자  ---
    public RecycleLogDTO(int logId, String logDate, String itemName, int quantity, int points) {
        this.logId = logId;
        this.logDate = logDate;
        this.itemName = itemName;
        this.quantity = quantity;
        this.points = points;
    }
    
    // --- 2. Getter 메서드  ---
    
    public int getLogId() {
        return logId;
    }

    public String getLogDate() {
        return logDate;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPoints() {
        return points;
    }
    


    @Override
    public String toString() {
        return "RecycleLogDTO{" +
                "logId=" + logId +
                ", logDate='" + logDate + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", points=" + points +
                '}';
    }
}