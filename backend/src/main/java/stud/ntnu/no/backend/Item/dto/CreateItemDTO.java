package stud.ntnu.no.backend.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateItemDTO {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Short description is required")
    private String shortDescription;
    
    @NotBlank(message = "Long description is required")
    private String longDescription;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private double price;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotNull(message = "Location ID is required")
    private Long locationId;
    
    @NotNull(message = "Shipping option ID is required")
    private Long shippingOptionId;
    
    @NotNull(message = "Latitude is required")
    private double latitude;
    
    @NotNull(message = "Longitude is required")
    private double longitude;
    
    @NotBlank(message = "Condition is required")
    private String condition;
    
    @NotBlank(message = "Size is required")
    private String size;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Color is required")
    private String color;
    
    private boolean isVippsPaymentEnabled;
    
    // Constructors
    public CreateItemDTO() {
    }
    
    public CreateItemDTO(String title, String shortDescription, String longDescription, double price, 
                        Long categoryId, Long locationId, Long shippingOptionId, double latitude, double longitude, 
                        String condition, String size, String brand, String color, boolean isVippsPaymentEnabled) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.categoryId = categoryId;
        this.locationId = locationId;
        this.shippingOptionId = shippingOptionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.condition = condition;
        this.size = size;
        this.brand = brand;
        this.color = color;
        this.isVippsPaymentEnabled = isVippsPaymentEnabled;
    }
    
    // Getters and setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public String getLongDescription() {
        return longDescription;
    }
    
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Long getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
    
    public Long getShippingOptionId() {
        return shippingOptionId;
    }
    
    public void setShippingOptionId(Long shippingOptionId) {
        this.shippingOptionId = shippingOptionId;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public boolean isVippsPaymentEnabled() {
        return isVippsPaymentEnabled;
    }
    
    public void setVippsPaymentEnabled(boolean vippsPaymentEnabled) {
        this.isVippsPaymentEnabled = vippsPaymentEnabled;
    }
}