package app.bennsandoval.com.woodmin.models.products;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Product {

    private String title;
    private int id;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    private String type;
    private String status;
    private boolean downloadable;
    private boolean virtual;
    private String permalink;
    private String sku;
    private String price;
    @SerializedName("regular_price")
    private String regularPrice;
    @SerializedName("sale_price")
    private String  salePrice;
    @SerializedName("price_html")
    private String priceHtml;
    private boolean taxable;
    @SerializedName("tax_status")
    private String taxStatus;
    @SerializedName("tax_class")
    private String taxClass;
    @SerializedName("managing_stock")
    private boolean managingStock;
    @SerializedName("stock_quantity")
    private int stockQuantity;
    @SerializedName("in_stock")
    private boolean inStock;
    @SerializedName("backorders_allowed")
    private boolean backordersAllowed;
    private boolean backordered;
    @SerializedName("sold_individually")
    private boolean soldIndividually;
    private boolean purchaseable;
    private boolean featured;
    private boolean visible;
    @SerializedName("catalog_visibility")
    private String catalogVisibility;
    @SerializedName("on_sale")
    private boolean onSale;
    private String weight;
    private Dimensions dimensions;
    @SerializedName("shipping_required")
    private boolean shippingRequired;
    @SerializedName("shipping_taxable")
    private boolean shippingTaxable;
    @SerializedName("shipping_class")
    private String shippingClass;
    @SerializedName("shipping_class_id")
    private int shippingClassId;
    private String description;
    @SerializedName("short_description")
    private String shortDescription;
    @SerializedName("reviews_allowed")
    private boolean reviewsAllowed;
    @SerializedName("average_rating")
    private String averageRating;
    @SerializedName("rating_count")
    private int ratingCount;
    @SerializedName("related_ids")
    private List<Integer> relatedIds;
    @SerializedName("upsell_ids")
    private List<Integer> upsellIds;
    @SerializedName("cross_sell_ids")
    private List<Integer> crossSellIds;
    @SerializedName("parent_id")
    private int parentId;
    private List<String> categories;
    private List<String> tags;
    private List<Images> images;
    @SerializedName("featured_src")
    private String featuredSrc;
    private List<Attributes> attributes;
    //"downloads": [],
    @SerializedName("download_limit")
    private int downloadLimit;
    @SerializedName("download_expiry")
    private int downloadExpiry;
    @SerializedName("download_type")
    private String downloadType;
    @SerializedName("purchase_note")
    private String purchaseNote;
    @SerializedName("total_sales")
    private int totalSales;
    private List<Variation> variations;
    //"parent": []
    @SerializedName("cogs_cost")
    private String cogsCost;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getPriceHtml() {
        return priceHtml;
    }

    public void setPriceHtml(String priceHtml) {
        this.priceHtml = priceHtml;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public String getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(String taxStatus) {
        this.taxStatus = taxStatus;
    }

    public String getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(String taxClass) {
        this.taxClass = taxClass;
    }

    public boolean isManagingStock() {
        return managingStock;
    }

    public void setManagingStock(boolean managingStock) {
        this.managingStock = managingStock;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public boolean isBackordersAllowed() {
        return backordersAllowed;
    }

    public void setBackordersAllowed(boolean backordersAllowed) {
        this.backordersAllowed = backordersAllowed;
    }

    public boolean isBackordered() {
        return backordered;
    }

    public void setBackordered(boolean backordered) {
        this.backordered = backordered;
    }

    public boolean isSoldIndividually() {
        return soldIndividually;
    }

    public void setSoldIndividually(boolean soldIndividually) {
        this.soldIndividually = soldIndividually;
    }

    public boolean isPurchaseable() {
        return purchaseable;
    }

    public void setPurchaseable(boolean purchaseable) {
        this.purchaseable = purchaseable;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getCatalogVisibility() {
        return catalogVisibility;
    }

    public void setCatalogVisibility(String catalogVisibility) {
        this.catalogVisibility = catalogVisibility;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public boolean isShippingTaxable() {
        return shippingTaxable;
    }

    public void setShippingTaxable(boolean shippingTaxable) {
        this.shippingTaxable = shippingTaxable;
    }

    public String getShippingClass() {
        return shippingClass;
    }

    public void setShippingClass(String shippingClass) {
        this.shippingClass = shippingClass;
    }

    public int getShippingClassId() {
        return shippingClassId;
    }

    public void setShippingClassId(int shippingClassId) {
        this.shippingClassId = shippingClassId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isReviewsAllowed() {
        return reviewsAllowed;
    }

    public void setReviewsAllowed(boolean reviewsAllowed) {
        this.reviewsAllowed = reviewsAllowed;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<Integer> getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(List<Integer> relatedIds) {
        this.relatedIds = relatedIds;
    }

    public List<Integer> getUpsellIds() {
        return upsellIds;
    }

    public void setUpsellIds(List<Integer> upsellIds) {
        this.upsellIds = upsellIds;
    }

    public List<Integer> getCrossSellIds() {
        return crossSellIds;
    }

    public void setCrossSellIds(List<Integer> crossSellIds) {
        this.crossSellIds = crossSellIds;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public String getFeaturedSrc() {
        return featuredSrc;
    }

    public void setFeaturedSrc(String featuredSrc) {
        this.featuredSrc = featuredSrc;
    }

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }

    public int getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public int getDownloadExpiry() {
        return downloadExpiry;
    }

    public void setDownloadExpiry(int downloadExpiry) {
        this.downloadExpiry = downloadExpiry;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getPurchaseNote() {
        return purchaseNote;
    }

    public void setPurchaseNote(String purchaseNote) {
        this.purchaseNote = purchaseNote;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public List<Variation> getVariations() {
        return variations;
    }

    public void setVariations(List<Variation> variations) {
        this.variations = variations;
    }

    public String getCogsCost() {
        return cogsCost;
    }

    public void setCogsCost(String cogsCost) {
        this.cogsCost = cogsCost;
    }
}
