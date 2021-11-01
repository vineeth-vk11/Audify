package com.audify.CategoriesUI.Models;

public class CategoriesModel {

    String categoryImage, categoryName, isActive;
    String categorySvg;

    public CategoriesModel() {
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getCategorySvg() {
        return categorySvg;
    }

    public void setCategorySvg(String categorySvg) {
        this.categorySvg = categorySvg;
    }
}
