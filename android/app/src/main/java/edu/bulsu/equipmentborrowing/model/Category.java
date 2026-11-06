package edu.bulsu.equipmentborrowing.model;

import java.io.Serializable;

public class Category implements Serializable {
    private Integer categoryId;
    private String name;
    private String slug;
    private String iconName;
    private String description;
    private Integer displayOrder;

    public Category() {}

    public Category(Integer categoryId, String name, String slug, String iconName, String description, Integer displayOrder) {
        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
        this.iconName = iconName;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
