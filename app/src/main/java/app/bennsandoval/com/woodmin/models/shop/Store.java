package app.bennsandoval.com.woodmin.models.shop;

import com.google.gson.annotations.SerializedName;

import app.bennsandoval.com.woodmin.models.orders.Meta;

public class Store {

    private String name;
    private String description;
    @SerializedName("URL")
    private String url;
    @SerializedName("wc_version")
    private String wcVersion;
    private Meta meta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWcVersion() {
        return wcVersion;
    }

    public void setWcVersion(String wcVersion) {
        this.wcVersion = wcVersion;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
