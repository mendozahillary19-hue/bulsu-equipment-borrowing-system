package edu.bulsu.equipmentborrowing.model;

import java.io.Serializable;

public class Equipment implements Serializable {
    private Integer equipmentId;
    private String itemCode;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String conditionStatus;
    private String defaultAccessories;
    private String imageName;
    private Boolean isFeatured;

    public Equipment() {}

    public Equipment(Integer equipmentId, String itemCode, String name, Integer categoryId,
                     String categoryName, String description, Integer totalQuantity,
                     Integer availableQuantity, String conditionStatus,
                     String defaultAccessories, String imageName, Boolean isFeatured) {
        this.equipmentId = equipmentId;
        this.itemCode = itemCode;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.conditionStatus = conditionStatus;
        this.defaultAccessories = defaultAccessories;
        this.imageName = imageName;
        this.isFeatured = isFeatured;
    }

    public Integer getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(String conditionStatus) { this.conditionStatus = conditionStatus; }

    public String getDefaultAccessories() { return defaultAccessories; }
    public void setDefaultAccessories(String defaultAccessories) { this.defaultAccessories = defaultAccessories; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
}
