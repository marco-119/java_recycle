package recycle.DTO;

public class ItemsDTO {
    private String itemId;
    private String itemName;
    private String disposalGuide;
    private String categoryId;
    
    public ItemsDTO() { }
    
    public ItemsDTO(String itemId, String itemName, String disposalGuide, String categoryId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.disposalGuide = disposalGuide;
        this.categoryId = categoryId;
    }
    
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getDisposalGuide() {
        return disposalGuide;
    }
    public void setDisposalGuide(String disposalGuide) {
        this.disposalGuide = disposalGuide;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ItemsDTO [품목번호=" + itemId + ", 품목명=" + itemName + ", 분류번호=" + categoryId + "]";
    }
}