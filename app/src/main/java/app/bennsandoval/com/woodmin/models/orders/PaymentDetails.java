package app.bennsandoval.com.woodmin.models.orders;

import com.google.gson.annotations.SerializedName;

public class PaymentDetails {

    @SerializedName("method_id")
    private String methodId;
    @SerializedName("method_title")
    private String methodTitle;
    private boolean paid;

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }
}
