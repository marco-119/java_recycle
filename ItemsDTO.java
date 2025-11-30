package recycle.DTO;

public class ItemsDTO {
    private String itemId;
    private String itemName;
    private String categoryId;
    private String disposalGuide;
    
    public ItemsDTO() { }
    
    public ItemsDTO(String itemId, String itemName, String categoryId, String disposalGuide) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.categoryId = categoryId;
        this.disposalGuide = disposalGuide;
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
    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getDisposalGuide() {
        return disposalGuide;
    }
    public void setDisposalGuide(String disposalGuide) {
        this.disposalGuide = disposalGuide;
    }

    @Override
    public String toString() {
        return "ItemsDTO [품목번호=" + itemId + ", 품목명=" + itemName + ", 분류번호=" + categoryId + "]";
    }
}
