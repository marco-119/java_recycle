package db.DTO;


public class GuideDTO {
    
    // DAO에서 가져온 데이터를 담을 필드를 선언합니다.
    private final String category;
    private final String content;

    //GuideDAO에서 사용하는 생성자
    public GuideDTO(String category, String content) {
        this.category = category;
        this.content = content;
    }

    // 데이터 접근을 위한 Getter 메서드
    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }
}