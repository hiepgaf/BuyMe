package app.bennsandoval.com.woodmin.models.orders;

import com.google.gson.annotations.SerializedName;

public class Meta {

    private String timezone;
    private String currency;
    @SerializedName("currency_format")
    private String currencyFormat;
    @SerializedName("tax_included")
    private boolean taxIncluded;
    @SerializedName("weight_unit")
    private String weightUnit;
    @SerializedName("dimension_unit")
    private String dimensionUnit;
    @SerializedName("ssl_enabled")
    private boolean sslEnabled;
    @SerializedName("permalinks_enabled")
    private boolean permalinks_enabled;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyFormat() {
        return currencyFormat;
    }

    public void setCurrencyFormat(String currencyFormat) {
        this.currencyFormat = currencyFormat;
    }

    public boolean isTaxIncluded() {
        return taxIncluded;
    }

    public void setTaxIncluded(boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getDimensionUnit() {
        return dimensionUnit;
    }

    public void setDimensionUnit(String dimensionUnit) {
        this.dimensionUnit = dimensionUnit;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public boolean isPermalinks_enabled() {
        return permalinks_enabled;
    }

    public void setPermalinks_enabled(boolean permalinks_enabled) {
        this.permalinks_enabled = permalinks_enabled;
    }
}
