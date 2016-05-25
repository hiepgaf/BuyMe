package app.bennsandoval.com.woodmin.models.orders;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private int id = -1;
    private String subtotal;
    @SerializedName("subtotal_tax")
    private String subtotalTax;
    private String total;
    @SerializedName("total_tax")
    private String totalTax;
    private String price;
    private int quantity;
    @SerializedName("tax_class")
    private String taxClass;
    private String name;
    @SerializedName("product_id")
    private int productId = -1;
    private String sku;
    private List<MetaItem> meta = new ArrayList<>();
    @SerializedName("cogs_cost")
    private String cogsCost;
    @SerializedName("cogs_total_cost")
    private String cogsTotalCost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getSubtotalTax() {
        return subtotalTax;
    }

    public void setSubtotalTax(String subtotalTax) {
        this.subtotalTax = subtotalTax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(String totalTax) {
        this.totalTax = totalTax;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(String taxClass) {
        this.taxClass = taxClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<MetaItem> getMeta() {
        return meta;
    }

    public void setMeta(List<MetaItem> meta) {
        this.meta = meta;
    }

    public String getCogsCost() {
        return cogsCost;
    }

    public void setCogsCost(String cogsCost) {
        this.cogsCost = cogsCost;
    }

    public String getCogsTotalCost() {
        return cogsTotalCost;
    }

    public void setCogsTotalCost(String cogsTotalCost) {
        this.cogsTotalCost = cogsTotalCost;
    }

}
