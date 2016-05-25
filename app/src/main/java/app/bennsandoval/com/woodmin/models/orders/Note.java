package app.bennsandoval.com.woodmin.models.orders;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Mackbook on 3/20/16.
 */
public class Note {

    private int id;

    @SerializedName("created_at")
    private Date createdAt;

    private String note;

    @SerializedName("customer_note")
    private boolean customerNote;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(boolean customerNote) {
        this.customerNote = customerNote;
    }
}
