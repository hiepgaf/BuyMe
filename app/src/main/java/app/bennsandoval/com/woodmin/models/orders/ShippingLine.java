package app.bennsandoval.com.woodmin.models.orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mackbook on 4/8/16.
 */
public class ShippingLine {

    @SerializedName("method_id")
    private String methodId;
    @SerializedName("method_title")
    private String methodTitle;
    private String total;

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

}
