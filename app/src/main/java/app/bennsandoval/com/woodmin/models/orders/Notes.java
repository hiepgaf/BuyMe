package app.bennsandoval.com.woodmin.models.orders;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mackbook on 3/20/16.
 */
public class Notes {

    @SerializedName("order_notes")
    private List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
