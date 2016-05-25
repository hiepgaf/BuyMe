package app.bennsandoval.com.woodmin.models.customers;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Customer {

    private int id;
    @SerializedName("created_at")
    private Date createdAt;
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String username;
    @SerializedName("last_order_id")
    private String lastOrderId;
    @SerializedName("last_order_date")
    private Date lastOrderDate;
    @SerializedName("orders_count")
    private int ordersCount;
    @SerializedName("total_spent")
    private String totalSpent;
    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("billing_address")
    private BillingAddress billingAddress;
    @SerializedName("shipping_address")
    private BillingAddress shippingAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastOrderId() {
        return lastOrderId;
    }

    public void setLastOrderId(String lastOrderId) {
        this.lastOrderId = lastOrderId;
    }

    public Date getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(Date lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    public String getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(String totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public BillingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(BillingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
