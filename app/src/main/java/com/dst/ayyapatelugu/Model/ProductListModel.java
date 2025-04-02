package com.dst.ayyapatelugu.Model;

public class ProductListModel {

    private String name;
    private String code;
    private String price;
    private String description;
    private String specification;
    private String image;

    public ProductListModel() {

    }

    public ProductListModel(String name, String code, String price, String description, String specification, String image) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.description = description;
        this.specification = specification;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
